//package net.majorkernelpanic.streaming.rtp;
package com.codyy.bennu.dependence.publish.rtmp;
import java.nio.ByteBuffer;

import android.util.Log;

public class NativeMemory 
{
	private native void nativeSetBuffer(Object buffer, int len);
	private native void nativeChangeBuffer(Object buffer, int len);
	private native void nativeSwapAdjacentTwo(Object buffer, int len);
	
	static 
	{
        System.loadLibrary("native");
    }
	
	private final static String TAG = "NativeMemory";
	private ByteBuffer byteBuffer;
	private int resolution = 0;
	
	public NativeMemory(int size) // 分辨率
	{
		this.resolution = size;
		this.byteBuffer = ByteBuffer.allocateDirect(this.resolution*3/2);
	}
	
	public byte[] swapAdjacentTwo(byte[] data, int size)
	{
		if (size != resolution)
		{
			this.resolution = size;
			this.byteBuffer = ByteBuffer.allocateDirect(this.resolution*3/2);
		}
		System.arraycopy(data, 0, byteBuffer.array(), 0, data.length);
		nativeSwapAdjacentTwo(byteBuffer, this.resolution);
		return byteBuffer.array();
	}
	
	public void printBuffer(String tag, byte[] buffer) 
	{
		StringBuffer sBuffer = new StringBuffer();
		for( int i=0; i<buffer.length; i++ ) {
			sBuffer.append(buffer[i]);
			sBuffer.append(" ");	
		}
		Log.d("Native", tag + sBuffer.toString());
	}
	
//	
//	
//	public void test() 
//	{
//		byte[] buffer = new byte[5];
//		for (int i = 0; i < 5; i++)
//		{
//			buffer[i] = (byte) i;
//		}
//		printBuffer("buffer: ",buffer);
//		
//		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
//		byte[] data = byteBuffer.array();
//		for (int i = 0; i < 5; i++)
//		{
//			data[i] += 10;
//		}
//		printBuffer("data: ",data);
//		printBuffer("buffer: ",buffer);
//		
//		
//		//testNative();
//		testJava();
//	}
//	
//	// C++
//	public void testNative() 
//	{
//		testNativeSetBuffer();
//		testNativeChangeBuffer();
//	}
//	
//	public void testNativeSetBuffer() 
//	{
//		long start = System.nanoTime();
//		nativeSetBuffer(mDirectBuffer, TEST_BUFFER_SIZE);
//		long spend = (System.nanoTime() - start)/1000000;
//		Log.d(TAG, String.format("c++ set spend=%d", spend));
//	}
//	
//	public void testNativeChangeBuffer() 
//	{
//		long start = System.nanoTime();
//		nativeChangeBuffer(mDirectBuffer, TEST_BUFFER_SIZE);
//		long spend = (System.nanoTime() - start)/1000000;
//		Log.d(TAG, String.format("c++ change spend=%d", spend));
//	}
//	
//	// Java
//	public void testJava() 
//	{
//		for (int i = 0; i < 2; i++)
//		{
//			testJavaSetBuffer();
//			testJavaChangeBuffer();
//			testJavaCopyBuffer();
//			testJavaSystemCopy();
//		}
//	}
//	
//	int mSize = 320 *240;
//	int count = mSize*3/2;
//	byte[] data    = new byte[mSize*3/2];
//	byte[] mBuffer = new byte[mSize*3/2];
//	public void testJavaSetBuffer() 
//	{
//		long start = System.currentTimeMillis();
//		int i = 0;
//		//for(i=0; i<count; i++)
//		int j = count;
//		while (j > 0)
//		{
//			//data[i] = 1;
//			j--;
//		}
//		long spend = System.currentTimeMillis() - start;
//		Log.d(TAG, String.format("java set spend=%d", spend));
//	}
//	
//	public void testJavaChangeBuffer() 
//	{
//		long start = System.currentTimeMillis();
//		for (int i = mSize; i < count; i += 2) 
//		{
//			mBuffer[0] = data[i+1];
//			data[i+1] = data[i];
//			data[i] = mBuffer[0]; 
//		}
//		long spend = System.currentTimeMillis() - start;
//		Log.d(TAG, String.format("java change spend=%d", spend));
//	}
//	
//	public void testJavaCopyBuffer() 
//	{
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < mSize/4; i+=1) 
//		{
//			mBuffer[i] = data[mSize+2*i];
//			mBuffer[mSize/4+i] = data[mSize+2*i+1];
//		}
//		long spend = System.currentTimeMillis() - start;
//		Log.d(TAG, String.format("java copy spend=%d", spend));
//	}
//	
//	public void testJavaSystemCopy() 
//	{
//		long start = System.currentTimeMillis();
//		System.arraycopy(data, 0, mBuffer, 0, count);
//		long spend = System.currentTimeMillis() - start;
//		Log.d(TAG, String.format("java system copy spend=%d", spend));
//	}
//	
}
