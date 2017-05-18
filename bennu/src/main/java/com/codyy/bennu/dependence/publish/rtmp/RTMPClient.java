package com.codyy.bennu.dependence.publish.rtmp;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import static com.codyy.bennu.sdk.impl.BNLivePublisher.AUDIOANDVIDEO;

/**
 * A publish client to publish stream to server.
 */
public class RTMPClient
{
	private native int  StartRTMP(String url);
	private native void StopRTMP();
	private native void SetVideoParams(boolean hasVideo, int width, int height, int framerate);
	private native void SetAudioParams(boolean hasAudio, int samplerate);
	private native int  WriteRTMP(byte[] buf, int len);
	private native int  OpenRTMPLog(byte[] logname, int length);
	private native int  ConfigRTMP();

	static {
		System.loadLibrary("rtmp_jni");
	}
	private static final boolean VERBOSE = true;  // lots of logging
	private final static String TAG = "RTMPClient";

	public final static int STOP_DONE = 0x01;
	public final static int START_DONE = 0x02;
	public final static int ERROR_HAPPENED = 0x03;

	public final static int MESSAGE_NONE = 0;
	public final static int MESSAGE_ERROR_CONNECTION_FAILED = 1;
	public final static int MESSAGE_ERROR_SEND_STREAM_FAILED = 2;
	public final static int MESSAGE_ERROR_ENCODER_AUDIO_BLOCK = 3;
	public final static int MESSAGE_ERROR_ENCODER_VIDEO_BLOCK = 4;
	public final static int MESSAGE_ERROR_CONFIG_EXCEPTION = 5;
	public final static int MESSAGE_ERROR_CONFIG_AUDIO_CAPTURE = 6;
	public final static int MESSAGE_ERROR_CONFIG_AUDIO_CODEC = 7;
	public final static int MESSAGE_ERROR_CONFIG_VIDEO_CAPTURE = 8;
	public final static int MESSAGE_ERROR_CONFIG_VIDEO_CODEC = 9;
	public final static int MESSAGE_ERROR_START_EXCEPTION = 10;
	public final static int MESSAGE_ERROR_START_OPEN_CAMERA = 11;
	public final static int MESSAGE_ERROR_START_OPEN_AUDIO_RECORD = 12;
	public final static int MESSAGE_WARNING_VIDEO_ENCODE_CAPACITY_LACKING = 13;
	//
	private final static int STATE_STARTED = 0x00;
	private final static int STATE_STARTING = 0x01;
	private final static int STATE_STOPPING = 0x02;
	private final static int STATE_STOPPED = 0x03;
	private int   mState = 0;

	private String mUri = null;
	private MediaEngine mMediaEngine = null;
	private int mMediaType = AUDIOANDVIDEO;
	private int mCurConfigTagNum = 0;
	private Callback mCallback;
	private Handler  mMainHandler;

	private long starttime  = 0;
	private Thread  mThread = null;

	private boolean hasVideo = false;
	private boolean hasAudio = false;

	private boolean audioInit = false;
	private boolean videoInit = false;
	private boolean isCanSent = false;
	private boolean isSetVideoConf = false;
	private boolean isSetAudioConf = false;

	// 音频和视频单独队列
	private List<MediaData> videolist = null;
	private List<MediaData> audiolist = null;
	private SimpleMemoryPool videoMemoryPool = null;
	private SimpleMemoryPool audioMemoryPool = null;
	private int videoListThreshold = 80;
	private int audioListThreshold = 150;

	private VideoPacketizer videoPacketizer = null;
	private AudioPacketizer audioPacketizer = null;

	private BlockingQueue<MediaData> mMuxerQueue;
	private Lock mQueueLock;
	private BNHandlerThread mHandlerThread;
	private boolean mFirstAudioTag;

	public interface Callback
	{
		public void onRtmpUpdate(int message,int messagetpye, Exception exception);
	}

	public RTMPClient()
	{
		videolist = Collections.synchronizedList(new LinkedList<MediaData>());
		audiolist = Collections.synchronizedList(new LinkedList<MediaData>());
		mMuxerQueue = new ArrayBlockingQueue<MediaData>(20);
		mQueueLock = new ReentrantLock();
		videoPacketizer = new VideoPacketizer();
		audioPacketizer = new AudioPacketizer();

		mCallback = null;
		mMainHandler = new Handler(Looper.getMainLooper());
		mState = STATE_STOPPED;
		mFirstAudioTag = true;

		mHandlerThread = new BNHandlerThread("RtmpClient");
		mHandlerThread.start();
		mHandlerThread.prepareHandler();
		Log.d(TAG,"RTMPClient");
	}

