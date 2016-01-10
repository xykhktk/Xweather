package com.xweather.activity;

import com.example.xweather.R;
import com.xweather.net.HttpCallbackListener;
import com.xweather.net.HttpUtil;
import com.xweather.service.Updateservice;
import com.xweather.util.Consts;
import com.xweather.util.LocationUtil;
import com.xweather.util.LogUtil;
import com.xweather.util.ParseResponUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherPageActivity extends Activity implements OnClickListener {
	
	private LinearLayout weatherInforlayout;
	private TextView cityName;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1text;
	private TextView temp2text;
	private TextView dataText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_page);
		
		weatherInforlayout = (LinearLayout) findViewById(R.id.weatherpage_info_linearlayout);
		publishText = (TextView) findViewById(R.id.weatherpage_publish_text);
		weatherDespText = (TextView) findViewById(R.id.weatherpage_desp);
		temp1text = (TextView) findViewById(R.id.weatherpage_temp1);
		temp2text = (TextView) findViewById(R.id.weatherpage_temp2);
		dataText = (TextView) findViewById(R.id.weatherpage_date);
		
		if (TextUtils.isEmpty(LocationUtil.getInstance().getWeatherCode())){
			LocationUtil.getInstance().getLatLng(this);
		};
		
		String coutrycode = null;
		coutrycode = getIntent().getStringExtra("country_code");
		
		if(coutrycode != null){	//if it is comes from select area page
			LogUtil.getInstance().info(getClass().getName() + " coutrycode " + coutrycode);
			publishText.setText(getResources().getString(R.string.weather_page_fetching));
			//weatherInforlayout.setVisibility(View.INVISIBLE);
			//cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(coutrycode);	
		}else if(!TextUtils.isEmpty(PreferenceManager.getDefaultSharedPreferences(this).getString("city_name", ""))){ 
			//not first time running,and have data in sharedpreference.
			showWeather();
		}else if(!TextUtils.isEmpty(LocationUtil.getInstance().getWeatherCode())){	
			//if it is first time running: no data in SharedPreferences ,and LocationUtil has data.
			queryWeatherInfo(LocationUtil.getInstance().getWeatherCode());
		}else{
			Toast.makeText(this, getResources().getString(R.string.weather_page_faile), Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_select_area:
			LogUtil.getInstance().info(getClass().getName() + "  action_select_area click");
			SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean("city_selected", false);
			editor.commit();
			
			Intent intent = new Intent(this, SelectArea.class);
			intent.putExtra("from_weather_page", true);
			startActivity(intent);
			//finish();
			break;
		case R.id.action_refresh:
			publishText.setText(getResources().getString(R.string.weather_page_refreshing));
			String weatherCode;
			weatherCode = LocationUtil.getInstance().getWeatherCode();
			if(TextUtils.isEmpty(weatherCode)){
				SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
				weatherCode = pre.getString("weather_code", "");
			}
			
			if (!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}else{
				Toast.makeText(this, getResources().getString(R.string.weather_page_faile), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
	}
	
	private void queryWeatherCode(String coutrycode){
		String address = "http://www.weather.com.cn/data/list3/city" + coutrycode + ".xml";
		queryFromServer(address,"countryCode");
	}
	
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address,"weatherCode");
	}
	
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText(getResources().getString(R.string.weather_page_error));
					}
				});
			}
			
			@Override
			public void onFinish(String response) {
				if (type.equals("weatherCode")) {
					ParseResponUtil.handleWeatherResponse(WeatherPageActivity.this, response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
					
				}else if(type.equals("countryCode")){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if (array != null && array.length==2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}
			}
		});
		
	}

	private void showWeather(){
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
		//cityName.setText(pre.getString("city_name", ""));
		getActionBar().setTitle(pre.getString(Consts.SPKey_City_name, ""));
		temp1text.setText(pre.getString(Consts.SPKey_Temp2, ""));
		temp2text.setText(pre.getString(Consts.SPKey_Temp1, ""));
		weatherDespText.setText(pre.getString(Consts.SPKey_Weather_desp, ""));
		publishText.setText(getResources().getString(R.string.weather_page_pubtime)+pre.getString(Consts.SPKey_Publish_time,""));
		dataText.setText(pre.getString(Consts.SPKey_Current_date, ""));
		//weatherInforlayout.setVisibility(View.VISIBLE);
		
		Intent intent = new Intent(this, Updateservice.class);
		startService(intent);
	}
	
/*	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		moveTaskToBack(false);
	}*/
}
