package com.xweather.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.xweather.util.LogUtil;

public class HttpUtil {
	
	
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		
		LogUtil.getInstance().info(address);
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection conn = null;
				
					try {
						URL url = new URL(address);
						conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("GET");
						conn.setConnectTimeout(10000);
						conn.setReadTimeout(10000);
						InputStream ins = conn.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
						StringBuilder response = new StringBuilder();
						String line;
						while( (line = reader.readLine()) != null){
							response.append(line);
						}
						if (listener != null){
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
					/*catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (ProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}*/
					
			}
		}).start();
		
	}
	
	
	
}
