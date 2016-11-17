package com.codyy.bennu.dependence.publish.rtmp;

import android.content.res.Configuration;

//import com.codyy.bennu.dependence.publish.gl.SurfaceView;
import android.view.SurfaceView;

/**
 * {@link MediaEngine} 配置文件
 * use for 初始化装配各类变量
 */
public class MediaEngineBuilder 
{
	public final static String TAG = "MediaEngineBuilder";
	
	private int mOrientation = Configuration.ORIENTATION_LANDSCAPE;//默认横屏
	private int mCameraId = 1;	//0：后置摄像头 1：前置摄像头
	private SurfaceView mSurfaceView = null;
	private MediaParameters  mVideoParameters = null;
	private MediaParameters  mAudioParameters = null;
	private MediaEngine.Callback mCallback = null;
	
	// The SessionManager implements the singleton pattern
	private static volatile MediaEngineBuilder sInstance = null; 
	
	// Removes the default public constructor
	private MediaEngineBuilder() {}
	
	public final static MediaEngineBuilder getInstance() 
	{
		if (sInstance == null) 
		{
			synchronized (MediaEngineBuilder.class) 
			{
				if (sInstance == null) 
				{
					MediaEngineBuilder.sInstance = new MediaEngineBuilder();
				}
			}
		}
		return sInstance;
	}
	
	public MediaEngineBuilder setCallback(MediaEngine.Callback callback) 
	{
		mCallback = callback;
		return this;
	}
	
	public MediaEngineBuilder setAudioParameters(MediaParameters parameters) 
	{
		mAudioParameters = parameters;
		return this;
	}
	
	public MediaEngineBuilder setVideoParameters(MediaParameters parameters) 
	{
		mVideoParameters = parameters;
		return this;
	}
	
	public MediaEngineBuilder setSurfaceView(SurfaceView surfaceView) 
	{
		mSurfaceView = surfaceView;
		return this;
	}
	
	public MediaEngineBuilder setCamera(int cameraId, int orientation)
	{
		mCameraId = cameraId;
		mOrientation = orientation;
		return this;
	}	
	
	public MediaEngine build() 
	{
		MediaEngine mediaEngine = new MediaEngine();
		mediaEngine.setCallback(mCallback);
		
		if (mAudioParameters != null)
		{
			AudioEngine audioEngine = new AudioEngine();
			audioEngine.setParameters(mAudioParameters);
			mediaEngine.addAudioEngine(audioEngine);
		}
		
		if (mVideoParameters != null)
		{
			VideoEngine videoEngine = new VideoEngine();
			videoEngine.setParameters(mVideoParameters);
			videoEngine.setSurfaceView(mSurfaceView);
			videoEngine.setCamera(mCameraId, mOrientation);
			mediaEngine.addVideoEngine(videoEngine);
		}
		
		return mediaEngine;
	}
}
