package com.codyy.bennu.sdk.impl;

import android.util.Log;

import com.codyy.bennu.sdk.BNPublisherParam;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BNLivePublisherParam implements BNPublisherParam
{
	private int type;
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public BNLivePublisherParam(int type)
	{
		this.type = type;
	}
	
	public int GetType()
	{
		return type;
	}
	
	public int SetValue(String key, Object value)
	{
		if (map.containsKey(key))
		{
			//Log.d("param", "remove key=" + key + " value=" + map.get(key).toString());
			map.remove(key);
		}
		map.put(key, value);
		return 0;
	}
	
	// return the value of the mapping with the specified key, or {@code null} if no mapping for the specified key is found.
	public Object GetValue(String key)
	{
		if (map.containsKey(key))
		{
			return map.get(key);
		}
		return null;
	}
	
	public void Show()
	{
		Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
		while (iter.hasNext())
		{
		    Map.Entry<String, Object> entry = iter.next();
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    Log.d("param", "key=" + key + " value=" + value.toString());
		}
	}
	
}
