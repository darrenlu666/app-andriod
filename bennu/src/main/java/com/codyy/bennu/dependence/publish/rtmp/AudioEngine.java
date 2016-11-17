package com.codyy.bennu.dependence.publish.rtmp;

import android.util.Log;

import java.util.Arrays;

public class AudioEngine implements MediaCallback
{
	private final static String TAG = "AudioEngine";
	
	private MediaParameters mAudioParameters = null;
	private AudioQuality mAudioQuality = null;
	// 
	private AudioCapture mAudioCapture = null;
	private AudioCodec mAudioCodec = null;
	private RTMPClient mRTMPClient = null;
	
	private boolean mStreaming = false;
	private boolean mCapturedAudio = false;
	private boolean mEnabled = true;
	private byte[] mZeroPCMData;

	public AudioEngine()
	{
		mAudioCapture = new AudioCapture();
		mAudioCodec = new AudioCodec();
		mAudioCapture.registerCaptureDataCallback(this);
		mAudioCodec.registerEncodedDataCallback(this);

		mZeroPCMData = new byte[1024];
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
	
	public void deliverAudioPlayData(byte[] data, int length)
	{
		if (null != mAudioCapture) {
			mAudioCapture.deliverAudioPlayData(data, length);
		}
	}
	
	public void setAecm(boolean enable) 
  	{
		mAudioCapture.setAecm(enable);
	}

	public void setNs(boolean enable)
	{
		mAudioCapture.setNs(enable);
	}

	public void setAgc(boolean enable) 
	{
		mAudioCapture.setAgc(enable);
	}
	
	public void setClient(RTMPClient client)
	{
		mRTMPClient = client;
	}
	
	public void setParameters(MediaParameters parameters)
	{
		mAudioParameters = parameters;
	}
	
	public int configure() throws Exception
	{
		if (mStreaming) {
			throw new IllegalStateException("Can't be called while streaming.");
		}
		
		if (mAudioParameters.getValue("samplerate") == null  || mAudioParameters.getValue("bitrate") == null) {
			throw new Exception("Audio parameters illegal.");
		}
		int samplerate = Integer.parseInt(mAudioParameters.getValue("samplerate").toString());
		int bitrate = Integer.parseInt(mAudioParameters.getValue("bitrate").toString());
		int channels = Integer.parseInt(mAudioParameters.getValue("channel").toString());
		int bitDeep = Integer.parseInt(mAudioParameters.getValue("bitDeep").toString());
		mAudioQuality = new AudioQuality(samplerate, bitrate, channels, bitDeep);

		if (mAudioCapture.configure(mAudioQuality) == -1)
			return RTMPClient.MESSAGE_ERROR_CONFIG_AUDIO_CAPTURE;

		if (mAudioCodec.configure(mAudioQuality) == -1)
			return RTMPClient.MESSAGE_ERROR_CONFIG_AUDIO_CODEC;

		Log.d(TAG,"configure samplerate=" + samplerate + " bitrate=" + bitrate);
		return 0;
	}
	
	public int start()
	{
		if (!mStreaming) {
			mAudioCodec.start();

		if (mAudioCapture.start() == -1) {
			mAudioCodec.stop();
			return RTMPClient.MESSAGE_ERROR_START_OPEN_AUDIO_RECORD;
		}

			mStreaming = true;
			Log.d(TAG,"Start");
		}
		return 0;
	}
	
	public void stop()
	{
		if (mStreaming) {
			mAudioCapture.stop();
			mAudioCodec.stop();
			mStreaming = false;
			Log.d(TAG,"Stop");
		}
	}
	
	public void release()
	{
		mAudioCapture.release();
		mAudioCodec.release();
	}
	
	@Override
	public void onIncomingCapturedFrame(byte[] data, int length, long captureTimeMs) 
	{
		//Log.d(TAG, "Capture audio timestamp = " + captureTimeMs);
		if (!mCapturedAudio && mRTMPClient != null) {
			mCapturedAudio = true;
			mRTMPClient.setAudioInit();
			mRTMPClient.SetAudioParams(mAudioQuality.samplerate);
		}
		
		if (!mEnabled) {
			/*if mute, set PCM data to zero.*/
			data = mZeroPCMData;
		}
		
		mAudioCodec.putCapturedFrame(data, length, captureTimeMs);	
	}
	
	private boolean isFirstAAC = true;
	private int adtsHeaderLength = 0;
	@Override
	public void onIncomingEncodedFrame(byte[] data, int length, long captureTimeMs) 
	{
		//Log.d(TAG, "Encoded audidata = " +data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" " +data[4]+" "+data[5]+" "+data[6] + " "+data[7] +" "+data[8] +" "+data[9] +" ");
		//mRTMPClient.putAudioEncodedFrame(data, 0, length, captureTimeMs);

		if (mRTMPClient != null) {
			if (isFirstAAC) {
				isFirstAAC = false;
				ATDSInfo info = ATDSInfo.parseADTS(data, length , true);
				adtsHeaderLength = info.adtsHeaderLength;
				if (info.audioCfg != null) {
					mRTMPClient.putAudioEncodedFrame(info.audioCfg, 0, info.audioCfg.length, 0);
				}
			}
			mRTMPClient.putAudioEncodedFrame(data, adtsHeaderLength, length, captureTimeMs);
		}

	}
	
	
	// =============================================================
	public class AudioQuality 
	{
		public final static String TAG = "AudioQuality";
		
		public int samplerate = 0;
		public int bitrate = 0;
		public int channel = 0;
		public int bitDeep = 0;
		
		public AudioQuality() {}

		public AudioQuality(int samplerate, int bitrate, int channel, int bitDeep)
		{
			this.samplerate = samplerate;
			this.bitrate = bitrate;
			this.channel = channel;
			this.bitDeep = bitDeep;
		}	

		public boolean equals(AudioQuality quality) 
		{
			if (quality == null) return false;
			return (quality.samplerate == this.samplerate &
					quality.bitrate == this.bitrate &&
					quality.channel == this.channel &&
					quality.bitDeep == this.bitDeep) ;
		}

		public AudioQuality clone() 
		{
			return new AudioQuality(samplerate, bitrate, channel, bitDeep);
		}
	}
}
