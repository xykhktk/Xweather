package com.xweather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.example.xweather.R;
import com.xweather.bean.JuheInfo;
import com.xweather.bean.JuheInfo.future_info;
import com.xweather.net.HttpCallbackListener;
import com.xweather.net.HttpUtil;
import com.xweather.service.Updateservice;
import com.xweather.util.Consts;
import com.xweather.util.LocationUtil;
import com.xweather.util.LogUtil;
import com.xweather.util.ObjectUtil;
import com.xweather.util.ParseResponUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 *使用聚合数据的api的activity。 
 *
 */
public class WeatherPageJuheActivity extends Activity {
	
	private JuheInfo JuheInfo;
	private String current_city = null;
	private String selected_city = null;
	private final String default_city = "南宁";
	private final static String intentKey_contry = "intentKey_contry";
	
	
	private TextView direct;
	private TextView wind;
	private TextView date;
	private TextView realtime_info;
	private TextView realtime_temperature;
	private TextView kongtiao0;
	private TextView kongtiao1;
	private TextView yundong0;
	private TextView yundong1;
	private TextView ziwaixian0;
	private TextView ziwaixian1;
	private TextView ganmao0;
	private TextView ganmao1;
	private TextView xiche0;
	private TextView xiche1;
	private TextView wuran0;
	private TextView wuran1;
	private TextView chuanyi0;
	private TextView chuanyi1;
	private TextView pm25;
	private TextView pm25_quality;
	private TextView pm25_des;
	private TextView week0;
	private TextView week0_info;
	private TextView week0_day;
	private TextView week0_night;
	private TextView week1;
	private TextView week1_info;
	private TextView week1_day;
	private TextView week1_night;
	private TextView week2;
	private TextView week2_info;
	private TextView week2_day;
	private TextView week2_night;
	private TextView week3;
	private TextView week3_info;
	private TextView week3_day;
	private TextView week3_night;
	
