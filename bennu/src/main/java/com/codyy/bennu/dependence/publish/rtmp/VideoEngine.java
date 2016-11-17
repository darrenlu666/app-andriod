package com.codyy.bennu.dependence.publish.rtmp;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.util.Log;

//import com.codyy.bennu.dependence.publish.gl.SurfaceView;
import android.view.SurfaceView;
import java.util.concurrent.Semaphore;

public class VideoEngine implements MediaCallback
{ 
	private final static String TAG = "VideoEngine";
	
	// 
	private MediaParameters mVideoParameters = null;
	private VideoQuality mVideoQuality = null;
	
	private VideoCapture mVideoCapture = null;
	private VideoCodec mVideoCodec = null;
	private RTMPClient mRTMPClient = null;
	private boolean mStreaming = false;
	private boolean mCapturedVideo = false;
	private boolean mEnabled = true;
	private int mOrientation = Configuration.ORIENTATION_LANDSCAPE;
	private int mCameraId = 1;
	private byte[] mZeroRAWData;
	private Thread t = null;
	private Semaphore sem = null;	// 信号量用于线程间同步
	private boolean videoCodecLeak = false;
	
	/*	added by leitiannet 20150814
	 * 	Using gstreamer to send audio data
	 * */
	private GstreamerParcel mGstreamerParcel = null;
	public void setGstreamerParcel(GstreamerParcel parcel) 
  	{
		this.mGstreamerParcel = parcel;
	}
	/*
	 * 	end 20150814
	 * */
	
	public VideoEngine()
	{
		mVideoCapture = new VideoCapture();
		mVideoCodec = new VideoCodec(this);
		mVideoCapture.registerCaptureDataCallback(this);
		mVideoCodec.registerEncodedDataCallback(this);
		sem = new Semaphore(0);

	}
	
	public boolean isStreaming()
	{
		return mStreaming;
	}
	
	public void enable()
	{
		mEnabled = true;
	}
	
	public void disable()
	{
		mEnabled = false;
	}

	public synchronized void setClient(RTMPClient client)
	{
		mRTMPClient = client;
	}
	
	public synchronized void setSurfaceView(SurfaceView view) 
	{
		mVideoCapture.setSurfaceView(view);
	}
	
	public synchronized void setParameters(MediaParameters parameters)
	{
		mVideoParameters = parameters;
	}

	public void setCamera(int cameraId, int orientation) {
		mCameraId = cameraId;
		mOrientation = orientation;
	}

	public void changeCamera(boolean isChange, int screenOrientation) {
		int cameraId = mCameraId;
		if (isChange) {
			if (1 == mCameraId) {
				cameraId = 0;
			} else {
				cameraId = 1;
			}
		}
		mCameraId = cameraId;

		if (screenOrientation == mOrientation) {
			Log.d(TAG, "Not change orientation.");
			mVideoCapture.changeCamera(isChange, screenOrientation);
		} else {
			Log.d(TAG, "Change orientation.");
			stop();
			mOrientation = screenOrientation;
			try {

				configure();
			} catch (Exception e) {
				Log.d(TAG,"configure failed:" + e.getMessage());
			}

			start();
		}
	}
	
	public int configure() throws Exception
	{
		if (mStreaming) throw new IllegalStateException("Can't be called while streaming.");
		
		if (mVideoParameters.getValue("width") == null || mVideoParameters.getValue("height") == null ||
			mVideoParameters.getValue("framerate") == null || mVideoParameters.getValue("bitrate") == null)
		{
			throw new Exception("Video parameters illegal.");
		}
		
		int width = Integer.parseInt(mVideoParameters.getValue("width").toString());
		int height = Integer.parseInt(mVideoParameters.getValue("height").toString());
		int framerate = Integer.parseInt(mVideoParameters.getValue("framerate").toString());
		int bitrate = Integer.parseInt(mVideoParameters.getValue("bitrate").toString());
		//int orientation = mOrientation;//// fixed
		//int orientation = Configuration.ORIENTATION_LANDSCAPE;
		mVideoQuality = new VideoQuality(width, height, framerate, bitrate, mCameraId, mOrientation);
		Log.d(TAG, "configure width=" + width + " height=" + height + " framerate=" + framerate);

		if(mVideoCapture.configure(mVideoQuality) == -1)
			return RTMPClient.MESSAGE_ERROR_CONFIG_VIDEO_CAPTURE;

		if (mVideoCodec.configure(mVideoQuality) == -1)
			return RTMPClient.MESSAGE_ERROR_CONFIG_VIDEO_CODEC;

		int bufSize = mVideoQuality.width * mVideoQuality.height * ImageFormat.getBitsPerPixel(VideoCapture.mCameraImageFormat) / 8;
		mZeroRAWData = new byte[bufSize];

		return 0;
	}
	
