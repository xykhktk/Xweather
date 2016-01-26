package com.xweather.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xweather.util.LogUtil;

public class HttpUtil {
	
	
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				HttpURLConnection conn = null;
				
					try {
						
						//String testurl = "http://op.juhe.cn/onebox/weather/query?key=9eb52df225d9f928161b624bfbdd97c0&cityname=" + URLEncoder.encode("南宁", "UTF-8");
						URL url = new URL(address);
						LogUtil.getInstance().info(getClass().getName() + " get url : " + address);
						conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("GET");
						conn.setConnectTimeout(10000);
						
						System.setProperty("http.keepAlive", "false");
						//conn.setRequestProperty("contentType", "GBK");
						//conn.setRequestProperty("apikey",  "9706e646fa25e770994a17d3d18f8dd6");//我的apikey
						conn.setReadTimeout(10000);
						InputStream ins = conn.getInputStream();
						InputStreamReader isr = new InputStreamReader(ins,"utf-8");
						BufferedReader reader = new BufferedReader(isr);
						StringBuilder response = new StringBuilder();
						String line;
						
						while( (line = reader.readLine()) != null){
							response.append(line);
						}
						if (listener != null){
							LogUtil.getInstance().info(getClass().getName() + " onsuccess : " + response.toString());
							listener.onFinish(response.toString());
						}
					} catch (Exception e){
						LogUtil.getInstance().info(getClass().getName() + " onError : " + e.toString());
						listener.onError(e);
					}finally {
						if (conn != null){
							conn.disconnect();
						}
					}
				
				
					
			}
		}).start();
		
	}

	
}
