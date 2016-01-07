package com.xweather.util;

import android.util.Log;

public class LogUtil {

	private static final int nothing = 0;
	private static final int verbose = 1;
	private static final int debug = 2;
	private static final int info = 3;
	private static final int warn = 4;
	private static final int error = 5;
	private static int level = 5;
	private static final String tag = "xweather";
	private static LogUtil mLogUtil;
	
	public static LogUtil getInstance(){
		if(mLogUtil == null){
			synchronized(LogUtil.class){
				mLogUtil = new LogUtil();
			}
		}
		return mLogUtil;
	}
	
	private LogUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public void verbose(String s){
		if(level >= verbose ){
			Log.v(tag, s);
		}
	}
	
	public void debug(String s){
		if(level >= debug ){
			Log.d(tag, s);
		}
	}
	
	public void info(String s){
		if(level >= info ){
			Log.i(tag, s);
		}
	}
	
	public void warn(String s){
		if(level >= warn ){
			Log.w(tag, s);
		}
	}
	
	public void error(String s){
		if(level >= error ){
			Log.e(tag, s);
		}
	}
}
