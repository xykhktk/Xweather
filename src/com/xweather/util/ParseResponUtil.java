package com.xweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.xweather.R;
import com.xweather.bean.City;
import com.xweather.bean.Country;
import com.xweather.bean.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class ParseResponUtil {
	
	/**
	 *信息格式   01|北京,02|上海,03|天津,04|...
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
	 * 信息格式   190401|苏州,190402|常熟...
	 */
	public static boolean handleCityResponse(WeatherDb weatherDb,String response,int provinceId){
		if (!TextUtils.isEmpty(response)){
			String[] allCity = response.split(",");
			for (String c:allCity){
				String[] s = c.split("\\|"); //\\��ת��
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
	 * 
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
	
	/**
	 * {"weatherinfo":{"city":"昆山","cityid":"101190404","temp1":"20℃","temp2":"11℃","weather":"小雨","img1":"d7.gif","img2":"n7.gif","ptime":"08:00"}}
	 */
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
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherdesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo(Context context,String cityName,String weathercode,
			String temp1,String temp2,String weatherDesp,String publishTime){
		
		//SimpleDateFormat sdf = new SimpleDateFormat(context.getResources().getString(R.string.simpledateformat), Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);	
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(Consts.SPKey_City_selected, true);
		editor.putString(Consts.SPKey_City_name, cityName);
		editor.putString(Consts.SPKey_Weather_code, weathercode);
		editor.putString(Consts.SPKey_Temp1, temp1);
		editor.putString(Consts.SPKey_Temp2, temp2);
		editor.putString(Consts.SPKey_Weather_desp, weatherDesp);
		editor.putString(Consts.SPKey_Publish_time, publishTime);
		editor.putString(Consts.SPKey_Current_date, sdf.format(new Date()));
		LogUtil.getInstance().info("current_date" + sdf.format(new Date()));
		editor.commit();
		
		
		
	}
	
}