	private int Msg_changeUI = 0x1001;
	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == Msg_changeUI){
				showWeather();
			}
			
			super.handleMessage(msg);
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weatherpage_juhe);
		initviews();
		
		JuheInfo = ObjectUtil.readJuheInfo(this);
		
		SharedPreferences sp = getSharedPreferences(Consts.SP_Name, Context.MODE_PRIVATE);
		if (TextUtils.isEmpty(sp.getString(Consts.SPKey_Current_city, ""))){
			LogUtil.getInstance().info(getClass().getName() + "LocationUtil ");
			LocationUtil.getInstance().getLatLng(this);
		};
		
		selected_city = getIntent().getStringExtra(intentKey_contry);
		if(selected_city != null){
			//if it is comes from select area page
			LogUtil.getInstance().info(getClass().getName() + " from select page , selected_city " + current_city);
			Editor editor = sp.edit();
			editor.putString(Consts.SPKey_Selected_City, selected_city);
			editor.commit();
			querryInfo(selected_city);
		}else if(JuheInfo != null && JuheInfo.getFuture_info()[0].getDay_info() !=null){ 
			//not first time running,and have data in temp file.
			LogUtil.getInstance().info(getClass().getName() + " current_city == null  ,read preference." );
			showWeather();
		}else if(!TextUtils.isEmpty(LocationUtil.getInstance().getCurrent_city())){	
			//if it is first time running: no data in SharedPreferences ,and LocationUtil has data.
			LogUtil.getInstance().info(getClass().getName() + " coutrycode == null  ,no data in SharedPreferences,,and LocationUtil has data" );
			querryInfo(LocationUtil.getInstance().getCurrent_city());
		}else{
			//Toast.makeText(this, getResources().getString(R.string.weather_page_faile), Toast.LENGTH_LONG).show();
			querryInfo(default_city);
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
			SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean("city_selected", false);
			editor.commit();
			
			Intent intent = new Intent(this, SelectArea.class);
			intent.putExtra("from_weather_page", true);
			startActivity(intent);
			//finish();
			break;
		case R.id.action_refresh:
			if(!TextUtils.isEmpty(selected_city)){
				querryInfo(selected_city);
			}else if(!TextUtils.isEmpty(current_city)){
				querryInfo(current_city);
			}else{
				querryInfo(default_city);
			}
			break;
		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void querryInfo(String city_name){
		
		if(city_name == null) return;
		
		final ProgressDialog pd = new ProgressDialog(WeatherPageJuheActivity.this);
		pd.setMessage(getResources().getString(R.string.progressdialog_getting_info));
		pd.show();
		
		String url = null;
		try {
			url = Consts.Juhe_Querry_Weather_Url + URLEncoder.encode(city_name, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if(url != null){
			HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
				
				@Override
				public void onFinish(String response) {
					// TODO Auto-generated method stub
					JuheInfo = ParseResponUtil.handleJsonResultFromJuhe(response, WeatherPageJuheActivity.this);
					ObjectUtil.saveJuheInfo(WeatherPageJuheActivity.this, JuheInfo);
					//showWeather();
					Message m = new Message();
					m.what = Msg_changeUI;
					handler.sendMessage(m);
					pd.dismiss();
				}
				
				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					pd.dismiss();
				}
			});
		}
		
	}
	

	private void showWeather(){
		
		if(!TextUtils.isEmpty(selected_city)){
			getActionBar().setTitle(selected_city);
		}else if(!TextUtils.isEmpty(current_city)){
			getActionBar().setTitle(current_city);
		}else{
			getActionBar().setTitle(default_city);
		}
		
		wind.setText(JuheInfo.getDirect() + " " + JuheInfo.getPower());
		realtime_info.setText(JuheInfo.getRealtime_info());
		realtime_temperature.setText(JuheInfo.getRealtime_temperature() + "°");
		
		kongtiao0.setText("空调-" + JuheInfo.getKongtiao0());
		kongtiao1.setText(JuheInfo.getKongtiao1());
		yundong0.setText("运动-" + JuheInfo.getYundong0());
		yundong1.setText(JuheInfo.getYundong1());
		ziwaixian0.setText("紫外线-" + JuheInfo.getZiwaixian0());
		ziwaixian1.setText(JuheInfo.getZiwaixian1());
		ganmao0.setText("感冒-" +JuheInfo.getGanmao0());
		ganmao1.setText(JuheInfo.getGanmao1());
		xiche0.setText("洗车-" +JuheInfo.getXiche0());
		xiche1.setText(JuheInfo.getXiche1());
		wuran0.setText("污染-" + JuheInfo.getWuran0());
		wuran1.setText(JuheInfo.getWuran1());
		chuanyi0.setText("穿衣-" + JuheInfo.getChuanyi0());
		chuanyi1.setText(JuheInfo.getChuanyi1());
		pm25.setText("pm2.5:" +JuheInfo.getPm25());
		pm25_quality.setText("空气质量  " + JuheInfo.getPm25_quality());
		
		week0.setText("今天");
		week0_info.setText(JuheInfo.getFuture_info()[0].getDay_info());
		week0_day.setText(JuheInfo.getFuture_info()[0].getDay_temperature0() + "~" + JuheInfo.getFuture_info()[0].getDay_temperature2());
		week0_night.setText(JuheInfo.getFuture_info()[0].getNight_temperature0() + "~" + JuheInfo.getFuture_info()[0].getNight_temperature2());
		LogUtil.getInstance().info(getClass().getName() + " showWeather() " + JuheInfo.getFuture_info()[0].getDay_info());
		
		week1.setText("周" + JuheInfo.getFuture_info()[1].getWeek());
		week1_info.setText(JuheInfo.getFuture_info()[1].getDay_info());
		week1_day.setText(JuheInfo.getFuture_info()[1].getDay_temperature0() + "~" + JuheInfo.getFuture_info()[1].getDay_temperature2());
		week1_night.setText(JuheInfo.getFuture_info()[1].getNight_temperature0() + "~" + JuheInfo.getFuture_info()[1].getNight_temperature2());
		
		week2.setText("周" +JuheInfo.getFuture_info()[2].getWeek());
		week2_info.setText(JuheInfo.getFuture_info()[2].getDay_info());
		week2_day.setText(JuheInfo.getFuture_info()[2].getDay_temperature0() + "~" + JuheInfo.getFuture_info()[2].getDay_temperature2());
		week2_night.setText(JuheInfo.getFuture_info()[2].getNight_temperature0() + "~" + JuheInfo.getFuture_info()[2].getNight_temperature2());
		
		week3.setText("周" + JuheInfo.getFuture_info()[3].getWeek());
		week3_info.setText(JuheInfo.getFuture_info()[3].getDay_info());
		week3_day.setText(JuheInfo.getFuture_info()[3].getDay_temperature0() + "~" + JuheInfo.getFuture_info()[3].getDay_temperature2());
		week3_night.setText(JuheInfo.getFuture_info()[3].getNight_temperature0() + "~" + JuheInfo.getFuture_info()[3].getNight_temperature2());
		
		Intent intent = new Intent(this, Updateservice.class);
		startService(intent);
	}
	
	
	private void initviews() {
		//direct;
		wind =  (TextView) findViewById(R.id.tv_realtime_wind);
		//date;
		realtime_info =  (TextView) findViewById(R.id.tv_realtime_describe);
		realtime_temperature  = (TextView) findViewById(R.id.tv_realtime_temperature);
		kongtiao0 = (TextView) findViewById(R.id.kongtiao0);
		kongtiao1 = (TextView) findViewById(R.id.kongtiao1);
		yundong0 = (TextView) findViewById(R.id.yundong0);
		yundong1 = (TextView) findViewById(R.id.yundong1);
		ziwaixian0 = (TextView) findViewById(R.id.ziwaixian0);
		ziwaixian1 = (TextView) findViewById(R.id.ziwaixian1);
		ganmao0 = (TextView) findViewById(R.id.ganmao0);
		ganmao1 = (TextView) findViewById(R.id.ganmao1);
		xiche0 = (TextView) findViewById(R.id.xiche0);
		xiche1 = (TextView) findViewById(R.id.xiche1);
		wuran0 = (TextView) findViewById(R.id.wuran0);
		wuran1 = (TextView) findViewById(R.id.wuran1);
		chuanyi0 = (TextView) findViewById(R.id.chuanyi0);
		chuanyi1 = (TextView) findViewById(R.id.chuanyi1);
		pm25 = (TextView) findViewById(R.id.tv_pm25_data);
		pm25_quality = (TextView) findViewById(R.id.tv_pm25_quality);
		//pm25_des;
		week0 = (TextView) findViewById(R.id.tv_future_week_0);
		week0_info = (TextView) findViewById(R.id.tv_future_info_0);
		week0_day= (TextView) findViewById(R.id.tv_future_day_0);
		week0_night = (TextView) findViewById(R.id.tv_future_night_0);
		week1 = (TextView) findViewById(R.id.tv_future_week_1);
		week1_info = (TextView) findViewById(R.id.tv_future_info_1);
		week1_day =(TextView) findViewById(R.id.tv_future_day_1);
		week1_night = (TextView) findViewById(R.id.tv_future_night_1);
		week2 = (TextView) findViewById(R.id.tv_future_week_2);
		week2_info = (TextView) findViewById(R.id.tv_future_info_2);
		week2_day= (TextView) findViewById(R.id.tv_future_day_2);
		week2_night = (TextView) findViewById(R.id.tv_future_night_2);
		week3 = (TextView) findViewById(R.id.tv_future_week_3);
		week3_info = (TextView) findViewById(R.id.tv_future_info_3);
		week3_day= (TextView) findViewById(R.id.tv_future_day_3);
		week3_night = (TextView) findViewById(R.id.tv_future_night_3);
	}
	
	public static void startMeWithContryName(Context mContext,String contryName){
		
		Intent intent = new Intent(mContext, WeatherPageJuheActivity.class);
		intent.putExtra(intentKey_contry, contryName);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mContext.startActivity(intent);
	}
	
}
