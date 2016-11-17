package com.codyy.bennu.dependence.publish.rtmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

//import com.codyy.bennu.dependence.publish.gl.SurfaceView;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class VideoCapture implements SurfaceHolder.Callback
{
	private final static String TAG = "VideoCapture";
	private static final boolean VERBOSE = false;  // lots of logging
	private VideoEngine.VideoQuality mVideoQuality = null;
	
	public static final int mCameraImageFormat = ImageFormat.NV21;//不支持YV12?
	private int numCaptureBuffers = 3;
	private int mCurrentCameraId = CameraInfo.CAMERA_FACING_FRONT;
	private Camera mCamera = null;  
	private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null;
	private boolean mPreviewStarted = false;
	//private Handler  mHandler = null;
	//
	private MediaCallback mCaptureDataCallback = null;

	// process video frame
	private WebRTCVideoProcess mWebRtcVideoProcess = null;
	private ByteBuffer mOutBuffer = null;
	private int bufferOffset = 0;

	//for framerate compute
	private long lastCaptureTimeMs = 0;
	private long durationTimePerSecond = 0;
	private int statisticalFrameCount = 0;

	//for framerate control
	private boolean bFrameRateControl = true;
	private int frameCount = 0;
	private long tickCount = 0;
	private int dropFrameIntelval = -1;
	private boolean dropFrameJitter = false;

	public VideoCapture()
	{
		this(CameraInfo.CAMERA_FACING_FRONT);
	}
	
	public VideoCapture(int cameraId) 
	{
		/*
		final Semaphore signal = new Semaphore(0);
		new HandlerThread("com.codyy.bennu.dependence.publish.rtmp.VideoCapture"){
			@Override
			protected void onLooperPrepared() 
			{
				mHandler = new Handler();
				signal.release();
			}
		}.start();
		signal.acquireUninterruptibly();
		*/
		setCamera(cameraId);
		mWebRtcVideoProcess = new WebRTCVideoProcess();
	}
	
	public int setCamera(int cameraId)
	{
		CameraInfo cameraInfo = new CameraInfo();
		int numberOfCameras = Camera.getNumberOfCameras();

		if (numberOfCameras == 0)
			return -1;

		for (int i=0;i<numberOfCameras;i++) 
		{
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == cameraId) 
			{
				mCurrentCameraId = i;
				break;
			}
		}

		return 0;
	}
	
	public int getCamera() 
	{
		return mCurrentCameraId;
	}
	
	public void changeCamera(boolean isChange, int screenOrientation) throws RuntimeException
	{
		if(mCamera == null){
			Log.e(TAG , "Camera is already stop , exchange failed !");
			return;
		}

		if (isChange) {
			stop();
			if (isChange) {
				if (Camera.getNumberOfCameras() == 1)
					throw new IllegalStateException("Phone only has one camera !");
				int cameraId = (mCurrentCameraId == CameraInfo.CAMERA_FACING_BACK) ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK;
				setCamera(cameraId);
			}
			if (screenOrientation != mVideoQuality.orientation) {
				mVideoQuality.orientation = screenOrientation;
			}
			start();
		}

	}
	
	public synchronized void registerCaptureDataCallback(MediaCallback dataCallBack) 
	{
		if (mCaptureDataCallback == null)
		{
			mCaptureDataCallback = dataCallBack;
		}
	}
	
	public synchronized void setSurfaceView(SurfaceView surfaceView) 
	{
		Log.d(TAG,"setSurfaceView");
		mSurfaceView = surfaceView;
		mSurfaceHolder = mSurfaceView.getHolder();
		//mSurfaceHolder.setKeepScreenOn(true);
		if (mSurfaceHolder != null)
		{
			mSurfaceHolder.addCallback(this);
			mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
		}
	}
	
	public synchronized int configure(VideoEngine.VideoQuality videoQuality)
	{
		mVideoQuality = videoQuality;
		return setCamera(mVideoQuality.cameraId);
	}
	
	public synchronized int start()
	{
		Log.d(TAG, "start Camera");

		try{
			mCamera = Camera.open(mCurrentCameraId);
		}catch (Exception e){
			Log.d(TAG, "open camera failed");
			return -1;
		}
		//testCameraParameters();

		Camera.Parameters parameters = mCamera.getParameters();
		//parameters.setRecordingHint(true);
		int[] max = determineMaximumSupportedFramerate(parameters);
		/*For bug in this function.*/
		//determineClosestSupportedResolution(parameters, mVideoQuality);
		/*
		//I found some device not support set max[0] == max[1], such as Huawei honor.
		int frameRate = mVideoQuality.framerate * 1000;
		if (frameRate > max[0] && frameRate < max[1]) {
			max[0] = max[1] = frameRate;
		}
		*/
		parameters.setPreviewFpsRange(max[0], max[1]);
		parameters.setPreviewSize(mVideoQuality.width, mVideoQuality.height);
		parameters.setPreviewFormat(mCameraImageFormat);
		// 横竖屏镜头自动调整
        if (mVideoQuality.orientation == Configuration.ORIENTATION_PORTRAIT) {
	        parameters.set("orientation", "portrait"); //
	        mCamera.setDisplayOrientation(90); // 在2.2以上可以使用
        } else {// 如果是横屏
	        parameters.set("orientation", "landscape"); //
	        mCamera.setDisplayOrientation(0); // 在2.2以上可以使用
        } 
		mCamera.setParameters(parameters);	
		//mCamera.setDisplayOrientation(mVideoQuality.orientation);
		startPreview();

		return  0;
	}
	
	public synchronized void stop() 
	{
		Log.d(TAG, "+++stop Camera+++");
		if (mCamera == null) 
		{
			throw new RuntimeException("Camera is already stopped!");
		}
		destroyCamera();
		Log.d(TAG, "---stop Camera---");
	}
	
	public void release()
	{
		//destroyCamera();
	}
	
	private Runnable mPreviewCallback = new Runnable() {
		@Override
		public void run() 
		{
			//mHandler.postDelayed(mPreviewCallback, 1000/mVideoQuality.framerate);
			mCamera.setOneShotPreviewCallback(new PreviewCallback() {
				
				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					if (data != null)
					{
						handleIncomingFrame(data, data.length, System.currentTimeMillis());
					}
					camera.addCallbackBuffer(data);
				}
			});
		}
	};
	
	public synchronized void startPreview() 
	{
		if (!mPreviewStarted)
		{
			if (mSurfaceView == null)
			{
				throw new IllegalStateException("setSurfaceView() has not been called !");
			}
			
			if (mCamera != null)
			{
				try {
					//Log.d(TAG, "Before setPreviewDisplay");
					mCamera.setPreviewDisplay(mSurfaceHolder);
					//Log.d(TAG, "After setPreviewDisplay");
				} catch (IOException e) {
					e.printStackTrace();
				}

				// 设置视频帧回调接口

				int bufSize = mVideoQuality.width * mVideoQuality.height * ImageFormat.getBitsPerPixel(mCameraImageFormat) / 8;
				for (int i = 0; i < numCaptureBuffers; i++) 
				{
					mCamera.addCallbackBuffer(new byte[bufSize]);
				}
				//
				//mHandler.post(mPreviewCallback);
				
				mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
					
					@Override
					public void onPreviewFrame(byte[] data, Camera camera) {
						if (data != null)
						{
							handleIncomingFrame(data, data.length, System.nanoTime()/1000000);
						}
						camera.addCallbackBuffer(data);
					}
				});

			}
			mCamera.startPreview();
			mPreviewStarted = true;
		}	
	}
	
	public synchronized void stopPreview() 
	{
		if (mPreviewStarted)
		{
			mCamera.stopPreview();
			mCamera.setPreviewCallbackWithBuffer(null);
			mSurfaceHolder.removeCallback(this);
			try {
				mCamera.setPreviewDisplay(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mPreviewStarted = false;
		}
	}
	
	private synchronized void destroyCamera() 
	{
		if (mCamera != null) 
		{
			mCamera.stopPreview();
			mCamera.setPreviewCallbackWithBuffer(null);
			mSurfaceHolder.removeCallback(this);
			try {
				mCamera.setPreviewDisplay(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mCamera.release();
			mCamera = null;
			mPreviewStarted = false;
			
			//mHandler.removeCallbacks(mPreviewCallback);
		}	
	}

	private boolean FrameDropControl()
	{
		boolean ret = false;

		if(dropFrameIntelval <= 0)
			return ret;

		frameCount ++;

		if (tickCount >= 4294967294L)
			tickCount = 0;

		if (dropFrameJitter)
		{
			if ((tickCount % 2 == 0 && frameCount == dropFrameIntelval) ||
					(tickCount % 2 == 1 && frameCount == dropFrameIntelval + 1)){

				tickCount ++;
				ret = true;
				frameCount = 0;
			}
		} else if (frameCount == dropFrameIntelval ) {

			ret = true;
			frameCount = 0;
		}

		return  ret;
	}

	private int FrameRateCompute(long captureTimeMs)
	{
		int computedFrameRate = 0;

		statisticalFrameCount ++ ;
		if (lastCaptureTimeMs != 0) {
			long FrameDuration = captureTimeMs - lastCaptureTimeMs;
			durationTimePerSecond += FrameDuration;
			if (durationTimePerSecond > 1000) {
				computedFrameRate = statisticalFrameCount -1 ;
				//Log.d(TAG, "videocaputre framerate is " + computedFrameRate + ", and duration is " + durationTimePerSecond);

				durationTimePerSecond = 0;
				statisticalFrameCount = 0;
				lastCaptureTimeMs = -1;

				return computedFrameRate;
			}
		}

		lastCaptureTimeMs = captureTimeMs;
		return  0;
	}

	private void handleIncomingFrame(byte[] data, int length, long captureTimeMs)
	{
		if(VERBOSE) Log.d(TAG, "Capture a video with timestamp = " + captureTimeMs + " size = " + length);

		if (bFrameRateControl) {
			if (dropFrameIntelval == -1) {
				int frameRate = FrameRateCompute(captureTimeMs);
				if (frameRate != 0 && mVideoQuality.framerate != 0 && frameRate > mVideoQuality.framerate){
					int lostFrameNum = frameRate - mVideoQuality.framerate;
					dropFrameIntelval = frameRate / lostFrameNum;

					if (frameRate % lostFrameNum != 0)
						dropFrameJitter = true;

//					Log.d(TAG, "Computedframerate is " + frameRate + ",DstFramerate is "+ mVideoQuality.framerate +
//							",dropFrameIntelval is " + dropFrameIntelval + ", dropFrameJitter is " + dropFrameJitter);
				}
			}else if (FrameDropControl()){
				return ;
			}
		}

		if (mOutBuffer == null) {
			mOutBuffer = ByteBuffer.allocateDirect(length);
			bufferOffset = mOutBuffer.arrayOffset();
		}

		if (mWebRtcVideoProcess != null) {
			if (mVideoQuality.orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (CameraInfo.CAMERA_FACING_FRONT == mCurrentCameraId) {
					mWebRtcVideoProcess.ProcessVideoFrame(data, length, mVideoQuality.width, mVideoQuality.height, 270, mOutBuffer);
					//Log.d(TAG, "270!!!!!!!!!!!!");
				} else {
					mWebRtcVideoProcess.ProcessVideoFrame(data, length, mVideoQuality.width, mVideoQuality.height, 90, mOutBuffer);
					//Log.d(TAG, "90!!!!!!!!!!!!");
				}
			} else {
				//processData = data;
				mWebRtcVideoProcess.ProcessVideoFrame(data, length, mVideoQuality.width, mVideoQuality.height, 0, mOutBuffer);
				//Log.d(TAG, "0!!!!!!!!!!!!");
			}
		}

		if (mCaptureDataCallback != null) {
			byte[] processData = mOutBuffer.array();
			if (bufferOffset > 0){
				//add by liuhao, andriod version greater than 5.0
				processData = Arrays.copyOfRange(processData, bufferOffset, length + bufferOffset);
			}

			mCaptureDataCallback.onIncomingCapturedFrame(processData, length, captureTimeMs);
		}
	}
	
	public void determineClosestSupportedResolution(Camera.Parameters parameters, VideoEngine.VideoQuality videoQuality) 
	{
		int targetWidth = videoQuality.width;
		int targetHeight = videoQuality.height;
		int minDist = Integer.MAX_VALUE;
		String supportedSizesStr = "Supported resolutions: ";
		List<Size> supportedSizes = parameters.getSupportedPreviewSizes();
		for (Iterator<Size> it = supportedSizes.iterator(); it.hasNext();) 
		{
			Size size = it.next();
			supportedSizesStr += size.width + "x" + size.height + (it.hasNext()?", ":"");
			int dist = Math.abs(videoQuality.width - size.width);
			if (dist<=minDist)
			{
				minDist = dist;
				targetWidth = size.width;
				targetHeight = size.height;
			}
		}
		Log.v(TAG, supportedSizesStr);
		if (videoQuality.width != targetWidth || videoQuality.height != targetHeight) 
		{
			videoQuality.width = targetWidth;
			videoQuality.height = targetHeight;
			Log.v(TAG, "Resolution modified: " + videoQuality.width + "x" + videoQuality.height + "->" + targetWidth + "x" + targetHeight);
		}
	}

	public int[] determineMaximumSupportedFramerate(Camera.Parameters parameters) 
	{
		int[] maxFps = new int[]{0,0};
		String supportedFpsRangesStr = "Supported frame rates: ";
		List<int[]> supportedFpsRanges = parameters.getSupportedPreviewFpsRange();
		for (Iterator<int[]> it = supportedFpsRanges.iterator(); it.hasNext();) 
		{
			int[] interval = it.next();
			supportedFpsRangesStr += interval[0]/1000+"-"+interval[1]/1000+"fps"+(it.hasNext()?", ":"");
			if (interval[1]>maxFps[1] || (interval[0]>maxFps[0] && interval[1]==maxFps[1])) 
			{
				maxFps = interval; 
			}
		}
		//Log.i(TAG, supportedFpsRangesStr);
		//Log.i(TAG, "max[0]" + maxFps[0] + "max[1]" + maxFps[1]);

		return maxFps;
	}
	
	
	@SuppressWarnings("unused")
	private void testCameraParameters()
	{
		List<Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();
		List<Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		List<Integer> previewFormats = mCamera.getParameters().getSupportedPreviewFormats();
		List<Integer> previewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
		
		Log.i(TAG, "cyy support parameters is ");
		Size psize = null;
		for (int i = 0; i < pictureSizes.size(); i++) 
		{
			psize = pictureSizes.get(i);
			Log.i(TAG, "PictrueSize,width: " + psize.width + " height" + psize.height);
		}
		for (int i = 0; i < previewSizes.size(); i++) 
		{
			psize = previewSizes.get(i);
			Log.i(TAG, "PreviewSize,width: " + psize.width + " height" + psize.height);
		}
		Integer pf = null;
		for (int i = 0; i < previewFormats.size(); i++) 
		{
			pf = previewFormats.get(i);
			Log.i(TAG, "previewformates:" + pf);
		}
	}
	
	public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width, int height) 
	{
		Log.d(TAG, "VideoCapture::surfaceChanged ignored: " + format + ": " + width + "x" + height);
	}

	public synchronized void surfaceCreated(SurfaceHolder holder) 
	{
		Log.d(TAG, "VideoCapture::surfaceCreated");
		try 
		{
			if (mCamera != null) 
			{
				mCamera.setPreviewDisplay(holder);
			}
		} 
		catch (IOException e) 
		{
			throw new RuntimeException(e);
		}
	}

	public synchronized void surfaceDestroyed(SurfaceHolder holder) 
	{
		Log.d(TAG, "VideoCapture::surfaceDestroyed");
		try 
		{
			if (mCamera != null) 
			{
				mCamera.setPreviewDisplay(null);
			}
		} 
		catch (IOException e) 
		{
			throw new RuntimeException(e);
		}
	}
}
