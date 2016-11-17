package com.codyy.bennu.dependence.publish.rtmp;

//import com.codyy.bennu.dependence.publish.gl.SurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import android.view.SurfaceView;

/**
 * 封装了视频编解码器+音频编解码器
 */
public class MediaEngine 
{
	public final static String TAG = "MediaEngine";
	
	private AudioEngine mAudioEngine = null;
	private VideoEngine mVideoEngine = null;
	
	private Callback mCallback;
	private Handler  mMainHandler;
	
	private GstreamerParcel mGstreamerParcel = null;

	/*	added by leitiannet 20150814
	 * 	Using gstreamer to send audio data
	 *  note:must call this function before start
	 * */
	public void useGstremer() 
  	{
		Log.d(TAG, "useGstremer");
		if (mGstreamerParcel == null)
		{
			mGstreamerParcel = new GstreamerParcel();
			mGstreamerParcel.Init();
		}
	}
	/*
	 * 	end 20150814
	 * */
	
	public MediaEngine() 
	{
		mMainHandler = new Handler(Looper.getMainLooper());
		
		// just for test
		// useGstremer();
	}
	
	public void release() 
	{
		removeAudioEngine();
		removeVideoEngine();
	}
	
	public void deliverAudioPlayData(byte[] data, int length)
	{
		if (mAudioEngine != null) {
			mAudioEngine.deliverAudioPlayData(data, length);
		}
	}
	
	public void setAecm(boolean enable) 
  	{
		if (mAudioEngine != null) {
			mAudioEngine.setAecm(enable);
		}
	}

	public void setNs(boolean enable)
	{
		if (mAudioEngine != null) {
			mAudioEngine.setNs(enable);
		}
	}

	public void setAgc(boolean enable) 
	{
		if (mAudioEngine != null) {
			mAudioEngine.setAgc(enable);
		}
	}
	
	public void setClient(RTMPClient client)
	{
		if (mAudioEngine != null) {
			mAudioEngine.setClient(client);
		}
		
		if (mVideoEngine != null) {
			mVideoEngine.setClient(client);
		}
	}
	
	public void setCallback(Callback callback) 
	{
		mCallback = callback;
	}
	
	void addAudioEngine(AudioEngine audioEngine) 
	{
		mAudioEngine = audioEngine;
	}

	void addVideoEngine(VideoEngine videoEngine) 
	{
		mVideoEngine = videoEngine;
	}
	
	void removeAudioEngine() 
	{
		if (mAudioEngine != null) 
		{
			mAudioEngine.release();
			mAudioEngine = null;
		}
	}

	/** You probably don't need to use that directly, use the {@link SessionBuilder}. */
	void removeVideoEngine() 
	{
		if (mVideoEngine != null) 
		{
			//mVideoEngine.stopPreview();
			mVideoEngine = null;
		}
	}
	
	public void setPreviewOrientation(int orientation) 
	{
		if (mVideoEngine != null) 
		{
			//mVideoEngine.setPreviewOrientation(orientation);
		}
	}
	
	public void setSurfaceView(final SurfaceView view) 
	{
		if (mVideoEngine != null) 
		{
			mVideoEngine.setSurfaceView(view);
		}
	}
	
	public boolean isStreaming() 
	{
		if ((mAudioEngine!=null && mAudioEngine.isStreaming()) || (mVideoEngine!=null && mVideoEngine.isStreaming()) )
			return true;
		else 
			return false;
	}
	
	public int syncConfigure() throws Exception
	{
		int ret =0;
		Log.d(TAG,"syncConfigure");
		if (mAudioEngine != null && !mAudioEngine.isStreaming()) {
			ret = mAudioEngine.configure();
			if (ret != 0) return  ret;
		}
		
		if (mVideoEngine != null && !mVideoEngine.isStreaming()) {
			ret = mVideoEngine.configure();
			if (ret != 0) return  ret;
		}
		postSessionConfigured();

		return ret;
	}
	
	public int syncStart()
	{
		int ret =0;
		Log.d(TAG,"syncStart");

		if (mAudioEngine != null && !mAudioEngine.isStreaming()) {
			Log.d(TAG,"mAudioEngine syncStart");
			ret = mAudioEngine.start();
			if (ret != 0)  return ret;
		}

		if (mVideoEngine != null && !mVideoEngine.isStreaming()) {
			Log.d(TAG,"mVideoEngine syncStart");
			ret = mVideoEngine.start();
			if (ret != 0) {
				mAudioEngine.stop();
				return  ret;
			}
		}
		


		postSessionStarted();

		return ret;
	}
	
	public void syncStop() 
	{
		if (mVideoEngine != null)
		{
			mVideoEngine.stop();
		}
		
		if (mAudioEngine != null)
		{
			mAudioEngine.stop();
		}

		postSessionStopped();
	}
	
	private void postSessionConfigured() 
	{
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mCallback != null) {
					mCallback.onSessionConfigured();
				}
			}
		});
	}
	
	private void postSessionStarted() 
	{
		mMainHandler.post(new Runnable() {
			@Override
			public void run() 
			{
				if (mCallback != null) {
					mCallback.onSessionStarted(); 
				}
			}
		});
	}
	
	private void postSessionStopped() 
	{
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mCallback != null) {
					mCallback.onSessionStopped(); 
				}
			}
		});
	}
	
	public void enableVideo()
	{
		mVideoEngine.enable();
	}
	
	public void disableVideo()
	{
		mVideoEngine.disable();
	}
	
	public void enableAudio()
	{
		mAudioEngine.enable();
	}
	
	public void disableAudio()
	{
		mAudioEngine.disable();
	}

	public void changeCamera(boolean isChange, int screenOrientation) { mVideoEngine.changeCamera(isChange, screenOrientation);}

	
	public interface Callback 
	{
		/** Called when some error occurs. */
		void onSessionError(int reason, int streamType, Exception e);

		/** 
		 * Called when the previw of the {@link VideoStream}
		 * has correctly been started.
		 * If an error occurs while starting the preview,
		 * {@link Callback#onSessionError(int, int, Exception)} will be
		 * called instead of {@link Callback#onPreviewStarted()}.
		 */
		void onPreviewStarted();

		/** 
		 * Called when the session has correctly been configured 
		 * after calling {@link Session#configure()}.
		 * If an error occurs while configuring the {@link Session},
		 * {@link Callback#onSessionError(int, int, Exception)} will be
		 * called instead of  {@link Callback#onSessionConfigured()}.
		 */
		void onSessionConfigured();

		/** 
		 * Called when the streams of the session have correctly been started.
		 * If an error occurs while starting the {@link Session},
		 * {@link Callback#onSessionError(int, int, Exception)} will be
		 * called instead of  {@link Callback#onSessionStarted()}. 
		 */
		void onSessionStarted();

		/** Called when the stream of the session have been stopped. */
		void onSessionStopped();
	}
	
}