	public int start()
	{
		if (!mStreaming) {
			mVideoCodec.start();

			if (mVideoCapture.start() == -1) {
				mVideoCodec.stop();
				return RTMPClient.MESSAGE_ERROR_START_OPEN_CAMERA;
			}
		}
		mStreaming = true;

		if (t == null) {
			t = new Thread(new VideoEngineDetectionThread());
			t.start();
		}
		return 0;
	}
	
	public void stop()
	{
		if (mStreaming) {
			mVideoCapture.stop();
			mVideoCodec.stop();
		}

		mStreaming = false;

		sem.release();
		if (t != null) {
			t.interrupt();
			try {
				t.join();
			} catch (InterruptedException e) {}
			t = null;
		}
	}
	
	public void release()
	{
		mVideoCapture.release();
		mVideoCodec.release();
	}

	public void videoCodecLeak()
	{
		videoCodecLeak = true;
		sem.release();
	}

	class VideoEngineDetectionThread implements Runnable
	{
		@Override
		public void run() {
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			while (!Thread.interrupted()) {

					try {
						sem.acquire(1);

						if(!mStreaming)
							break;

						if (videoCodecLeak) {
							mRTMPClient.postError(RTMPClient.ERROR_HAPPENED, RTMPClient.MESSAGE_WARNING_VIDEO_ENCODE_CAPACITY_LACKING, null);
							videoCodecLeak = false;
						}

					} catch (InterruptedException e) {
						break;
					}

			}
			Log.d(TAG, "Leave VideoEngine Detection thread");
		}
	}


	@Override
	public void onIncomingCapturedFrame(byte[] data, int length, long captureTimeMs) 
	{
		/*	added by leitiannet 20150814
		 * 	Using gstreamer to send audio data
		 * */
		if (mGstreamerParcel != null) {
			mGstreamerParcel.PushVideo(data, length, captureTimeMs);
			return;
		}


		/*
		 * 	end 20150814
		 * */
		
		if (!mCapturedVideo && mRTMPClient != null) {
			mCapturedVideo = true;
			mRTMPClient.setVideoInit();
			mRTMPClient.SetVideoParams(mVideoQuality.width, mVideoQuality.height, mVideoQuality.framerate);
		}
		
		/*Add by sparktend.*/
		if (!mEnabled) {
			data = mZeroRAWData;
		}

		mVideoCodec.putCapturedFrame(data, length, captureTimeMs);	
	}
	
	@Override
	public void onIncomingEncodedFrame(byte[] data, int length, long captureTimeMs) 
	{
		if (mRTMPClient != null) {
			mRTMPClient.putVideoEncodedFrame(data, 0, length, captureTimeMs);
		}
	}
	
	// =============================================================
	public class VideoQuality 
	{
		public final static String TAG = "VideoQuality";
		
		public int width 		= 0;
		public int height 	 	= 0;
		public int framerate 	= 0;
		public int bitrate 		= 0;
		public int orientation 	= 0;
		public int cameraId     = 1;

		public VideoQuality() {}

		public VideoQuality(int width, int height) 
		{
			this.width = width;
			this.height = height;
		}	

		public VideoQuality(int width, int height, int framerate, int bitrate, int cameraId, int orientation)
		{
			this.width = width;
			this.height = height;
			this.framerate = framerate;
			this.bitrate = bitrate;
			this.cameraId = cameraId;
			this.orientation = orientation;
		}

		public boolean equals(VideoQuality quality) 
		{
			if (quality == null) return false;
			return (quality.width == this.width &
					quality.height == this.height &
					quality.framerate == this.framerate	&
					quality.bitrate == this.bitrate);
		}

		public VideoQuality clone() 
		{
			return new VideoQuality(width, height, framerate, bitrate, cameraId, orientation);
		}
		
		public String toString() 
		{
	        return "width=" + width + ", height=" + height + ", framerate=" + framerate + ", bitrate=" + bitrate + ", orientation=" + orientation;
	    }
		
	}

}
