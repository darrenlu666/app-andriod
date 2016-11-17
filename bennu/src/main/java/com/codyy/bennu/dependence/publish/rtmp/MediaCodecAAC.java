
package com.codyy.bennu.dependence.publish.rtmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import com.codyy.bennu.dependence.publish.rtmp.AudioEngine.AudioQuality;

public class MediaCodecAAC extends AudioEncoder
{	
	private static final String TAG = "MediaCodecAAC";
	private static final boolean VERBOSE = false;  // lots of logging
	
	private static final long kTimeoutUs = 0;
	private static final String AUDIO_MIME_TYPE = "audio/mp4a-latm";

	private Thread t = null;
	private Semaphore sem = null;	// 信号量用于线程间同步
	
	private MediaCodec mEncoder = null;
	private ByteBuffer[] mOutputBuffers = null;
	private ByteBuffer[] mInputBuffers = null;
    // allocate one of these up front so we don't need to do it every time
    private MediaCodec.BufferInfo mBufferInfo;

	private class RawAudioData
	{
		byte[] buf;
		int    len;
		long   ts;
	}
	private List<RawAudioData> mRawList = null;
	private SimpleMemoryPool   rawAudioMemoryPool = null;
	
	private byte[] mEncodedBuffer = new byte[1024];
	
	public MediaCodecAAC(AudioCodec codec)  
	{
		super(codec);
		mRawList = Collections.synchronizedList(new LinkedList<RawAudioData>());
		sem = new Semaphore(0);	
	}
	
	@Override
	protected int configure(AudioQuality audioQuality) 
	{
		// TODO Auto-generated method stub
		rawAudioMemoryPool = new SimpleMemoryPool(50, 5, 2048);
		int samplerate = audioQuality.samplerate;
		int channel = audioQuality.channel;
		int bitrate = audioQuality.bitrate;
		prepareEncoder(samplerate, channel, bitrate);
		mAudioQuality = audioQuality;
		
		if (mEncoder != null)
		{
			mEncoder.start();
			mInputBuffers = mEncoder.getInputBuffers();
			mOutputBuffers = mEncoder.getOutputBuffers();
			mBufferInfo = new MediaCodec.BufferInfo();
			// mInputBuffers=5 mOutputBuffers=4
			Log.d(TAG, "mInputBuffers=" + mInputBuffers.length + " mOutputBuffers=" + mOutputBuffers.length);
		}
		
		if (t == null) 
		{
			t = new Thread(new VidoeEncodeThread());
			t.start();
		}	

		return 0;
	}

	@Override
	protected int encode(byte[] data, int length, long captureTimeMs) 
	{
		putCapturedFrame(data, length, captureTimeMs);
		
		return 0;
	}

	@Override
	protected int close() 
	{
		if (t != null) 
		{
			t.interrupt();
			try 
			{
				t.join();
			} 
			catch (InterruptedException e) {}
			t = null;
		}
		releaseEncoder();
		mRawList.clear();
		rawAudioMemoryPool.stopBuffer();
		return 0;
	}
	
	public void putCapturedFrame(byte[] data, int length, long captureTimeMs) 
	{
		if (data == null || length <= 0) return;
		
		//byte[] buffer = new byte[length];
		byte[] buffer = rawAudioMemoryPool.pollBuffer();
		if (buffer == null) return;
		System.arraycopy(data, 0, buffer, 0, length);
		RawAudioData audioData = new RawAudioData();
		audioData.buf = buffer;
		audioData.len = length;
		audioData.ts  = captureTimeMs;
		mRawList.add(audioData);
		sem.release();
	}
	
