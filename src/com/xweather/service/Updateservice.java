package com.xweather.service;

import com.xweather.net.HttpCallbackListener;
import com.xweather.net.HttpUtil;
import com.xweather.receiver.UpdateReceiver;
import com.xweather.util.ParseResponUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class Updateservice extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stubhr
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int p =  8 * 60 * 60 * 1000;
		long triggerTime = SystemClock.elapsedRealtime() +  p;
		Intent i =new Intent(this, UpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	
	public void updateWeather(){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
		String weathercode = pre.getString("weather_code", ""); 
		String address = "http://www.weather.com.cn/data/cityinfo/" + weathercode + ".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinish(String response) {
				ParseResponUtil.handleWeatherResponse(Updateservice.this, response);
			}
		});
	}
	
}
