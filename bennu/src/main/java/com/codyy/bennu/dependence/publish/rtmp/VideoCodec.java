
package com.codyy.bennu.dependence.publish.rtmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

//http://blog.csdn.net/liuhongxiangm/article/details/17584303
@SuppressLint("InlinedApi")
public class VideoCodec
{	
	private static final String TAG = "VideoCodec";
	private static final boolean VERBOSE = false;  // lots of logging
	
	private static final long kTimeoutUs = 0;
	private static final String VIDEO_MIME_TYPE = "video/avc";

	private Thread t = null;
	private Semaphore sem = null;	// 信号量用于线程间同步

	private VideoEngine mVideoEngine = null;
	private MediaCodec mEncoder = null;
	private ByteBuffer[] mOutputBuffers = null;
	private ByteBuffer[] mInputBuffers = null;
    // allocate one of these up front so we don't need to do it every time
    private MediaCodec.BufferInfo mBufferInfo;

	private VideoEngine.VideoQuality mVideoQuality = null;
	private MediaCallback mEncodedDataCallback = null;

	//for compute frameRate
	private long lastCaptureTimeMs = 0;
	private long durationTimePerSecond = 0;
	private int statisticalFrameCount = 0;

	//for encode capacity analyze
	private int countNum = 0;
	private int wastedEncoderTimes = 0;
	private int wastedHappendTimes = 0;
	private boolean wastedHappendPerSecond = false;
	private int wasetedThreshold = 0;


	private SimpleMemoryPool   rawVideoMemoryPool = null;
	
	private final static int NAULLENGTH = 720 * 1280 * 3 / 2;
	private byte[] mEncodedBuffer = null;
	private boolean isEos = false;
	private boolean isFrameComing = false;

	public VideoCodec(VideoEngine videoEngine)
	{
		mVideoEngine = videoEngine;
		sem = new Semaphore(0);
	}
	
	public synchronized void registerEncodedDataCallback(MediaCallback dataCallBack) 
	{
		if (mEncodedDataCallback == null) {
			mEncodedDataCallback = dataCallBack;
		}
	}

	public void FrameRateCompute(long captureTimeMs)
	{
		int computedFrameRate = 0;

		statisticalFrameCount ++ ;
		if (lastCaptureTimeMs != 0) {
			long FrameDuration = captureTimeMs - lastCaptureTimeMs;
			durationTimePerSecond += FrameDuration;
			if (durationTimePerSecond > 1000) {
				computedFrameRate = statisticalFrameCount -1 ;
				Log.d(TAG, " the actual framerate is " + computedFrameRate + ", and duration is " + durationTimePerSecond);

				durationTimePerSecond = 0;
				statisticalFrameCount = 0;
			}
		}

		lastCaptureTimeMs = captureTimeMs;
	}

	public void putCapturedFrame(byte[] data, int length, long captureTimeMs)
	{
		if (data == null || length <= 0) return;

		if(VERBOSE)FrameRateCompute(captureTimeMs);

		queueInputBuffer(data, length, captureTimeMs, 0);

		if (!isFrameComing) {
			sem.release();
		}
	}
	
	public int configure(VideoEngine.VideoQuality videoQuality)
	{
		int ret = 0;
		Log.d(TAG, "configure() " + videoQuality.toString());

		int bufSize = videoQuality.width * videoQuality.height * ImageFormat.getBitsPerPixel(VideoCapture.mCameraImageFormat) / 8;
		rawVideoMemoryPool = new SimpleMemoryPool(10, 3, bufSize);
		mEncodedBuffer = new byte[bufSize];

		int width = videoQuality.width;
		int height = videoQuality.height;
		int bitrate = videoQuality.bitrate;
		int framerate = videoQuality.framerate;
		// 需要对图像进行旋转90
		if (videoQuality.orientation == Configuration.ORIENTATION_PORTRAIT) {
			width = videoQuality.height;
			height = videoQuality.width;
		}
		ret = prepareEncoder(width, height, framerate, bitrate);
		mVideoQuality = videoQuality;
		if (framerate > 0)
			wasetedThreshold = (1000 /framerate) *2;

		return ret;
	}
	
	public void start() 
	{
		Log.d(TAG, "start()");
		
		if (mEncoder != null)
		{
			mEncoder.start();
			mInputBuffers = mEncoder.getInputBuffers();
			mOutputBuffers = mEncoder.getOutputBuffers();
			mBufferInfo = new MediaCodec.BufferInfo();
			// mInputBuffers=5 mOutputBuffers=4
			Log.d(TAG, "mInputBuffers=" + mInputBuffers.length + " mOutputBuffers=" + mOutputBuffers.length);
		}
		
		if (t == null) {
			t = new Thread(new VidoeEncodeThread());
			t.start();
		}	
	}
	
