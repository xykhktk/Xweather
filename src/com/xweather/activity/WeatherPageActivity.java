package com.xweather.activity;

import java.util.prefs.Preferences;

import com.example.xweather.R;
import com.xweather.service.Updateservice;
import com.xweather.util.HttpCallbackListener;
import com.xweather.util.HttpUtil;
import com.xweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherPageActivity extends Activity implements OnClickListener {
	
	private LinearLayout weatherInforlayout;
	private TextView cityName;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1text;
	private TextView temp2text;
	private TextView dataText;
	private Button switchCityBtn;
	private Button refreshBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_page);
		
		weatherInforlayout = (LinearLayout) findViewById(R.id.weatherpage_info_linearlayout);
		cityName = (TextView) findViewById(R.id.weatherpage_cityname);
		publishText = (TextView) findViewById(R.id.weatherpage_publish_text);
		weatherDespText = (TextView) findViewById(R.id.weatherpage_desp);
		temp1text = (TextView) findViewById(R.id.weatherpage_temp1);
		temp2text = (TextView) findViewById(R.id.weatherpage_temp2);
		dataText = (TextView) findViewById(R.id.weatherpage_date);
		switchCityBtn  = (Button) findViewById(R.id.weatherpage_btn_switchCity);
		refreshBtn  = (Button) findViewById(R.id.weatherpage_btn_refresh);
		
		String coutrycode = getIntent().getStringExtra("country_code");
		//Log.i("xw","WeatherPage onCreate:"+coutrycode);
		if(!TextUtils.isEmpty(coutrycode)){
			publishText.setText("同步中");
			weatherInforlayout.setVisibility(View.INVISIBLE);
			//cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(coutrycode);
		}else{
			showWeather();
		}
		
		switchCityBtn.setOnClickListener(this);
		refreshBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.weatherpage_btn_switchCity:
			SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean("city_selected", false);
			editor.commit();
			
			Intent intent = new Intent(this, SelectArea.class);
			intent.putExtra("from_weather_page", true);
			startActivity(intent);
			finish();
			break;
		case R.id.weatherpage_btn_refresh:
			publishText.setText("刷新中...");
			SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = pre.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
		
	}
	
	private void queryWeatherCode(String coutrycode){
		String address = "http://www.weather.com.cn/data/list3/city" + coutrycode + ".xml";
		//Log.i("xw","queryWeatherCode: "+address);
		queryFromServer(address,"countryCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		//Log.i("xw","queryWeatherInfo: "+address);
		queryFromServer(address,"weatherCode");
	}
	
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onRrror(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("刷新失败");
					}
				});
			}
			
			@Override
			public void onFinish(String response) {
				//Log.i("xw","queryFromServer get response :"+response);
				if (type.equals("weatherCode")) {
					Utility.handleWeatherResponse(WeatherPageActivity.this, response);
					//Log.i("xw","queryFromServer,callback type: "+type);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
					
				}else if(type.equals("countryCode")){
					//Log.i("xw","queryFromServer,callback type: "+type);
					if(!TextUtils.isEmpty(response)){
						//Log.i("xw","queryFromServer get response :"+response);
						String[] array = response.split("\\|");
						//Log.i("xw","queryFromServer,array length: "+array.length);
						//Log.i("xw","queryFromServer,array[0]: "+array[0]);
						if (array != null && array.length==2){
							String weatherCode = array[1];
							//Log.i("xw","queryFromServer,callback weatherCode: "+weatherCode);
							queryWeatherInfo(weatherCode);
						}
					}
				}
			}
		});
		
		
	}

	private void showWeather(){
		//Log.i("xw","WeatherPageActivity showWeather");
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(pre.getString("city_name", ""));
		temp1text.setText(pre.getString("temp2", ""));
		temp2text.setText(pre.getString("temp1", ""));
		weatherDespText.setText(pre.getString("weather_desp", ""));
		publishText.setText("今天"+pre.getString("publish_time","")+"发布");
		dataText.setText(pre.getString("current_date", ""));
		weatherInforlayout.setVisibility(View.VISIBLE);
		//cityName.setVisibility(View.VISIBLE);
		
		Intent intent = new Intent(this, Updateservice.class);
		startService(intent);
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		moveTaskToBack(false);
	}
}
