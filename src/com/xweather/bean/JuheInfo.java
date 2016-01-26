package com.xweather.bean;

import java.io.Serializable;

import org.json.JSONObject;

public class JuheInfo implements Serializable{

	private String cityName;
	/**
	 * 风向
	 */
	private String direct;
	/**
	 * 风级
	 */
	private String power;
	/**
	 * 实时信息的发布时间
	 */
	private String date;
	
	/**
	 * "晴"之类的描述
	 */
	private String realtime_info;
	/**
	 * 当前实时温度
	 */
	private String realtime_temperature;
	
	
	//生活上的建议，0是简介，1是具体描述；具体是什么，看拼音吧。
	private String kongtiao0;
	private String kongtiao1;
	private String yundong0;
	private String yundong1;
	private String ziwaixian0;
	private String ziwaixian1;
	private String ganmao0;
	private String ganmao1;
	private String xiche0;
	private String xiche1;
	private String wuran0;
	private String wuran1;
	private String chuanyi0;
	private String chuanyi1;
	
	/**
	 * 未来天气预报
	 */
	private static future_info[] future_info = new future_info[]
			{new future_info(),new future_info(),new future_info(),new future_info()};
	
	private String pm25;
	private String pm25_quality;
	private String pm25_des;
	
	public String getDirect() {
		return direct;
	}

	public void setDirect(String direct) {
		this.direct = direct;
	}

	public String getPower() {
		return power;
	}


	public void setPower(String power) {
		this.power = power;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getKongtiao0() {
		return kongtiao0;
	}

	public void setKongtiao0(String kongtiao0) {
		this.kongtiao0 = kongtiao0;
	}

	public String getKongtiao1() {
		return kongtiao1;
	}

	public void setKongtiao1(String kongtiao1) {
		this.kongtiao1 = kongtiao1;
	}

	public String getYundong0() {
		return yundong0;
	}

	public void setYundong0(String yundong0) {
		this.yundong0 = yundong0;
	}

	public String getYundong1() {
		return yundong1;
	}

	public void setYundong1(String yundong1) {
		this.yundong1 = yundong1;
	}

	public String getZiwaixian0() {
		return ziwaixian0;
	}

	public void setZiwaixian0(String ziwaixian0) {
		this.ziwaixian0 = ziwaixian0;
	}

	public String getZiwaixian1() {
		return ziwaixian1;
	}

	public void setZiwaixian1(String ziwaixian1) {
		this.ziwaixian1 = ziwaixian1;
	}

	public String getGanmao0() {
		return ganmao0;
	}

	public void setGanmao0(String ganmao0) {
		this.ganmao0 = ganmao0;
	}

	public String getGanmao1() {
		return ganmao1;
	}

	public void setGanmao1(String ganmao1) {
		this.ganmao1 = ganmao1;
	}

	public String getXiche0() {
		return xiche0;
	}

	public void setXiche0(String xiche0) {
		this.xiche0 = xiche0;
	}

	public String getXiche1() {
		return xiche1;
	}

	public void setXiche1(String xiche1) {
		this.xiche1 = xiche1;
	}

	public String getWuran0() {
		return wuran0;
	}


	public void setWuran0(String wuran0) {
		this.wuran0 = wuran0;
	}


	public String getWuran1() {
		return wuran1;
	}

	public void setWuran1(String wuran1) {
		this.wuran1 = wuran1;
	}


	public String getChuanyi0() {
		return chuanyi0;
	}

	public void setChuanyi0(String chuanyi0) {
		this.chuanyi0 = chuanyi0;
	}


	public String getChuanyi1() {
		return chuanyi1;
	}


	public void setChuanyi1(String chuanyi1) {
		this.chuanyi1 = chuanyi1;
	}

	public future_info[] getFuture_info() {
		return future_info;
	}

	public void setFuture_info(future_info[] future_info) {
		this.future_info = future_info;
	}


	public String getPm25() {
		return pm25;
	}

	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}


	public String getPm25_quality() {
		return pm25_quality;
	}

	public void setPm25_quality(String pm25_quality) {
		this.pm25_quality = pm25_quality;
	}

	public String getPm25_des() {
		return pm25_des;
	}

	public void setPm25_des(String pm25_des) {
		this.pm25_des = pm25_des;
	}
	
	public void setRealtime_info(String realtime_info) {
		this.realtime_info = realtime_info;
	}
	
	public void setRealtime_temperature(String realtime_temperature) {
		this.realtime_temperature = realtime_temperature;
	}
	
	public String getRealtime_info() {
		return realtime_info;
	}
	
	public String getRealtime_temperature() {
		return realtime_temperature;
	}

	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public static class future_info implements Serializable{
		/**
		 * 0是最低温度，2是最高温度
		 */
		private String night_temperature0;
		private String night_temperature2;
		private String day_temperature0;
		private String day_temperature2;
		private String day_info;
		/**
		 * 星期几
		 */
		private String week;
		
		public String getNight_temperature0() {
			return night_temperature0;
		}
		
		public void setNight_temperature0(String night_temperature) {
			this.night_temperature0 = night_temperature;
		}
		
		public void setNight_temperature2(String night_temperature2) {
			this.night_temperature2 = night_temperature2;
		}
		
		public String getNight_temperature2() {
			return night_temperature2;
		}
		
		public String getDay_temperature0() {
			return day_temperature0;
		}
		
		public void setDay_temperature0(String day_temperature) {
			this.day_temperature0 = day_temperature;
		}
		
		public void setDay_temperature2(String day_temperature2) {
			this.day_temperature2 = day_temperature2;
		}
		
		public String getDay_temperature2() {
			return day_temperature2;
		}
		
		public String getWeek() {
			return week;
		}
		
		public void setWeek(String week) {
			this.week = week;
		}
		
		public String getDay_info() {
			return day_info;
		}
		
		public void setDay_info(String day_info) {
			this.day_info = day_info;
		}
		
	}
	
}
