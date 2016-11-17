package com.codyy.bennu.sdk;

import com.codyy.bennu.dependence.publish.rtmp.MediaEngine;
import com.codyy.bennu.dependence.publish.rtmp.MediaParameters;
import com.codyy.bennu.dependence.publish.rtmp.RTMPClient;
import com.codyy.bennu.sdk.impl.BNLivePublisher;

import android.view.SurfaceView;

public interface BNPublisher
{
	/**
	 * initialize the publisher.
	 */
	public void init();
	/**
	 * release the publisher.
	 */
	public void release();
	/**
	 * set media type of publisher.
	 * @param mediaType  AUDIOANDVIDEO means publish audio and video.
	 *                   AUDIOONLY     means publish audio.
	 *                   VIDEOOwNLY     means publish video.
	 */
	public int setMediaType(int mediaType);
	/**
	 * get MediaParameters to set some attributes.
	 * @param mediaType  AUDIOANDVIDEO means publish audio and video.
	 *                   AUDIOONLY     means publish audio.
	 *                   VIDEOONLY     means publish video.
	 */
	public MediaParameters getParam(int mediaType);
	/**
	 * set the video dimensions that captures.
	 * @param width
	 * @param height
	 */
	public void setVideoSize(int width, int height);
	/**
	 * set the camera attribute.
	 * @param cameraId           0 means back camera, 1 means front.
	 * @param screenOrientation  call{@code getResources().getConfiguration().orientation} to specified.
	 */
	public void setCamera(int cameraId, int screenOrientation);
	/**
	 * set the AECM flag.
	 * @param isUse           true means start the AECM.
	 *                        false means stop the AECM.
	 */
	public void setAECMFlag(boolean isUse);
	/**
	 * call{@code start} the start the publisher.
	 * It must call after call {@code init}.
	 * It is a asynchronous function, set listener if you want to know when it starts completely.
	 * @param uri  the uri that to publish.
	 * @param view the view tht for camera preview.
	 */
	public int  start(String uri, SurfaceView view);
	/**
	 * call{@code stop} the stop the publisher.
	 * It is a asynchronous function, set listener if you want to know when it stops completely.
	 */
	public void stop();

	/** ---------------------------------------------------------------------------------------------------------
	 * Change the audio or video publish dynamically.
	 * If you set not to publisher video(audio) before starting, the call{@code enableXX} function doesn't work.
	 * /

	/**
	 * call{@code enableVideo} to enable the video publisher.
	 * It can be called when publisher on running.
	 */
	public void enableVideo();
	/**
	 * call{@code disableVideo} to disable the video publisher.
	 * It can be called when publisher on running.
	 */
	public void disableVideo();
	/**
	 * call{@code enableAudio} to enable the audio publisher.
	 * It can be called when publisher on running.
	 */
	public void enableAudio();
	/**
	 * call{@code disableAudio} to disable the audio publisher.
	 * It can be called when publisher on running.
	 */
	public void disableAudio();

	// *  ---------------------------------------------------------------------------------------------------------
	/**
	 * change the camera attribute.
	 * @param isChange           true means change the camera which setting before publishing.
	 * @param screenOrientation  call{@code getResources().getConfiguration().orientation} to specified.
	 */
	public void changeCamera(boolean isChange, int screenOrientation);

	public static interface StopDoneListener {
		abstract void stopped();
	}

	/**
	 * call {@code setStopDoneListener} to set stop done listener.
	 * It can set before playing.
	 * @param listener  the StopDoneListener listener that can listen the stop[async] done.
	 */
	public void setStopDoneListener(StopDoneListener listener);

	public static interface StartDoneListener {
		abstract void started();
	}

	/**
	 * call {@code setErrorListener} to set error listener.
	 * It can set before playing.
	 * @param listener  the ErrorListener listener that can listen the error[async] message.
	 */
	public void setErrorListener(ErrorListener listener);

	public static interface ErrorListener {
		abstract void error(int errorCode, String errorMsg);
	}

	/**
	 * call {@code setStartDoneListener} to set start done listener.
	 * It can set before playing.
	 * @param listener  the StartDoneListener listener that can listen the start[async] done.
	 */
	public void setStartDoneListener(StartDoneListener listener);

	//==================================================================================================
	/**
	 * called by render on BNMediaPlayer.
	 * Don't use the function. Just see it as private.
	 */
	public void deliverAudioPlayData(byte[] data, int length);

	/**
	 * called by render on BNMediaPlayer.
	 * Check the Publisher status. Just see it as private.
	 */
	public boolean isRunning();
}

