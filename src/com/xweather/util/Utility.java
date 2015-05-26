package com.xweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.xweather.bean.City;
import com.xweather.bean.Country;
import com.xweather.bean.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
	
	//数据类似于  01|北京,02|上海,03|天津,04|
	//190401|苏州,190402|常熟,
	/*{"weatherinfo": 
    {"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃",
    "weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}*/
	/**
	 *根据response解析和保存省一级信息
	 */
	public synchronized static boolean handleProvinceResponse(WeatherDb weatherDb,String response){
		
		if( !TextUtils.isEmpty(response)){
			String[] allProvince = response.split(",");	
			if (allProvince != null && allProvince.length>0){
				for (String p:allProvince){
					String[] array = p.split("\\|");
					Province p1 = new Province();
					p1.setProvinceCode(array[0]);
					p1.setProvinceName(array[1]);
					weatherDb.saveProvince(p1);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和保存市一级信息
	 */
	public static boolean handleCityResponse(WeatherDb weatherDb,String response,int provinceId){
		if (!TextUtils.isEmpty(response)){
			String[] allCity = response.split(",");
			for (String c:allCity){
				String[] s = c.split("\\|"); //\\是转义
				City c2 = new City();
				c2.setCityCode(s[0]);
				c2.setCityName(s[1]);
				c2.setProvinceId(provinceId);
				weatherDb.saveCity(c2);
			}
			return true;
		}
		return false;
	}
	
	
	/**
	 * 解析和保存   应该是县   一级信息
	 */
	public static boolean handleCountryResponse(WeatherDb weatherDb,String response,int cityId){
		if (!TextUtils.isEmpty(response) ){
			String[] allCountry = response.split(",");
			if (allCountry != null && allCountry.length>0){
				for(String s :allCountry){
					String[] s2 = s.split("\\|");
					Country c = new Country();
					c.setCountcode(s2[0]);
					c.setCountName(s2[1]);
					c.setCityId(cityId);
					weatherDb.saveCountry(c);
				}
				return true;
			}
		}
		return false;
	}
	
	
	public static void handleWeatherResponse(Context context,String response){
		
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherdesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			Log.i("xw","handleWeatherResponse temp1 temp2 :"+temp1+"   "+temp2);
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherdesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo(Context context,String cityName,String weathercode,
			String temp1,String temp2,String weatherDesp,String publishTime){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年m月d日", Locale.CHINA);	//??
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weathercode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
		
		
	}
	
}
