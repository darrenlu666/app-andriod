package com.codyy.bennu.dependence.publish.rtmp;

import android.util.Log;

public class TimeUtil 
{
	private long lasttime = 0;
	public long calcInterval()
	{
		long currenttime = System.nanoTime()/1000;
		if (lasttime == 0)
		{
			lasttime = currenttime;
		}
		long delta = (currenttime - lasttime)/1000;//两帧之间间隔
		lasttime = currenttime;
		Log.d("", String.format("VideoCapture() delta=%d", delta));
		return delta;
	}
}
