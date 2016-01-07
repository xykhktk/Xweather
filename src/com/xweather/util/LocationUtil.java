package com.xweather.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationUtil {
	
	private static LocationUtil locationUtil= null;
	private double latitude;
	private double longitude;
	private Geocoder geocoder;
	private String addr;
	private String weatherCode;
	
	private LocationUtil(){
		
	}
	
	public static LocationUtil getInstance(){
		if (locationUtil == null){
			locationUtil = new LocationUtil();
		}
		return locationUtil;
	}
	
	public LocationUtil getLatLng(Context context){
		
		getInstance();
		LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		String provider = null;
		List<String> providers = manager.getAllProviders();
		if(providers.contains(LocationManager.NETWORK_PROVIDER)){
			provider = LocationManager.NETWORK_PROVIDER;
		}else if(providers.contains(LocationManager.GPS_PROVIDER)){
			provider = LocationManager.GPS_PROVIDER;
		}else{
			return null;
		}
		
		//LogUtil.getInstance().info(getClass().getName() + " provider: "+ provider);
		Location location = manager.getLastKnownLocation(provider);
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		//LogUtil.getInstance().info(getClass().getName() + " latitude: "+ latitude + " longitude " +longitude  );
		
		geocoder = new Geocoder(context, Locale.CHINESE);
		List<Address> address = new ArrayList<Address>();
		
		try {
			address = geocoder.getFromLocation(latitude, longitude, 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (address.size() > 0){
			extractLocality(address.toString());
			extractCityCode();
			//LogUtil.getInstance().info(getClass().getName() + " address.size():" + address.size()+ " address: "+ address.toString() );
		}
		
		manager.requestLocationUpdates(provider, 50*1000, 10, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		});
		
		return locationUtil;
	}
	
	private void extractCityCode() {
		Pattern p = Pattern.compile(addr + ":\\d{9}");
		Matcher m = p.matcher(Consts.City_Name_Code);
		while(m.find()){
			weatherCode = m.group().substring(3);
			LogUtil.getInstance().info(getClass().getName() + " CityCode:" + weatherCode);
		}
		
		
	}

	private void extractLocality(String s){
		Pattern p = Pattern.compile("locality=.*,thoroughfare");
		Matcher m = p.matcher(s);
		while(m.find()){
			addr = m.group();
			addr = addr.substring(9, addr.length()-14);
			LogUtil.getInstance().info(getClass().getName() + " addr:" + addr);
			break;
		}
		
		
	}
	
	public String getAddr() {
		return addr;
	}
	
	public String getWeatherCode() {
		return weatherCode;
	}
}