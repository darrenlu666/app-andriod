package com.codyy.bennu.sdk.impl;

import android.util.Log;
import android.view.Surface;

import com.codyy.bennu.dependence.publish.rtmp.WebRtcVAD;
import com.codyy.bennu.sdk.BNMediaPlayer;
import com.codyy.bennu.sdk.BNPublisher;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class BNPlayerImpl {
	private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
	private native boolean nativeInit();
	private native void nativeRelease();
	private native void nativeSetAttribute(int type, Object value);
	private native void nativeSetUris(String[] classroomUris);
	private native void nativePlay();      // Set pipeline to PLAYING

	private native void nativePause();    // Set pipeline to PAUSED
	private native void nativeStop();    // Set pipeline to STOP
	private native void nativeSeek(long milliseconds); // Seek
	private native long nativeGetPosition();

	private long _CFJ_native_custom_data;

	public static int CHAT_SOFTWARE = 1;
	public static int CHAT_HARDWARE = 2;
	public static int LIVE_SOFTWARE = 3;
	public static int LIVE_HARDWARE = 4;
	public static int LIVE_WITH_AUDIO_MIX = 5;

	private static int PLAYER_ATTR_TYPE_URI = 1;
	private static int PLAYER_ATTR_TYPE_SURFACE = 2;
	private static int PLAYER_ATTR_TYPE_FORCE_ASPECT_RATIO = 3;
	private static int PLAYER_ATTR_TYPE_VOLUME = 4;
	private static int PLAYER_ATTR_TYPE_PIPELINE = 5;
	private static int PLAYER_ATTR_TYPE_MUTE = 6;
	private static int PLAYER_ATTR_TYPE_TIME_OUT = 7;
	private static int PLAYER_ATTR_VIDEO_SINK_RATE = 8;
	private static int PLAYER_ATTR_VIDEO_SINK_COLORSPACE = 9;
	private static int PLAYER_ATTR_VIDEO_SINK_RESOLUTION = 10;
	private static int PLAYER_ATTR_TYPE_RECEIVE_VIDEO = 11;

	private WebRtcVAD mVAD = null;
	private BNPublisher mPublisher;
	private String mUri;
	private Surface mSurface;
	private boolean mMute;
	private int mVolume;
	private boolean mForceAspectRatio;
	private boolean mReceiveVideo;
	private boolean mStopByIsRecvVideo = false;
	private long mPosition;
	private int mType;
	private int mTimeOut;
	private boolean isUseHwDecoder;
	private boolean mRunning;
	private boolean audioMixerRenderSwitch = true;


	private BNMediaPlayer mMediaPlayer;

	public BNPlayerImpl() {
		mUri = null;
		mSurface = null;
		mMute = false;
		mForceAspectRatio = true;
		mReceiveVideo = true;
		mVolume = 100;
		mTimeOut = 6;
		isUseHwDecoder = true;
		mRunning = false;
		mVAD = new WebRtcVAD();
	}

	public void init() {
		nativeInit();
		//16khz, mono
		mVAD.Init(16000, 160);
		//default is mode_0
		mVAD.SetMode(2);
	}

	public void release() {
		mVAD.Release();
		nativeRelease();
	}

	public void setCallback(BNMediaPlayer mediaPlayer) {
		mMediaPlayer = mediaPlayer;
	}

	public void setPipelineType(int type) {
		if (isUseHwDecoder) {
			if (LIVE_SOFTWARE == type) {
				type = LIVE_HARDWARE;
			} else if (CHAT_SOFTWARE == type) {
				type = CHAT_HARDWARE;
			}
		} else {
			if (LIVE_HARDWARE == type) {
				type = LIVE_SOFTWARE;
			} else if (CHAT_HARDWARE == type) {
				type = CHAT_SOFTWARE;
			}
		}

		nativeSetAttribute(PLAYER_ATTR_TYPE_PIPELINE, type);

		String manufacturer = android.os.Build.MODEL;// ��ȡ�ֻ���
		if (manufacturer.equalsIgnoreCase("MI 3") )
			nativeSetAttribute(PLAYER_ATTR_VIDEO_SINK_COLORSPACE, "RGB");
	}

	public void setDecodeType(boolean isUseHardware) {
		isUseHwDecoder = isUseHardware;
	}

	public void setUri(String uri) {
		mUri = uri;
		nativeSetAttribute(PLAYER_ATTR_TYPE_URI, uri);
	}

	public String getUri() {
		return mUri;
	}

	public void setSurface(Surface surface) {
		mSurface = surface;
		nativeSetAttribute(PLAYER_ATTR_TYPE_SURFACE, surface);
	}

	public Surface getSurface() {
		return mSurface;
	}

	public void setVolume(int volume) {
		if (volume > 100) {
			volume = 100;
		} else if (volume < 0) {
			volume = 0;
		}

		mVolume = volume;

		if(audioMixerRenderSwitch && audioId != -1 && mAudioMixer!= null){
			mAudioMixer.setVolume(audioId, volume);
		} else {
			nativeSetAttribute(PLAYER_ATTR_TYPE_VOLUME, volume);

			if (volume == 0){
				nativeSetAttribute(PLAYER_ATTR_TYPE_MUTE, true);
			}
		}
	}

	public int getVolume() {
		return mVolume;
	}

	public void setMute(boolean mute) {
		mMute = mute;

		if (audioMixerRenderSwitch && audioId != -1 && mAudioMixer!= null){
			if (mMute){
				mAudioMixer.setVolume(audioId, 0);
			}else{
				mAudioMixer.setVolume(audioId, mVolume);
			}
		} else {
			nativeSetAttribute(PLAYER_ATTR_TYPE_MUTE, mute);
			if (mMute) {
				nativeSetAttribute(PLAYER_ATTR_TYPE_VOLUME, 0);
			} else {
				nativeSetAttribute(PLAYER_ATTR_TYPE_VOLUME, mVolume);
			}
		}
	}

	public boolean getMute() {
		return mMute;
	}

	public void setForceAspectRatio(boolean b) {
		mForceAspectRatio = b;
		nativeSetAttribute(PLAYER_ATTR_TYPE_FORCE_ASPECT_RATIO, b);
	}

	public void setReceiveVideo(boolean bRVideo, Surface showSurface)
	{
		if ((bRVideo == mReceiveVideo) || (bRVideo == true && showSurface == null))
			return ;

		mReceiveVideo = bRVideo;
		mSurface = showSurface;

		Log.d("bennu impl", "+setReceiveVideo in implementation:" + bRVideo);

		nativeSetAttribute(PLAYER_ATTR_TYPE_RECEIVE_VIDEO, mReceiveVideo);
		if (mRunning == true) {
			stop();
			mStopByIsRecvVideo = true;
		}
	}

	public boolean getForceAspectRatio() {
		return mForceAspectRatio;
	}

	public void setTimeOut(int time) {
		mTimeOut = time;
		nativeSetAttribute(PLAYER_ATTR_TYPE_TIME_OUT, time);
	}

	public int getTimeOut() {
		return mTimeOut;
	}

	public long getPosition() {
		return mPosition;
	}

	public void setPublisher(BNPublisher publisher) {
		if (null != mAudioMixer) {
			mAudioMixer.setPublisher(publisher);
		}
	}

	private BNAudioMixer mAudioMixer;
	private int audioId = -1;
	public void setAudioMixer(BNAudioMixer audioMixer) {
		if (audioMixer == null) {
			// we will resume bennu player's volume
			if(!mMute && mVolume> 0){
				nativeSetAttribute(PLAYER_ATTR_TYPE_VOLUME, mVolume);
			}
		}
		mAudioMixer = audioMixer;
	}

	public void playWithAudioMix(BNClassroomInfo info) {
		String[] uris = new String[info.getAssistClassroomNum()];

		for (int i = 0; i < info.getAssistClassroomNum(); i++) {
			uris[i] = info.getAssistClassroomUri(i);
		}

		nativeSetUris(uris);
		nativeSetAttribute(PLAYER_ATTR_TYPE_PIPELINE, LIVE_WITH_AUDIO_MIX);
		nativePlay();
	}

	public void play() {
		if(audioMixerRenderSwitch && mAudioMixer != null && mAudioMixer.isRunning()){
			nativeSetAttribute(PLAYER_ATTR_TYPE_MUTE, true);
		}
		nativePlay();
		mRunning = true;
	}

	public void playAfterStop(){
		if (mStopByIsRecvVideo == false)
			return;

		Log.d("OnStateChanged","Receive Video changed, and need play again : " + mReceiveVideo);

		if (mReceiveVideo == true && mSurface != null) {
			nativeSetAttribute(PLAYER_ATTR_TYPE_SURFACE, mSurface);
		}
		nativeSetAttribute(PLAYER_ATTR_TYPE_RECEIVE_VIDEO, mReceiveVideo);
		nativeSetAttribute(PLAYER_ATTR_TYPE_URI, mUri);
		nativeSetAttribute(PLAYER_ATTR_TYPE_VOLUME, mVolume);
		play();

		mStopByIsRecvVideo = false;
	}

	public void pause() {
		nativePause();
	}

	public void stop() {
		nativeStop();
		mRunning = false;
		if (audioId != -1) {
			mAudioMixer.removeAudioID(audioId);
			audioId = -1;
		}
	}

	public void seekTo(long position) {
		nativeSeek(position);
	}


	private void CFJ_PositionUpdated(final long position)  {
		if (mMediaPlayer != null) {
			mPosition = position;
			mMediaPlayer.onPositionUpdated(position);
		}
	}

	private void CFJ_DurationChanged(final long duration) {
		if (mMediaPlayer != null) {
			mMediaPlayer.onDurationChanged(duration);
		}
	}


	private void CFJ_StateChanged(final int state) {
		if (mMediaPlayer != null) {
			mMediaPlayer.onStateChanged(state);
		}
	}

	private void CFJ_Buffering(final int present) {
		if (mMediaPlayer != null) {
			mMediaPlayer.onBuffering(present);
		}
	}

	private void CFJ_EOS () {
		if (mMediaPlayer != null) {
			mMediaPlayer.onEndOfStream();
		}
	}

	private void CFJ_Error(final int errorCode, final String errorMsg) {
		if (mMediaPlayer != null) {
			mMediaPlayer.onError(errorCode, errorMsg);
		}
	}

	private void CFJ_VideoDimensionsChanged (final int width, final int height) {
		if (mMediaPlayer != null) {
			mMediaPlayer.onVideoDimensionsChanged(width, height);
		}
	}
	private void CFJ_UnsupportedHW() {
		if (mMediaPlayer != null) {
			mMediaPlayer.onUnsupportedHW();
		}
	}

	private void CFJ_Message (final String message) {

	}

	private void CFJ_SendPcm (byte[] pcmData) {
		//Log.i("Bennu", "pcmData " + pcmData.length);
		short[] pcmAsShorts = shortMe(pcmData);

		if (null != mAudioMixer && mAudioMixer.isRunning()) {
			if (-1 == audioId && mRunning) {
				audioId = mAudioMixer.getAudioId();
			}
			//int activeData = mVAD.Process(pcmAsShorts, pcmAsShorts.length);
			//Log.d("Bennu", "CFJ_SendPcm len "+ pcmAsShorts.length +",vad active " + activeData);

			//if (activeData == 1)
			mAudioMixer.addNewAudioData(audioId, pcmAsShorts);
		}
	}

	public static short[] shortMe(byte[] bytes) {
		short[] out = new short[bytes.length / 2]; // will drop last byte if odd number
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(out);

		return out;
	}

	public static short[] shortMe2(byte[] bytes) {
		short[] audioDataAsShort = new short[bytes.length]; // will drop last byte if odd number
		for (int i = 0; i < bytes.length; i++) {
			audioDataAsShort[i] = (short)((bytes[i] - 0x80) << 8);
		}
		return audioDataAsShort;
	}

	static {
		System.loadLibrary("bennu_player");
		nativeClassInit();
	}
}
