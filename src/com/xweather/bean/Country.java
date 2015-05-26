package com.xweather.bean;

public class Country {

	private int id;
	private String countName;
	private String countcode;
	private int cityId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCountName() {
		return countName;
	}
	public void setCountName(String countName) {
		this.countName = countName;
	}
	public String getCountcode() {
		return countcode;
	}
	public void setCountcode(String countcode) {
		this.countcode = countcode;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
}
