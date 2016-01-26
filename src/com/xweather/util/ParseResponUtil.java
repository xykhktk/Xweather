package com.xweather.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.xweather.R;
import com.xweather.bean.City;
import com.xweather.bean.Country;
import com.xweather.bean.JuheInfo;
import com.xweather.bean.JuheInfo.future_info;
import com.xweather.bean.Province;
import com.xweather.db.WeatherDb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		LogUtil.getInstance().info(" handleCityResponse response: " + response);
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
		//editor.putBoolean(Consts.SPKey_City_selected, true);
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
	
	/**
	 * 根据聚合数据的接口返回信息，进行处理。
	 * @param response
	 * @param context
	 * @return
	 */
	public static JuheInfo handleJsonResultFromJuhe(String response,Context context){
		
		JuheInfo juheinfo = new JuheInfo();
		
		try {
			JSONObject all = new JSONObject(response);
			JSONObject result = all.getJSONObject("result");
			JSONObject data = result.getJSONObject("data");
			
			
			//获取实时信息
			JSONObject realtime = data.getJSONObject("realtime");
			String time = realtime.getString("time");
			JSONObject wind = realtime.getJSONObject("wind");
			juheinfo.setDirect(wind.getString("direct"));
			juheinfo.setPower(wind.getString("power"));
			juheinfo.setDate(realtime.getString("date"));
			juheinfo.setCityName(realtime.getString("city_name"));
			
			JSONObject weather = realtime.getJSONObject("weather");
			juheinfo.setRealtime_info(weather.getString("info"));
			juheinfo.setRealtime_temperature(weather.getString("temperature"));
			
			//生活上的建议
			JSONObject life = data.getJSONObject("life");
			JSONObject info = life.getJSONObject("info");
			
			String[] tempArray = spiltVelue(info.getString("kongtiao"));
			juheinfo.setKongtiao0(tempArray[0]);
			juheinfo.setKongtiao1(tempArray[1]);
			
			tempArray = spiltVelue(info.getString("yundong"));
			juheinfo.setYundong0(tempArray[0]);
			juheinfo.setYundong1(tempArray[1]);
			
			tempArray = spiltVelue(info.getString("ziwaixian"));
			juheinfo.setZiwaixian0(tempArray[0]);
			juheinfo.setZiwaixian1(tempArray[1]);
			
			tempArray = spiltVelue(info.getString("ganmao"));
			juheinfo.setGanmao0(tempArray[0]);
			juheinfo.setGanmao1(tempArray[1]);
			
			tempArray = spiltVelue(info.getString("xiche"));
			juheinfo.setXiche0(tempArray[0]);
			juheinfo.setXiche1(tempArray[1]);
			
			tempArray = spiltVelue(info.getString("wuran"));
			juheinfo.setWuran0(tempArray[0]);
			juheinfo.setWuran1(tempArray[1]);
			
			tempArray = spiltVelue(info.getString("chuanyi"));
			juheinfo.setChuanyi0(tempArray[0]);
			juheinfo.setChuanyi1(tempArray[1]);
			
			//从今天开始，7天内的信息。只获取4天。
			JSONArray weatherArray = data.getJSONArray("weather");
			for(int i = 0;i < 4 ; i++){
				JSONObject temp = (JSONObject) weatherArray.get(i);
				future_info f = new future_info();
				
				f.setWeek(temp.getString("week"));
				
				JSONObject info_weather = temp.getJSONObject("info");
				String[] temparray = spiltVelue(info_weather.getString("night"));
				f.setNight_temperature0(temparray[0]);
				f.setNight_temperature2(temparray[2]);
				temparray = spiltVelue(info_weather.getString("day"));
				f.setDay_temperature0(temparray[0]);
				f.setDay_temperature2(temparray[2]);
				f.setDay_info(temparray[1]);
				
				juheinfo.getFuture_info()[i] = f;
				
			}
			
			//pm25
			JSONObject pm25 = data.getJSONObject("pm25");
			JSONObject pm25_2 = pm25.getJSONObject("pm25");
			juheinfo.setPm25(pm25_2.getString("pm25"));
			juheinfo.setPm25_quality(pm25_2.getString("quality"));
			juheinfo.setPm25_des(pm25_2.getString("des"));
			
			saveDataForWidgetProvider(context,juheinfo);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return juheinfo;
	}
	
	
	private static void saveDataForWidgetProvider(Context context, JuheInfo juheinfo) {
		Editor editor = context.getSharedPreferences(Consts.SP_Name, Context.MODE_PRIVATE).edit();
		editor.putString(Consts.SPKey_Temp1, juheinfo.getFuture_info()[0].getDay_temperature0());
		editor.putString(Consts.SPKey_Temp2, juheinfo.getFuture_info()[0].getDay_temperature2());
		editor.putString(Consts.SPKey_City_name, juheinfo.getCityName());
		editor.putString(Consts.SPKey_Weather_desp, juheinfo.getRealtime_info());
		editor.commit();
	}

	/**
	 * 解析json，有时候得到的key值是 ["较少开启","您将感到很舒适，一般不需要开启空调。"]
	 */
	private static String[] spiltVelue(String value){
		String[] array = value.split(",");
		for(int i = 0;i < array.length;i++){
			array[i] = array[i].replace("\"", "").replace("[", "").replace("]", "");
		}
		return array;
	}
	
}
