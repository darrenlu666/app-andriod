package com.codyy.bennu.dependence.publish.rtmp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.codyy.bennu.sdk.impl.BNAudioMixer;

public class AudioCapture 
{
    final String TAG = "AudioCapture";

	private static final boolean VERBOSE = false;  // lots of logging
    private Thread mThread = null;
	private ByteBuffer mTempBufRec = null;
	private int bufferOffset = 0;
	private int mToReadLen = 0;

    private AudioRecord mAudioRecord = null;
    private final ReentrantLock _recLock = new ReentrantLock();
    
	private MediaCallback mCaptureDataCallback = null;
	private AudioEngine.AudioQuality mAudioQuality = null;
	
	private class RawAudioData
	{
		public byte[] buf;
		public int    len;
	}
	
	private List<RawAudioData> farendAudioList = null;
	private SimpleMemoryPool   farendAudioMemoryPool = null;
	
	// Use WebRTC(APM) to eliminate echo. Handle max 10 ms @ 48 kHz
	private WebRtcAudioProcess audioProcess = null;
	private boolean isAecm = false;
	private boolean isNs   = false;
	private boolean isAgc  = false;
	private boolean isPlaying = false;
	private byte[] processData = null;
	
	
	public AudioCapture() 
    {
        audioProcess = new WebRtcAudioProcess();
        farendAudioList = Collections.synchronizedList(new LinkedList<RawAudioData>());
    }
	
	// ==============================================
	// 回音消除功能
	public void setAecm(boolean enable) 
  	{
		Log.d(TAG, "SetAecm=" + enable);
		this.isAecm = enable;
		audioProcess.SetAecm(enable);
	}

	public void setNs(boolean enable)
	{
		Log.d(TAG, "SetNs=" + enable);
		this.isNs = enable;
		audioProcess.SetNs(enable);
	}

	public void setAgc(boolean enable) 
	{
		Log.d(TAG, "SetAgc=" + enable);
		this.isAgc = enable;
		audioProcess.SetAgc(enable);
	}
	
	// 如果开启回音消除功能，在这里缓存远端数据
	// Receive 10 ms |frame| from player
	public void deliverAudioPlayData(byte[] data, int length)
	{
		if (isAecm || isNs || isAgc) 
		{
			if (isPlaying == false)
			{
				isPlaying = true;
				farendAudioMemoryPool = new SimpleMemoryPool(50, 5, length);
			}
			byte[] buffer = farendAudioMemoryPool.pollBuffer();
			if (buffer == null) return;
			System.arraycopy(data, 0, buffer, 0, length);
			RawAudioData farendAudioData = new RawAudioData();
			farendAudioData.buf = buffer;
			farendAudioData.len = length;
			farendAudioList.add(farendAudioData);
			// doLog("deliverAudioPlayData " + length);
		}
	}
	
    // 如果开启了回音消除功能，在这里对近端数据进行处理
	public void processRecordedDataWithAPM(byte[] data, int length, byte[] outData)
	{
		// 接收到的先缓存起来，在接收线程中送入APM，每次只处理10ms数据
		long count = farendAudioList.size();
		if (count > 0) {
			RawAudioData farendAudioData = farendAudioList.remove(0);
			audioProcess.AnalyzeReverseStream(farendAudioData.buf, farendAudioData.len);
			audioProcess.ProcessStream(data, length, 0, outData);
			farendAudioMemoryPool.offerBuffer(farendAudioData.buf);
		}
	}
	
	// ==============================================
    public synchronized void registerCaptureDataCallback(MediaCallback dataCallBack) 
	{
		if (mCaptureDataCallback == null) {
			mCaptureDataCallback = dataCallBack;
		}
	}

    public int configure(AudioEngine.AudioQuality audioQuality) 
    {
    	int sampleRate = audioQuality.samplerate;
		int bitDeep = audioQuality.bitDeep;
		int channel = audioQuality.channel;
    	mAudioQuality = audioQuality;

		int channelConfig = AudioFormat.CHANNEL_IN_MONO;
		if (channel == 2)
			channelConfig = AudioFormat.CHANNEL_IN_STEREO;

		int audioFormatConfig = AudioFormat.ENCODING_PCM_8BIT;
		if (bitDeep == 16)
			audioFormatConfig = AudioFormat.ENCODING_PCM_16BIT;


        // get the minimum buffer size that can be used
        int minRecBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormatConfig);
        // double size to be more safe
        int recBufSize = minRecBufSize * 2;
        doLog("minRecBufSize=" + minRecBufSize + " recBufSize=" + recBufSize);

