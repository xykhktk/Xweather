package com.xweather.db;

import java.util.ArrayList;
import java.util.List;

import com.xweather.bean.City;
import com.xweather.bean.Country;
import com.xweather.bean.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDb {

	public static final String DB_NAME = "xweather";
	public static final int DB_VERSON = 1;
	private static WeatherDb weatherDb;
	private SQLiteDatabase sqlDb;
	
	private WeatherDb(Context context) {
		DBOpenHelper deHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSON);
		sqlDb =  deHelper.getWritableDatabase();
	}
	
	public synchronized static WeatherDb getInstance(Context context){
		if (weatherDb == null){
			weatherDb = new WeatherDb(context);
		}
		return weatherDb;
	}
	
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			sqlDb.insert("Province", null, values);	
		}
	}
	
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = sqlDb.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while(cursor.moveToNext());
		}
		return list;
	}
	
	
/*	public void saveProvince2(Province p){
		if(p != null){
			ContentValues cv = new ContentValues();
			cv.put("", p.getProvinceCode());
			cv.put("", p.getProvinceName());
			sqlDb.insert("Province", null, cv);
		}
	}
	
	
	public  List<Province> queryProvince(){
		List<Province> list = new ArrayList<Province>();
		Cursor corsur = sqlDb.query("Province", null, null, null, null, null, null);
		if(corsur.moveToFirst()){
			do{
				Province p = new Province();
				p.setId(corsur.getInt(corsur.getColumnIndex("id")));
				p.setProvinceName(corsur.getString(corsur.getColumnIndex("province_name")));
				p.setProvinceCode(corsur.getString(corsur.getColumnIndex("province_code")));
				list.add(p);
			}while(corsur.moveToNext());
		}
		return list;
	}*/
	
	
	public void saveCity(City city){
		if (city != null){
			ContentValues value = new ContentValues();
			value.put("city_name", city.getCityName());
			value.put("city_code", city.getCityCode());
			value.put("province_id", city.getProvinceId());
			sqlDb.insert("City", null, value);
		}
	}
	
	public List<City> loadCity(int province_id){
		List<City> list = new ArrayList<City>();
		Cursor cursor = sqlDb.query("City", null, "province_id=?", 
				new String[]{String.valueOf(province_id)}, null, null, null);
		if (cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(province_id);
				list.add(city);
			}while(cursor.moveToNext());
		}
		return list;
	}
	
	public void saveCountry(Country country){
		if (country != null){
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountName());
			values.put("country_code", country.getCountcode());
			values.put("city_id", country.getCityId());
			sqlDb.insert("Country", null, values);
		}
	}
	
	
	public List<Country> loadCountry(int city_id){
		 List<Country> list = new ArrayList<Country>();
		 Cursor cursor = sqlDb.query("Country", null, "city_id=?", 
				 new String[]{String.valueOf(city_id)}, null, null, null);
		 if (cursor.moveToFirst()){
			 do{
				 Country c = new Country();
				 c.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				 c.setCountName(cursor.getString(cursor.getColumnIndex("country_name")));
				 c.setCountcode(cursor.getString(cursor.getColumnIndex("country_code")));
				 c.setId(city_id);
				 list.add(c);
			 }while(cursor.moveToNext());
		 }
		 return list;
	}
}