	public boolean isStreaming()
	{
		return (mState == STATE_STARTED) | (mState == STATE_STARTING);
	}

	public void setMediaEngine(MediaEngine mediaEngine, int mediaType)
	{
		mMediaEngine = mediaEngine;
		mMediaEngine.setClient(this);

		mMediaType = mediaType;
	}

	public void setCallback(Callback cb)
	{
		mCallback = cb;
	}

	public void setServerAddress(String uri)
	{
		mUri = uri;
	}

	private void ReleaseBuffer()
	{
		videolist.clear();
		audiolist.clear();
		mMuxerQueue.clear();
		mFirstAudioTag = true;
		starttime = 0;
		hasVideo = false;
		hasAudio = false;
		isCanSent = false;
		isSetVideoConf = false;
		isSetAudioConf = false;
		mCurConfigTagNum = 0;
	}

	private void ExceptionHandling()
	{
		if (mMediaEngine != null) {
			mMediaEngine.syncStop();
			mMediaEngine = null;
		}
		StopRTMP();
		ReleaseBuffer();
		abord();
	}

	public void startStream()
	{
		if (mUri == null) throw new IllegalStateException("setServerAddress(String) has not been called !");
		if (mMediaEngine == null) throw new IllegalStateException("setMediaEngine(MediaEngine) has not been called !");

		if (mState == STATE_STARTED || mState == STATE_STARTING) {
			Log.w(TAG,"The publisher has been started or on starting");
			return;
		}

		mHandlerThread.postTask(new Runnable() {

			@Override
			public void run() {
				mState = STATE_STARTING;

				Log.d(TAG, "Connecting to RTMP server...");
				try {
					tryConnection();
					Log.d(TAG, "Connecting to RTMP server ok");
				} catch (Exception e) {
					Log.d(TAG, "tryConnection failed:" + e.getMessage());
					postError(ERROR_HAPPENED, MESSAGE_ERROR_CONNECTION_FAILED, e);
					abord();
					return;
				}

				Log.d(TAG, "syncConfigure...");

				try {
					int RetConfig = mMediaEngine.syncConfigure();
					if (RetConfig != 0) {
						Log.d(TAG, "syncConfigure failed with " + RetConfig);
						StopRTMP();
						mMediaEngine = null;
						abord();
						postError(ERROR_HAPPENED, RetConfig, null);
						return;
					}

					Log.d(TAG, "syncConfigure ok");
				} catch (Exception e) {
					Log.d(TAG, "syncConfigure failed:" + e.getMessage());
					StopRTMP();
					mMediaEngine = null;
					abord();
					postError(ERROR_HAPPENED, MESSAGE_ERROR_CONFIG_EXCEPTION, null);
					return;
				}

				Log.d(TAG, "syncStart...");
				try {
					int RetStart = mMediaEngine.syncStart();
					if (RetStart != 0) {
						Log.d(TAG, "syncStart failed with " + RetStart);

						ExceptionHandling();
						postError(ERROR_HAPPENED, RetStart, null);
						return;
					}
					Log.d(TAG, "syncStart ok");
				} catch (Exception e) {
					Log.d(TAG, "syncStart failed:" + e.getMessage());
					ExceptionHandling();
					postError(ERROR_HAPPENED, MESSAGE_ERROR_START_EXCEPTION, null);
					return;
				}

				mThread = new Thread(new PublishTask());
				mThread.start();
				mState = STATE_STARTED;

				if (null != mCallback) {
					mCallback.onRtmpUpdate(START_DONE, MESSAGE_NONE, null);
				}
			}

		});
	}

	public void release()
	{
		mHandlerThread.quit();
	}

	public void stopStream()
	{
		if (mState == STATE_STOPPED || mState == STATE_STOPPING) {
			Log.w(TAG,"The publisher has been stopped or on stopping");
			return;
		}

		mHandlerThread.postTask(new Runnable () {
			@Override
			public void run()
			{
				if (mMediaEngine != null) {
					mMediaEngine.syncStop();
				}
				if (mThread != null) {
					mState = STATE_STOPPED;
					Log.d(TAG, "set mState == STATE_STOPPED");
					try {
						mThread.join();
					}
					catch (InterruptedException e) {}
					mThread = null;
				}
				Log.d(TAG, "Will stop RTMP");
				StopRTMP();
				ReleaseBuffer();
				if (mState != STATE_STOPPED) {
					mState = STATE_STOPPED;
				}

				/*Temp and silly use.*/
				if (null != mCallback) {
					mCallback.onRtmpUpdate(STOP_DONE, MESSAGE_NONE, null);
				}
			}
		});
	}