	class VidoeEncodeThread implements Runnable 
	{
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			while (!Thread.interrupted()) 
			{
				try 
				{
					sem.acquire(1);
				} 
				catch (InterruptedException e) 
				{
					//e.printStackTrace();
					break;
				}
				encodeFrame(0);
				readAndSendFrame(0);
			}
		}
	}
	
	private int encodeFrame(long timeoutUs)
	{
		if (mRawList.size() > 0)
		{
			RawAudioData audioData = mRawList.get(0);
			int ret = queueInputBuffer(audioData.buf, audioData.len, audioData.ts, timeoutUs);
			if (ret == 0) {
				mRawList.remove(0);
				rawAudioMemoryPool.offerBuffer(audioData.buf);
				return 0;
			}
		}
		return -1;
	}
	
	private void readAndSendFrame(long timeoutUs)
	{
		while (true) {
			int ret = dequeueOutputBuffer(mEncodedBuffer, timeoutUs);
			if (ret < 0) {
				break;
			}
			//sendEncodedData(mEncodedBuffer, ret, mBufferInfo.presentationTimeUs/1000);
		}
	}
	
	// ============================================
	private void prepareEncoder(int samplerate, int channel, int bitrate) 
	{
		try 
		{
			// 选择使用硬件编码器
			MediaCodecInfo codecInfo = selectHardwareCodec(AUDIO_MIME_TYPE);
			if (codecInfo != null) 
			{
				mEncoder = MediaCodec.createByCodecName(codecInfo.getName());
				if (VERBOSE) Log.d(TAG, "found hardware codec: " + codecInfo.getName());
			}
			else
			{
				// select by system
				mEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE);
				codecInfo = mEncoder.getCodecInfo();
				if (VERBOSE) Log.d(TAG, "select software codec: " + codecInfo.getName());
			}
			
			/*
			*  If you fail to set a mandatory key, MediaCodec throws an error because it has been left in an illegal state.
			* */
			MediaFormat mediaFormat =  new MediaFormat();
			mediaFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
			mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
			mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, samplerate);
			mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channel);
			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
			//mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 2048);
 			if (VERBOSE) Log.d(TAG, "format: " + mediaFormat);
 			
			mEncoder.configure(mediaFormat, null/* surface */, null/* crypto */, MediaCodec.CONFIGURE_FLAG_ENCODE);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private void releaseEncoder() 
    {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) 
        {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        } 
    }
    
    private int queueInputBuffer(byte[] data, int length, long captureTimeMs, long timeoutUs)
	{
		int bufferIndex = mEncoder.dequeueInputBuffer(timeoutUs);
		if (bufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER/* -1 */)
		{
			//if (VERBOSE) Log.d(TAG, "input buffer:" + bufferIndex);
			if (data == null)
			{
				mEncoder.queueInputBuffer(bufferIndex, 0, 0, captureTimeMs * 1000, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
			}
			else
			{
				ByteBuffer inputBuffer = mInputBuffers[bufferIndex];
				inputBuffer.clear();
				inputBuffer.put(data);
				/*
				 *  It appears that some devices will drop frames or encode them at low quality if the presentation time stamp isn't set to a reasonable value
				 *  Remember that the time required by MediaCodec is in microseconds. Most timestamps passed around in Java code are in milliseconds or nanoseconds. 
				 * */
				mEncoder.queueInputBuffer(bufferIndex, 0/* offset */, length, captureTimeMs * 1000, 0/* flag */);
			}
			return 0;
		}
		else
		{
			if (VERBOSE) Log.d(TAG, "input buffer not available");
		}
		return -1;
	}
    
	private int dequeueOutputBuffer(byte[] encodedBuffer, long timeoutUs)
	{
		int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, timeoutUs);
		if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) 
		{
			//if (VERBOSE) Log.d(TAG, "no output from encoder available");
		} 
		else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) 
		{
			mOutputBuffers = mEncoder.getOutputBuffers();
			if (VERBOSE) Log.d(TAG, "encoder output buffers changed");
		} 
		else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) 
		{
			// Subsequent data will conform to new format.
			MediaFormat format = mEncoder.getOutputFormat();
			if (VERBOSE) Log.d(TAG, "encoder output format changed: " + format);
		}
		else if (outputBufferIndex < 0)
		{
			Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " + outputBufferIndex);
		}
		else  // outputBufferIndex >= 0
		{
			if (0 == mBufferInfo.size) {
				return -1;
			}

			if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
				if (VERBOSE) Log.d(TAG, "BUFFER_FLAG_CODEC_CONFIG " + mBufferInfo.size);
			}
			else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) {
				if (VERBOSE) Log.d(TAG, "BUFFER_FLAG_KEY_FRAME " + mBufferInfo.size);
			}
			if (VERBOSE) Log.d(TAG, "BUFFER_FRAME " + mBufferInfo.size + "timestamp = " + mBufferInfo.presentationTimeUs);

			ByteBuffer outputBuffer = mOutputBuffers[outputBufferIndex]; 
			if (outputBuffer == null) {
                throw new RuntimeException("mOutputBuffers " + outputBufferIndex + " was null");
            }
			int outBitsSize = mBufferInfo.size;
			//int outPacketSize = outBitsSize + 7; // 7 is ADTS Header size

			outputBuffer.position(mBufferInfo.offset);
			outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);

			byte[] data = new byte[mBufferInfo.size];
			outputBuffer.get(data, 0, mBufferInfo.size);