        // release the object
        if (mAudioRecord != null) {
        	mAudioRecord.release();
        	mAudioRecord = null;
        }

        try {
        	mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig,
											audioFormatConfig,
				                            recBufSize);
        } catch (Exception e) {
            doLog(e.getMessage());
            return -1;
        }

        // check that the audioRecord is ready to be used
        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            // doLog("rec not initialized " + sampleRate);
            return -1;
        }

		int bitPerTenMillisecond = sampleRate / 100;

		mToReadLen = bitPerTenMillisecond * channel * (bitDeep / 8);

		if (mTempBufRec == null) {
			// allocate more data
			mTempBufRec =  ByteBuffer.allocateDirect(2 * mToReadLen);
			bufferOffset = mTempBufRec.arrayOffset();
		}

        // doLog("rec sample rate set to " + sampleRate);
        audioProcess.InitProcess(sampleRate);
        return 0;
    }

    public int start()
    {
		startRecoding();

		int readed = recordAudio();
		if (readed < 0)
			return -1;

    	mThread = new Thread(new AudioRecordThread());
    	mThread.start();

		return 0;
    }

    public void stop() 
    {
		doLog("+++stop+++");
    	if (mThread != null) {
    		mThread.interrupt();
			try {
				mThread.join();
			} catch (InterruptedException e) {}
			mThread = null;
			stopRecoding();
			mAudioRecord.release();
			mAudioRecord = null;
			if (null != farendAudioMemoryPool) {
				farendAudioMemoryPool.stopBuffer();
			}

			audioProcess.StopProcess();
		}
		doLog("---stop---");
    }
    
	public void release()
	{
		mAudioRecord.release();
		mAudioRecord = null;
	}
    
    private void startRecoding()
    {
    	try {
    		mAudioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    private int stopRecoding()
    {
		 _recLock.lock();
	     try {
	         // only stop if we are recording
	         if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
	             try {
	            	 mAudioRecord.stop();
	             } catch (IllegalStateException e) {
	                 e.printStackTrace();
	                 return -1;
	             }
	         }
	         // release the object
	         //mAudioRecord.release();
	         //mAudioRecord = null;
	     } finally {
	         // Ensure we always unlock, both for success, exception or error
	         // return.
	         _recLock.unlock();
	     }
	     return 0;
    }

    private int recordAudio()
    {
    	int readBytes = 0;
        //_recLock.lock();
        try {
            if (mAudioRecord == null) {
                return -2; // We have probably closed down while waiting for rec lock
            }

			readBytes = mAudioRecord.read(mTempBufRec, mToReadLen);
			if (readBytes == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			} else if (readBytes == AudioRecord.ERROR_BAD_VALUE) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_BAD_VALUE");
			} else if (readBytes == AudioRecord.ERROR_INVALID_OPERATION) {
				throw new IllegalStateException("read() returned AudioRecord.ERROR_INVALID_OPERATION");
			}

            if (readBytes <= 0) {
                return -1;
            }
        } catch (Exception e) {
            doLogErr("RecordAudio try failed: " + e.getMessage());
        } finally {
           // _recLock.unlock();
        }

        return readBytes;
    }

    
    class AudioRecordThread implements Runnable 
	{  
		@Override  
		public void run() 
		{
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

			while (!Thread.interrupted()) {

				int readed = recordAudio();
				if (readed > 0) {
					handleIncomingFrame(mTempBufRec.array(), readed, System.nanoTime()/1000000);
				}
			}
			doLog("Leave AudioRecordThread thread");
		}

	}  
    
    private void handleIncomingFrame(byte[] data, int length, long captureTimeMs)
	{
		if (bufferOffset > 0) {
			//add by liuhao, andriod version greater than 5.0
			data = Arrays.copyOfRange(data, bufferOffset, length + bufferOffset);
		}

		byte[] callbackData = data;

    	if (isPlaying && (isAecm || isNs || isAgc)) {
    		if (processData == null) {
    			processData = new byte[length];
    		}
    		processRecordedDataWithAPM(data, length, processData);

			callbackData = processData;
		}

		if(VERBOSE) doLog("Capture a audio with timestamp = " + captureTimeMs + " size = " + length);
    	
		if (mCaptureDataCallback != null) {
			mCaptureDataCallback.onIncomingCapturedFrame(callbackData, length, captureTimeMs);
		}
	}
    
    private void doLog(String msg)
    {
        Log.d(TAG, msg);
    }

    private void doLogErr(String msg)
    {
        Log.e(TAG, msg);
    }
}

