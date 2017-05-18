package com.codyy.bennu.sdk.impl;

import android.content.res.Configuration;
import android.util.Log;
import android.view.SurfaceView;

import com.codyy.bennu.dependence.publish.rtmp.MediaEngine;
import com.codyy.bennu.dependence.publish.rtmp.MediaEngineBuilder;
import com.codyy.bennu.dependence.publish.rtmp.MediaParameters;
import com.codyy.bennu.dependence.publish.rtmp.RTMPClient;
import com.codyy.bennu.sdk.BNPublisher;

//import com.codyy.bennu.dependence.publish.gl.SurfaceView;

/*
 * BNPublisher publisher = new BNLivePublisher();
 * publisher.SetClientCallback(this);
 * publisher.SetEngineCallback(this);
 * publisher.SetMediaType(mediaType);
 * publisher.Start(publisherUrl, publisherView);	
 * */

public class BNLivePublisher implements BNPublisher
{
	private final static String TAG = "BNLivePublisher";

	public static final int AUDIOONLY	   = 0x01;
	public static final int VIDEOONLY	   = 0x02;
	public static final int AUDIOANDVIDEO  = 0x03;

	private RTMPClient mClient = null;
	private MediaEngine mMediaEngine = null;
	private MediaEngine.Callback mEngineCallback = null;
	private RTMPClient.Callback  mClientCallback = null;
	private MediaParameters mAudioParameters = null;
	private MediaParameters mVideoParameters = null;
	private int mMediaType = AUDIOANDVIDEO;

	private int mWidth = 640;
	private int mHeight = 480;
	private int mOrientation = Configuration.ORIENTATION_LANDSCAPE;
	private int mCameraId = 1;
	private boolean mRunning = false;//发送服务是否在运行
	private StopDoneListener mStopDoneListener;
	private StartDoneListener mStartedListener;
	private ErrorListener mErrorListener;

	public void setStopDoneListener(StopDoneListener listener) {
		mStopDoneListener = listener;
	}

	private void onStopDone() {
		mRunning = false;
		if (mStopDoneListener != null) {
			mStopDoneListener.stopped();
		}
	}

	public void setStartDoneListener(StartDoneListener listener) {
		mStartedListener = listener;
	}

	private void onStart() {
		mRunning = true;
		if (mStartedListener != null) {
			mStartedListener.started();
		}
	}

	public void setErrorListener(ErrorListener listener) {
		mErrorListener = listener;
	}

	private void onError(int errorCode, String errorMsg) {
		if (mErrorListener != null) {
			mErrorListener.error(errorCode, errorMsg);
		}
	}


	public BNLivePublisher()
	{
		super();
	}

	public void init() {
		if (null != mClient) {
			mClient.release();
			mClient = null;
		}

		mClient = new RTMPClient();

		doLog("Init");
	}

	public void release()
	{
		doLog("Release");
		if (mClient != null) {
			mClient.release();
			mClient = null;
		}
	}

	public void setClientCallback(RTMPClient.Callback callback)
	{
		doLog("SetClientCallback");
		mClientCallback = callback;
	}

	public void setEngineCallback(MediaEngine.Callback callback)
	{
		doLog("SetEngineCallback");
		mEngineCallback = callback;
	}

	public int setMediaType(int mediaType)
	{
		doLog("SetMediaType " + mediaType);
		if (checkMediaType(mediaType) != 0) {
			return -1;
		}

		if (AUDIOONLY == mediaType || AUDIOANDVIDEO == mediaType) {
			// { 5512, 11025, 22050, 44100 }
			mAudioParameters = new MediaParameters(AUDIOONLY);
			mAudioParameters.setValue("samplerate", 16000);
			mAudioParameters.setValue("channel", 1);
			mAudioParameters.setValue("bitDeep", 16);
			mAudioParameters.setValue("bitrate", 64000);
			doLog("Has audioParameters");
		}

		if (VIDEOONLY == mediaType || AUDIOANDVIDEO == mediaType) {
			mVideoParameters = new MediaParameters(VIDEOONLY);
			mVideoParameters.setValue("width", mWidth);
			mVideoParameters.setValue("height", mHeight);
			mVideoParameters.setValue("framerate", 20);
			mVideoParameters.setValue("bitrate", 1024000);
			doLog("Has videoParameters");
		}

		mMediaType = mediaType;

		return 0;
	}

	public MediaParameters getParam(int mediaType)
	{
		if (checkMediaType(mediaType) != 0) {
			return null;
		}

		if (AUDIOONLY == mediaType) {
			return mAudioParameters;
		} else if (VIDEOONLY == mediaType) {
			return mVideoParameters;
		} else {
			return null;
		}
	}

	public int start(String url, SurfaceView surfaceView)
	{
		doLog("Start " + url);

		if (null == mClient) {
			return -1;
		}

		mMediaEngine = MediaEngineBuilder.getInstance()
				.setAudioParameters(mAudioParameters)
				.setVideoParameters(mVideoParameters)
				.setSurfaceView(surfaceView)
				.setCamera(mCameraId, mOrientation)
				.setCallback(mEngineCallback)
				.build();

		mClient.setMediaEngine(mMediaEngine, mMediaType);
		mClient.setServerAddress(url);

		mClientCallback = new RTMPClient.Callback() {
			public void onRtmpUpdate(int message, int messagetype, Exception exception) {
				if (RTMPClient.STOP_DONE == message) {
					onStopDone();
				} else if (RTMPClient.START_DONE == message) {
					onStart();
				} else if (RTMPClient.ERROR_HAPPENED == message) {
					onError(messagetype, "Send Error.");
				}
			}
		};
		mClient.setCallback(mClientCallback);
		mClient.startStream();
		return 0;
	}

	public void stop() {
		doLog("Stop");
		mClient.stopStream();
	}

	public void enableVideo()
	{
		mMediaEngine.enableVideo();
	}

	public void disableVideo() {
		mMediaEngine.disableVideo();
	}

	public void enableAudio()
	{
		mMediaEngine.enableAudio();
	}

	public void disableAudio()
	{
		mMediaEngine.disableAudio();
	}

	public void changeCamera(boolean isChange, int screenOrientation)
	{
		mMediaEngine.changeCamera(isChange, screenOrientation);
	}

	public void setCamera(int cameraId, int screenOrientation)
	{
		mCameraId = cameraId;
		mOrientation = screenOrientation;
	}

	public void setVideoSize(int width, int height)
	{
		mWidth = width;
		mHeight = height;
	}
	//==========================================
	public void deliverAudioPlayData(byte[] data, int length)
	{
		if (mMediaEngine != null) {
			mMediaEngine.deliverAudioPlayData(data, length);
		}
	}

	public boolean isRunning() {
		return mRunning;
	}

	@Override
	public void setAECMFlag(boolean isUse) {
		if (mMediaEngine != null) {
			mMediaEngine.setAecm(isUse);
			mMediaEngine.setNs(isUse);
			mMediaEngine.setAgc(isUse);
		}
	}

	//==========================================

	private int checkMediaType(int mediaType)
	{
		if (mediaType != AUDIOONLY &&  mediaType != VIDEOONLY && mediaType != AUDIOANDVIDEO) {
			doLogErr("");
			return -1;
		}
		return 0;
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
