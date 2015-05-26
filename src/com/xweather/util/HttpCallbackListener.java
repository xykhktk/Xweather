package com.xweather.util;

public interface HttpCallbackListener {
	
	void onFinish(String response);
	void onRrror(Exception e);
}
