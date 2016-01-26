package com.xweather.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.xweather.activity.WeatherPageJuheActivity;
import com.xweather.bean.JuheInfo;
import com.xweather.net.HttpCallbackListener;
import com.xweather.net.HttpUtil;
import com.xweather.receiver.UpdateReceiver;
import com.xweather.util.Consts;
import com.xweather.util.ObjectUtil;
import com.xweather.util.ParseResponUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

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
		SharedPreferences pre = getSharedPreferences(Consts.SP_Name, Context.MODE_PRIVATE);
		String city = pre.getString(Consts.SPKey_Selected_City, ""); 
		if(!TextUtils.isEmpty(city)) city = pre.getString(Consts.SPKey_Current_city, "");
		if(!TextUtils.isEmpty(city)) city = Consts.DefaultCity;
		
		try {
			String address = Consts.Juhe_Querry_Weather_Url + URLEncoder.encode(city,"UTF-8");
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				
				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFinish(String response) {
					//ParseResponUtil.handleWeatherResponse(Updateservice.this, response);
					JuheInfo uheInfo = ParseResponUtil.handleJsonResultFromJuhe(response, Updateservice.this);
					ObjectUtil.saveJuheInfo(Updateservice.this, uheInfo);
				}
			});
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
}