/*			addADTStoPacket(data, outPacketSize);
			outputBuffer.get(data, 7, outBitsSize);
			outputBuffer.position(mBufferInfo.offset);
*/
			mEncoder.releaseOutputBuffer(outputBufferIndex, false/* render */);
			if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
				if (VERBOSE) Log.d(TAG, "BUFFER_FLAG_END_OF_STREAM");
			}

			sendEncodedData(data, outBitsSize, mBufferInfo.presentationTimeUs/1000);
			return mBufferInfo.size;
		}
		return -1;
	}

	/**
	 *  Add ADTS header at the beginning of each and every AAC packet.
	 *  This is needed as MediaCodec encoder generates a packet of raw
	 *  AAC data.
	 *
	 *  Note the packetLen must count in the ADTS header itself.
	 **/
	private void addADTStoPacket(byte[] packet, int packetLen) {
		int profile = 39;  //2 = AAC LC
		//39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
		int freqIdx = 8;  //8 means 16KHz //44.1KHz
		int chanCfg = 1;  //CPE

		// fill in ADTS data
		packet[0] = (byte)0xFF;
		packet[1] = (byte)0xF9;
		packet[2] = (byte)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>2));
		packet[3] = (byte)(((chanCfg&3)<<6) + (packetLen>>11));
		packet[4] = (byte)((packetLen&0x7FF) >> 3);
		packet[5] = (byte)(((packetLen&7)<<5) + 0x1F);
		packet[6] = (byte)0xFC;
	}
	
	private MediaCodecInfo selectHardwareCodec(String mimeType) 
	{
		List<MediaCodecInfo> mediaCodecs = getCodecInfoForType(mimeType);
		for (MediaCodecInfo codecInfo : mediaCodecs) 
		{
			if (isSoftwareCodec(codecInfo))
			{
				continue;
			}
	   		return codecInfo;
        }
		return null;
	}
	
	private List<MediaCodecInfo> getCodecInfoForType(String mimeType) 
	{
		LinkedList<MediaCodecInfo> codecInfos = new LinkedList<MediaCodecInfo>();
		
		int n = MediaCodecList.getCodecCount();
		for (int i = 0; i < n; ++i) 
		{
			MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
			if (!info.isEncoder()) 
			{
				continue;
			}
			
			String[] supportedTypes = info.getSupportedTypes();
			for (int j = 0; j < supportedTypes.length; ++j) 
			{
				if (supportedTypes[j].equalsIgnoreCase(mimeType)) 
				{
					codecInfos.push(info);
					break;
				}
			}
		}
		return codecInfos;
	}
	
	private boolean isSoftwareCodec(MediaCodecInfo codecInfo) 
	{
		String componentName = codecInfo.getName();
		if (!componentName.startsWith("OMX.") || componentName.startsWith("OMX.google.")) 
		{
			return true;
		}
		
	    return false;
	}
}
