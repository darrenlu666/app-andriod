package com.codyy.bennu.dependence.publish.rtmp;

public class FAACEncoder extends AudioEncoder
{ 
	static 
	{
		/*Not use any more.*/
		//System.loadLibrary("faac");
	}

	public native int  AACEncoderOpen(int sampleRate, int channels);
    public native int  AACEncoderEncode(byte[] Buffer, int BufferSize,byte[] encoded, int encodedSize);
	public native int  AACEncoderClose();
	public native int  AACEncoderGetMaxOutputBytes();
	public native int  AACEncoderGetSamplesInput();
	public native int  AACEncoderEncodeWithDirectBuffer(byte[] rawBuffer, int rawSize, Object encodedBuffer, int encodedSize);
	
	private int samplesInput  = 0;
	private int pcmBufferSize = 0;
	private int aacBufferSize = 0;
	// Code 2048 bytes at a time for FAAC
	private byte[] pcmBuffer   = null;
	private byte[] encodedData = null;
	private int    pcmDataSize = 0;
	
	public FAACEncoder() 
	{
		this(null);
	}
	
	public FAACEncoder(AudioCodec codec) 
	{
		super(codec);
	}
	
	@Override
	public int configure(AudioEngine.AudioQuality audioQuality) 
	{
		// TODO Auto-generated method stub
		mAudioQuality = audioQuality;
		AACEncoderOpen(mAudioQuality.samplerate, mAudioQuality.channel);
		aacBufferSize = AACEncoderGetMaxOutputBytes(); // 768
		samplesInput  = AACEncoderGetSamplesInput();   // 1024
		pcmBufferSize = samplesInput * (16 / 8) * mAudioQuality.channel; // 2048
		
		pcmBuffer = new byte[pcmBufferSize];
		encodedData = new byte[aacBufferSize];
		return 0;
	}

	@Override
	public int close() 
	{
		// TODO Auto-generated method stub
		AACEncoderClose();
		return 0;
	}
	
	@Override
	public int encode(byte[] data, int length, long captureTimeMs) 
	{
		// TODO Auto-generated method stub
		int ret = AACEncoderEncode(data, length, encodedData, encodedData.length);
		if (ret > 0)
		{
			sendEncodedData(encodedData, ret, captureTimeMs);
		}
		return ret;
	}

}