	private void abord()
	{
		mState = STATE_STOPPED;
	}

	/**
	 * 开始发送rtmp流
	 * @throws IOException
	 */
	private void tryConnection() throws IOException
	{
		ConfigRTMP(); // add
		int ret = StartRTMP(mUri);
		if (ret != 0) {
			throw new IOException("connect to RTMP Server failed");
		}
	}

	public void postError(final int message, final int messagetype, final Exception e) {
		mMainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mCallback != null) {
					mCallback.onRtmpUpdate(message, messagetype, e);
				}
			}
		});
	}


	public class PublishTask implements Runnable
	{
		@Override
		public void run() {
			int ret = 0;
			while (mState != STATE_STOPPED) {
				ret = SendTag();
				if (ret == -1) {
					ExceptionHandling();
					/*Temp and silly use.*/
					if (null != mCallback) {
						mCallback.onRtmpUpdate(ERROR_HAPPENED, MESSAGE_ERROR_SEND_STREAM_FAILED, null);
					}
					break;
				}
			}
			Log.d(TAG, "Leave publish thread");
		}
	}

	//==========================================================================
	public void SetVideoParams(int width, int height, int framerate)
	{
		hasVideo = true;
		SetVideoParams(hasVideo, width, height, framerate);
	}

	public void SetAudioParams(int samplerate)
	{
		hasAudio = true;
		SetAudioParams(hasAudio, samplerate);
	}

	public synchronized void setAudioInit()
	{
		audioInit = true;
		if (firstCapture == 0) {
			firstCapture = System.nanoTime()/1000000;
		}
	}

	public synchronized void setVideoInit()
	{
		videoInit = true;
		if (firstCapture == 0) {
			firstCapture = System.nanoTime()/1000000;
		}
	}

	private void collectData() {
		//Lock
		mQueueLock.lock();

		if (hasVideo && hasAudio) {
			while (!videolist.isEmpty() && !audiolist.isEmpty()) {
				MediaData videoTag = null;
				MediaData audioTag = null;

				videoTag = videolist.get(0);
				audioTag = audiolist.get(0);

				if (audioTag.getTimestamp() <= videoTag.getTimestamp()) {
					if (VERBOSE) Log.d(TAG, "Add an audio tag to queue");
					audiolist.remove(0);
					mMuxerQueue.offer(audioTag);
				} else {
					if (VERBOSE) Log.d(TAG, "Add an video tag to queue");
					videolist.remove(0);
					mMuxerQueue.offer(videoTag);
				}
			}
		} else if (hasVideo) {
			mMuxerQueue.offer(videolist.get(0));
			videolist.remove(0);
		} else if (hasAudio) {
			mMuxerQueue.offer(audiolist.get(0));
			audiolist.remove(0);
		}

		mQueueLock.unlock();
		//Unlock
	}

	// 使用对象池避免重复生成对象造成的消耗
	public synchronized int putAudioEncodedFrame(byte[] data, int offset, int length, long captureTimeMs)
	{
		MediaData audioData = MediaData.createAudioData(data, offset, length);
		audioData.setTimestamp(captureTimeMs);
		audiolist.add(audioData);
		if (audiolist.size() > audioListThreshold && hasVideo) {
			ExceptionHandling();

			if (null != mCallback) {
				//the size of audiolist is too big means video block
				mCallback.onRtmpUpdate(ERROR_HAPPENED, MESSAGE_ERROR_ENCODER_VIDEO_BLOCK, null);
			}
			return -1;
		}

		if (VERBOSE)Log.d(TAG, "Audio capture timestamp = " + captureTimeMs + " Audio queue_size = " + audiolist.size());

		collectData();
		return  0;
	}

	public synchronized int putVideoEncodedFrame(byte[] data, int offset, int length, long captureTimeMs)
	{
		MediaData videoData = MediaData.createVideoData(data, offset, length);
		videoData.setTimestamp(captureTimeMs);
		videolist.add(videoData);
		if (videolist.size() > videoListThreshold && hasAudio) {
			ExceptionHandling();

			if (null != mCallback) {
				//the size of videolist is too big means audio block
				mCallback.onRtmpUpdate(ERROR_HAPPENED, MESSAGE_ERROR_ENCODER_AUDIO_BLOCK, null);
			}
			return -1;
		}

		if (VERBOSE)Log.d(TAG, "Video capture timestamp = " + captureTimeMs + " Video queue_size = " + videolist.size());

		collectData();
		return  0;
	}

	private int mThrowVideoFrameStatus = STATUS_INIT;
	private static int STATUS_INIT = 0;
	private static int STATUS_START = 1;
	private static int STATUS_RUNNING = 2;
	private static int STATUS_END = 3;

	private boolean isSend(byte[] data, int length) {
		/*
		* Simple to say.
		* [H264 PPS] [I Frame] [...] [I Frame] [...]
		*           |**************|
		*           Throw away this data.
		*
		*/
		if (STATUS_INIT == mThrowVideoFrameStatus) {
			mThrowVideoFrameStatus = STATUS_START;
			return true;
		} else if (STATUS_START == mThrowVideoFrameStatus || STATUS_RUNNING == mThrowVideoFrameStatus) {
			if ( videoPacketizer.isKeyFrame(data, length) ) {
				if (STATUS_START == mThrowVideoFrameStatus) {
					mThrowVideoFrameStatus = STATUS_RUNNING;
					return false;
				} else {
					mThrowVideoFrameStatus = STATUS_END;
					return true;
				}
			}
		}

		if (STATUS_END == mThrowVideoFrameStatus) {
			return true;
		}

		return false;
	}

	public synchronized void putEncodedFrame(MediaData mediaData)
	{
		if (mediaData == null) return;
		if (mediaData.isAudio())
		{
			audiolist.add(mediaData);
		}
		else if (mediaData.isVideo())
		{
			videolist.add(mediaData);
		}
	}

	// 取最小时间戳
	private int SendTag()
	{
		int ret = 0;
		MediaData tag = null;

		try {
			//Log.d(TAG, "Wait a tag to send");
			tag = mMuxerQueue.poll(2, TimeUnit.SECONDS);
			//Log.d(TAG, "Got the tag");
			if (null != tag) {
				ret = writeTag(tag);
			}
		} catch (InterruptedException e) {
			;
		}

		return ret;
	}

	private int SendTagFromOneList(int listType)
	{
		MediaData tag = null;
		List<MediaData> tmpList = null;
		if (listType == 1)
		{
			tmpList = audiolist;
		}
		else if (listType == 2)
		{
			tmpList = videolist;
		}

		while (tmpList != null && tmpList.size() > 0)
		{
			tag = tmpList.remove(0);
			int ret = writeTag(tag);
			if (ret != 0)
			{
				return ret;
			}
		}
		return 0;
	}

	private int SendTagFromTwoList()
	{
		int videoSize = videolist.size();
		int audioSize = audiolist.size();
		//Log.d("list", String.format("Publisher videoSize=%d audioSize=%d", videoSize, audioSize));
		// 只有两个队列都有数据时才发送
		if (videoSize > 0 && audioSize > 0)
		{
			int ret = 0;
			while (true)
			{
				MediaData videoTag = null;
				MediaData audioTag = null;
				MediaData tag = null;
				if (videoSize > 0) {
					videoTag = videolist.get(0);
				}

				if (audioSize > 0) {
					audioTag = audiolist.get(0);
				}

				if (videoTag != null && audioTag != null)
				{
					if (audioTag.getTimestamp() <= videoTag.getTimestamp())
					{
						tag = audiolist.remove(0);
						audioSize--;
					}
					else
					{
						tag = videolist.remove(0);
						videoSize--;
					}
				}
				else
				{
					break;//
				}

				if (tag != null)
				{
					ret = writeTag(tag);
					if (ret != 0)
					{
						return ret;
					}
				}
			}
		}
		return 0;
	}

	public int writeTag(MediaData mediaData)
	{
		int ret = -1;
		if (mediaData.isAudio()) {
			ret = writeAudioTag(mediaData.getData(), mediaData.getDataLength(), mediaData.getTimestamp());
		} else if (mediaData.isVideo()) {
			ret = writeVideoTag(mediaData.getData(), mediaData.getDataLength(), mediaData.getTimestamp());
		}
		return ret;
	}

	private int writeAudioTag(byte[] buf, int len, long timepoint)
	{
		if (audioPacketizer == null || buf == null || len <= 0) return -1;

		if (mFirstAudioTag) {
			if (VERBOSE) Log.d(TAG, "Send audio conf tag len = [" + len + "] timestamp = " + timepoint);
			audioPacketizer.parseAudioConf(buf, len);
			setAudioConf(buf, len);
			mFirstAudioTag = false;
			sendAudioConf(0);
			setAudioOrVideoCanSent(true);
			//trySendAudioAndVideoConf();
			return 0;
		}

		//setAudioOrVideoCanSent(true);
		if (isAudioOrVideoCanSent())
		{
			int timeStamp = calcTimestamp(timepoint);
			if (VERBOSE) Log.d(TAG, "Send audio tag len = [" + len + "] timestamp = " + timeStamp + " timepoint = " + timepoint);
			byte[] tag = audioPacketizer.makeAudio(buf, len, timeStamp);
			int ret = send(tag, tag.length, timepoint);// 发送数据

//			int timeStamp = calcTimestamp(timepoint);
//			int ret = WriteAudio(buf, len, timeStamp, false);
			return ret;
		}
		return 0;
	}

	private byte[] audioConf = null;
	private byte[] videoConf = null;
	public void setAudioConf(byte[] buf, int len)
	{
		audioConf = new byte[len];
		System.arraycopy(buf, 0, audioConf, 0, len);
	}

	public void setVideoConf(byte[] buf, int len)
	{
		videoConf = new byte[len];
		System.arraycopy(buf, 0, videoConf, 0, len);
	}

	private int writeVideoTag(byte[] buf, int len, long timepoint)
	{
		if (videoPacketizer == null || buf == null || len <= 0) return -1;

		if (videoPacketizer.isVideoConf(buf, len))
		{
			if (VERBOSE) Log.d(TAG, "Send video conf tag len = [" + len + "] timestamp = " + timepoint);
			videoPacketizer.parseVideoConf(buf, len);

			setVideoConf(buf, len);
			sendVideoConf(0);
			setAudioOrVideoCanSent(true);
			//trySendAudioAndVideoConf();
			return 0;
		}

		if (isAudioOrVideoCanSent())
		{
			boolean isKeyFrame = videoPacketizer.isKeyFrame(buf, len);
			int timeStamp = calcTimestamp(timepoint);
			String msg =  isKeyFrame ? "[KeyFrame]" : "";
			if (VERBOSE) Log.d(TAG, "Send video tag len = [" + len + "] timestamp = " + timeStamp + "" + msg);
			byte[] tag = videoPacketizer.makeVideoTag(buf, len, timeStamp, isKeyFrame);
			int ret = send(tag, tag.length, timepoint);// 发送数据

			return ret;
		}
		return 0;
	}

	private void trySendAudioAndVideoConf()
	{
		if (hasAudio || hasVideo) {
			if (hasAudio && hasVideo) {
				if (isSetVideoConf && isSetAudioConf) {
					sendAudioConf(0);
					sendVideoConf(0);
				}
			} else if (hasAudio) {
				sendAudioConf(0);

			} else if (hasVideo) {
				sendVideoConf(0);
			}
			setAudioOrVideoCanSent(true);
		} else {
			setAudioOrVideoCanSent(false);
		}

	}

	private boolean isAudioOrVideoCanSent()
	{
		return isCanSent;
	}

	private void setAudioOrVideoCanSent(boolean isCanSent)
	{
		if (mMediaType == AUDIOANDVIDEO && mCurConfigTagNum < 2)
			return;;

		this.isCanSent = isCanSent;
	}

	private int sendVideoConf(int ts)
	{
		if (isSetVideoConf)
			return 0;

		isSetVideoConf = true;
		mCurConfigTagNum ++;

		byte[] tag = videoPacketizer.makeVideoConf(ts);
		return send(tag, tag.length, ts);
	}

	private int sendAudioConf(int ts)
	{
		if(isSetAudioConf)
			return 0;

		isSetAudioConf = true;
		mCurConfigTagNum ++;

		byte[] tag = audioPacketizer.makeAudioConf(ts);
		return send(tag, tag.length, ts);
	}

	private long firstCapture = 0;
	private long firstSend = 0;
	private long fixed = 0;
	private int send(byte[] buf, int length, long timepoint)
	{
		if (timepoint != 0 && firstSend == 0) {
			firstSend = System.nanoTime()/1000000;
			fixed = firstSend - firstCapture;
		}
		long delay = System.nanoTime()/1000000 - timepoint;
		//Log.d(TAG, String.format("capture and send:delay=%d fistDelay=%d", delay, fixed));
		return WriteRTMP(buf, length);
	}

	// 返回值由long改成int
	private int calcTimestamp(long time)
	{
		if (time != 0) {
			if (starttime == 0) {
				starttime = time;
			}
			return (int)(time - starttime);
		}
		return 0;
	}

}

