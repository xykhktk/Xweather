package com.xweather.bean;

import java.io.Serializable;

public class InfoToAppwidget implements Serializable{

	private static final long serialVersionUID = 123L;
	private String locate;
	private String temp_hight;
	private String temp_low;
	private String desc;
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}
	public String getLocate() {
		return locate;
	}
	public String getTemp_hight() {
		return temp_hight;
	}
	public String getTemp_low() {
		return temp_low;
	}
	public void setLocate(String locate) {
		this.locate = locate;
	}
	public void setTemp_hight(String temp_hight) {
		this.temp_hight = temp_hight;
	}
	public void setTemp_low(String temp_low) {
		this.temp_low = temp_low;
	}
}
