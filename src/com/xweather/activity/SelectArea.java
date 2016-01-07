package com.xweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.xweather.R;
import com.xweather.bean.City;
import com.xweather.bean.Country;
import com.xweather.bean.Province;
import com.xweather.net.HttpCallbackListener;
import com.xweather.net.HttpUtil;
import com.xweather.util.ParseResponUtil;
import com.xweather.util.WeatherDb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectArea extends Activity{

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	
	private ProgressDialog progressdialog;
	//private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDb weatherDb;
	private List<String> dataList = new ArrayList<String>();	//
	
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;
	
	private Province selectedProvince;
	private City selectedCity;
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
/*		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
		boolean b = pre.getBoolean("city_selected", false);
		if (pre.getBoolean("city_selected", false)){
				Intent intent = new Intent(this, WeatherPageActivity.class);
				startActivity(intent);
				return;
			}*/
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_area);
		
		listView = (ListView) findViewById(R.id.select_area_listview);
		//titleText = (TextView) findViewById(R.id.select_area_titletext);
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		
		weatherDb = WeatherDb.getInstance(this);
		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(arg2);
					queryCity();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(arg2);
					queryCountry();
				}else if(currentLevel == LEVEL_COUNTRY){
					String countryCode = countryList.get(arg2).getCountcode();
					Intent intent = new Intent(SelectArea.this, WeatherPageActivity.class);
					intent.putExtra("country_code", countryCode);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			}});
		queryProvince();
	}
	
	private void queryProvince(){
		provinceList = weatherDb.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province p:provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			//titleText.setText("�й�");
			getActionBar().setTitle(getResources().getString(R.string.select_area_china));
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	private void queryCity(){
		cityList = weatherDb.loadCity(selectedProvince.getId());
		if (cityList.size()>0){
			dataList.clear();
			for(City c:cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			//titleText.setText(selectedProvince.getProvinceName());
			getActionBar().setTitle(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	/**
	 * ��
	 */
	private void queryCountry(){
		countryList = weatherDb.loadCountry(selectedCity.getId());
		if (countryList.size()>0){
			dataList.clear();
			for (Country c:countryList){
				dataList.add(c.getCountName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			//titleText.setText(selectedCity.getCityName());
			getActionBar().setTitle(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"country");
		}
	}
	
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(SelectArea.this, 
								getResources().getString(R.string.select_area_failed_request_service), 
								Toast.LENGTH_SHORT).show();
					}
				});
				
			}
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if (type.equals("province")){
					result = ParseResponUtil.handleProvinceResponse(weatherDb, response);
				}else if (type.equals("city")){
					result = ParseResponUtil.handleCityResponse(weatherDb, response, selectedProvince.getId());
				}else if (type.equals("country")){
					result = ParseResponUtil.handleCountryResponse(weatherDb, response, selectedCity.getId());
				}
				
				if(result){
					runOnUiThread(new Runnable() {	//??
						public void run() {
							closeProgressDialog();
							if (type.equals("province")){
								queryProvince();
							}else if (type.equals("city")){
								queryCity();
							}else if (type.equals("country")){
								queryCountry();
							}
						}
					});
					
				}
			}
		});
	}

	private void showProgressDialog(){
		if (progressdialog == null){
			progressdialog = new ProgressDialog(this);
			progressdialog.setMessage(getResources().getString(R.string.select_area_reading_info));
			progressdialog.setCanceledOnTouchOutside(false);
		}
		progressdialog.show();
	}

	public void closeProgressDialog(){
		if (progressdialog != null){
			progressdialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		if (currentLevel == LEVEL_COUNTRY){
			queryCity();
		}else if(currentLevel == LEVEL_CITY){
			queryProvince();
		}else{
			finish();
			//moveTaskToBack(true);
		}
	}
}
