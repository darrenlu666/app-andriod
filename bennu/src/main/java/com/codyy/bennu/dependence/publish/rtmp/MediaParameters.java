package com.codyy.bennu.dependence.publish.rtmp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.util.Log;

/**
 * 参数配置
 */
public class MediaParameters
{
	private int type;
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public MediaParameters(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
	
	public int setValue(String key, Object value)
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
	public Object getValue(String key)
	{
		if (map.containsKey(key))
		{
			return map.get(key);
		}
		return null;
	}
	
	public void show()
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