	public void stop()
	{
		Log.d(TAG, "+++stop+++");

		queueInputBuffer(null, 0, 0, 0);//send eos to encoder
		if (t != null) {
			t.interrupt();
			try {
				t.join();
			} catch (InterruptedException e) {}
			t = null;
		}

		if (mEncoder != null) {
            mEncoder.stop();
        } 

		rawVideoMemoryPool.stopBuffer();

		releaseEncoder();

		Log.d(TAG, "---stop---");
	}

	/*Not used.*/
	public void release()
	{
		//stop();
		//releaseEncoder();
	}
	
	class VidoeEncodeThread implements Runnable 
	{
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			while (!Thread.interrupted()) {

				if (!isFrameComing)// just need to wait for one time
				{
					try {
						sem.acquire(1);
					} catch (InterruptedException e) {
						break;
					}
					isFrameComing = true;
				}

				readAndSendFrame(-1);

				if (isEos)
					break;
			}
			Log.d(TAG, "Leave video encode thread");
		}
	}
	
	private void readAndSendFrame(long timeoutUs)
	{
		while (true) {
			int ret = dequeueOutputBuffer(mEncodedBuffer, timeoutUs);
			if (ret < 0) {
				break;
			}
			sendEncodedData(mEncodedBuffer, ret, mBufferInfo.presentationTimeUs/1000);
		}
	}
	
	private void sendEncodedData(byte[] data, int length, long captureTimeMs)
	{
		if (mEncodedDataCallback != null) {
			mEncodedDataCallback.onIncomingEncodedFrame(data, length, captureTimeMs);
		}
	}
	
	// ============================================
	// https://android.googlesource.com/platform/cts/+/jb-mr2-release/tests/tests/media/src/android/media/cts/EncoderTest.java
	private int prepareEncoder(int width, int height, int framerate, int bitrate)
	{
		try {
			// 选择使用硬件编码器
			if (mEncoder != null){
				mEncoder.release();
				mEncoder = null;
			}

			MediaCodecInfo codecInfo = selectHardwareCodec(VIDEO_MIME_TYPE);
			if (codecInfo != null) {
				mEncoder = MediaCodec.createByCodecName(codecInfo.getName());//OMX.google.h264.encoder

				if (VERBOSE) Log.d(TAG, "found hardware codec: " + codecInfo.getName());
			} else {
				// select by system
				mEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
				//codecInfo = mEncoder.getCodecInfo();
				//if (VERBOSE) Log.d(TAG, "select software codec: " + codecInfo.getName());
			}

			if (mEncoder == null)
				return  -1;
			
			int colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
			if(!supportColorFormat(codecInfo, VIDEO_MIME_TYPE, colorFormat)){
				colorFormat = selectColorFormat(codecInfo, VIDEO_MIME_TYPE);
			}

			String manufacturer = android.os.Build.MODEL;// 获取手机厂商
			if (manufacturer.equalsIgnoreCase("MI 3") )
				colorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;

			if (VERBOSE) Log.d(TAG, "color format: %d" + colorFormat);
			/*
			*  If you fail to set a mandatory key, MediaCodec throws an error because it has been left in an illegal state.
			* */
 			MediaFormat mediaFormat = MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, width, height);
 			mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, colorFormat);
 			mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, framerate); // 15?
 			mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1); // 10?
 			mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate); // 
 			//mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
 			if (VERBOSE) Log.d(TAG, "format: " + mediaFormat);

			try{
				mEncoder.configure(mediaFormat, null/* surface */, null/* crypto */, MediaCodec.CONFIGURE_FLAG_ENCODE);
			}
			catch (IllegalStateException e) {
				return  -1;
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
    private void releaseEncoder() 
    {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.release();
            mEncoder = null;
        } 
    }
    
    private int queueInputBuffer(byte[] data, int length, long captureTimeMs, long timeoutUs)
	{
		int bufferIndex = mEncoder.dequeueInputBuffer(timeoutUs);
		if (bufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER/* -1 */) {
			//if (VERBOSE) Log.d(TAG, "input buffer:" + bufferIndex);
			if (data == null) {
				mEncoder.queueInputBuffer(bufferIndex, 0, 0, captureTimeMs * 1000, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
			} else {
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
		} else {
			if (VERBOSE) Log.d(TAG, "input buffer not available");
		}
		return -1;
	}

	private void encoderCapacityDetection(long before)
	{
		long after = System.nanoTime()/1000000;
		long waitedTime = after - before;

		countNum ++;

		//Log.d(TAG, "waited Time for getting encoded data is " + waitedTime);

		if (wasetedThreshold > 0 && waitedTime > wasetedThreshold)//waited time to get encoder data
			wastedEncoderTimes ++;

		if (mVideoQuality.framerate > 0 && countNum == mVideoQuality.framerate){

			if (VERBOSE) Log.d(TAG, "DstFramerate is " + mVideoQuality.framerate + ", Threshold is " + wasetedThreshold +", wastedEncoderTimes is " + wastedEncoderTimes);
			if (wastedEncoderTimes > 0 && mVideoQuality.framerate / wastedEncoderTimes <= 4)
				wastedHappendPerSecond = true;
			else
				wastedHappendPerSecond = false;

			wastedEncoderTimes = 0;
			countNum = 0;

			//Log.d(TAG, "wastedHappendPerSecond is " + wastedHappendPerSecond + ", wastedHappendTimes is " + wastedHappendTimes);
			if (wastedHappendPerSecond)
				wastedHappendTimes ++;
			else
				wastedHappendTimes = 0;
		}

		if (wastedHappendTimes >= 5){
			wastedHappendTimes = 0;
			mVideoEngine.videoCodecLeak();
		}
	}

	private int dequeueOutputBuffer(byte[] encodedBuffer, long timeoutUs)
	{
		long before = System.nanoTime()/1000000;
		int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, timeoutUs);

		encoderCapacityDetection(before);

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
			//if (VERBOSE) Log.d(TAG, "output buffer:" + outputBufferIndex);
			ByteBuffer outputBuffer = mOutputBuffers[outputBufferIndex]; 
			if (outputBuffer == null) 
			{
                throw new RuntimeException("mOutputBuffers " + outputBufferIndex + " was null");
            }
			
			if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) 
			{
				// sps和pps [0, 0, 0, 1, 103, 66, -128, 30, -38, 2, -128, -10, -128, 109, 10, 19, 80, 0, 0, 0, 1, 104, -50, 6, -30]
				if (VERBOSE) Log.d(TAG, "BUFFER_FLAG_CODEC_CONFIG " + mBufferInfo.size);
			}
			else if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_KEY_FRAME) != 0) 
			{	
				if (VERBOSE) Log.d(TAG, "BUFFER_FLAG_KEY_FRAME " + mBufferInfo.size);
			}
			
			if (mBufferInfo.size != 0)
			{
				outputBuffer.position(mBufferInfo.offset);
            	outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);	
				outputBuffer.get(encodedBuffer, 0, mBufferInfo.size);
			}
			
			mEncoder.releaseOutputBuffer(outputBufferIndex, false/* render */); 
			
			if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) 
			{
				if (VERBOSE) Log.d(TAG, "BUFFER_FLAG_END_OF_STREAM");
				isEos = true;
				return  -1;
			}
			return mBufferInfo.size;
		}
		return -1;
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
	
	private int selectColorFormat(MediaCodecInfo codecInfo, String mimeType) 
	{
		if (codecInfo != null)
		{
			MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
			for (int i = 0; i < capabilities.colorFormats.length; i++) 
			{
				int colorFormat = capabilities.colorFormats[i];
				if (isRecognizedFormat(colorFormat)) 
				{
					return colorFormat;
				}
			}
		}
		return 0;   // not reached
	}

	private boolean supportColorFormat(MediaCodecInfo codecInfo, String mimeType, int colorFormat)
	{
		if (codecInfo != null)
		{
			MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
			for (int i = 0; i < capabilities.colorFormats.length; i++)
			{
				if(capabilities.colorFormats[i] == colorFormat)
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRecognizedFormat(int colorFormat) 
	{
		switch (colorFormat) 
		{
			// these are the formats we know how to handle for this test
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
			case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
			case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
				return true;
			default:
				return false;
		}
	}
	
	private boolean isSoftwareCodec(MediaCodecInfo codecInfo) 
	{
		String componentName = codecInfo.getName();
		if (!componentName.startsWith("OMX.") || componentName.startsWith("OMX.google.")) {
			return true;
		}
		
	    return false;
	}
}
