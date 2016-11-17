package com.codyy.bennu.dependence.publish.rtmp;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.codyy.bennu.dependence.publish.rtmp.AudioEngine.AudioQuality;

public class VoAACEncoder extends AudioEncoder
{
	static 
	{
//		System.loadLibrary("voaac_enc");
		System.loadLibrary("voaac_jni");
	}
	// 指示编码后数据是否包含ADTS头?
	private native int  nativeInit(int sampleRate, int channels, int bitrate);
	private native int  nativeEncode(byte[] inputBuffer, int inputSize, Object outputBuffer, int outputSize);
	private native int  nativeClose();
	
	private ByteBuffer mOutBuffer = null;
	private int mOutBufferSize = 4096;
	private boolean hasOffset = false;
	private int offset = 0;
	
	public VoAACEncoder(AudioCodec audioCodec)
	{
		super(audioCodec);
		mOutBuffer = ByteBuffer.allocateDirect(mOutBufferSize);
		if (mOutBuffer.arrayOffset() != 0) {
			hasOffset = true;
			offset = mOutBuffer.arrayOffset();
		}
	}

	@Override
	public int configure(AudioQuality audioQuality) 
	{
		mAudioQuality = audioQuality;
		nativeInit(mAudioQuality.samplerate, mAudioQuality.channel, mAudioQuality.bitrate);
		return 0;
	}
	
	@Override
	public int close() 
	{
		nativeClose();
		return 0;
	}
	
	public int encode(byte[] data, int length, long captureTimeMs)
	{
		//logInfo(data);
		//Log.d("VoAACEncoder", length + " is coming");
		int ret = nativeEncode(data, length, mOutBuffer, mOutBufferSize);
		if (ret > 0) {
			byte[] outData = mOutBuffer.array();

			if (hasOffset){
				//add by liuhao；The encoded audio data in JNI is right，but error in java，when andriod version greater than 5.0
				outData = Arrays.copyOfRange(outData, offset, mOutBufferSize + offset);
			}
			sendEncodedData(outData, ret, captureTimeMs);
		}
		return ret;
	}

	public void logInfo(byte[] data){
		if(data != null && data.length >=8){
			StringBuilder jpsb = new StringBuilder();
			jpsb.append(" ");
			for(int i = 0 ;i <8 ; i++){
				jpsb.append(data[i]);
				jpsb.append(" ");
			}

			Log.d(TAG,jpsb.toString());
		}
	}
	
}
