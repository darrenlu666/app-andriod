package com.codyy.bennu.sdk;

public interface BNPublisherParam 
{
	public int GetType();
	public int SetValue(String key, Object value);
	public Object GetValue(String key);
}
