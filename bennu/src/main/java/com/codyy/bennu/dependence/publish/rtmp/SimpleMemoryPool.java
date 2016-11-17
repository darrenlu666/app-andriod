package com.codyy.bennu.dependence.publish.rtmp;

import java.util.concurrent.ArrayBlockingQueue;

// 实现简单的内存池，防止频繁开辟内存
class SimpleMemoryPool
{
	private int bufferCapacity = 0;
	private int bufferCount = 0;
	private int bufferSize = 0;
	private ArrayBlockingQueue<byte[]> buffers = null;

	// TODO:内存池最大为50，预先开辟5个，如果大于50如何处理？
	SimpleMemoryPool(int capacity, int count, int size)
	{
		bufferCapacity = capacity;
		bufferSize = size;
		buffers = new ArrayBlockingQueue<byte[]>(bufferCapacity);
		addBuffer(count, bufferSize);
	}
	
	public void addBuffer(int count, int size)
	{
		bufferCount = count;
		for (int x = 0; x < bufferCount; x++)
		{
			buffers.add(allocBuffer());
		}		
	}
	
	public void offerBuffer(byte[] buffer)
	{
		buffers.offer(buffer);
	}

	public byte[] pollBuffer()
	{
		byte[] this_buffer;
		if (buffers.isEmpty()) {
			if (bufferCount < bufferCapacity) {
				this_buffer = allocBuffer();
				bufferCount++;
			} else {
				return null;
			}
		} else {
			this_buffer = buffers.poll();
		}
		return this_buffer;
	}

	public void stopBuffer()
	{
		buffers.clear();
		buffers = null;
	}
	
	private byte[] allocBuffer()
	{
		byte[] buffer = new byte[bufferSize];
		return buffer;
	}
}
