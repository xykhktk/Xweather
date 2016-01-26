package com.xweather.appwidgetprovider;

import com.example.xweather.R;
import com.xweather.activity.WeatherPageActivity;
import com.xweather.activity.WeatherPageJuheActivity;
import com.xweather.bean.InfoToAppwidget;
import com.xweather.util.Consts;
import com.xweather.util.LogUtil;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class XweatherAppWidgetProvider extends AppWidgetProvider{
	
	public final String borcast = "com.xweather.appwidgetprovider";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(context, XweatherAppWidgetProvider.class);
		intent.setAction(borcast);
		
		PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidgetlayout);
		
		remoteViews.setOnClickPendingIntent(R.id.appwidgetlayout, pIntent);
		remoteViews.setOnClickPendingIntent(R.id.tv_locate_appwiggetlayout, pIntent);
		remoteViews.setOnClickPendingIntent(R.id.tv_temp_appwiggetlayout, pIntent);
		remoteViews.setOnClickPendingIntent(R.id.tv_weatherdesc_appwiggetlayout, pIntent);
		
		SharedPreferences sp = context.getSharedPreferences(Consts.SP_Name, Context.MODE_PRIVATE);
		remoteViews.setTextViewText(R.id.tv_locate_appwiggetlayout, sp.getString(Consts.SPKey_City_name, ""));
		remoteViews.setTextViewText(R.id.tv_temp_appwiggetlayout, 
				sp.getString(Consts.SPKey_Temp1, "") + "°~" + sp.getString(Consts.SPKey_Temp2, "") + "°");
		remoteViews.setTextViewText(R.id.tv_weatherdesc_appwiggetlayout,
				sp.getString(Consts.SPKey_Weather_desp, ""));
		
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
		
		LogUtil.getInstance().info(getClass().getName() + " onUpdate ");
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.getInstance().info(getClass().getName() + " a broacast ");
		if(intent.getAction().equals(borcast)){
			
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidgetlayout);
			SharedPreferences sp = context.getSharedPreferences(Consts.SP_Name, Context.MODE_PRIVATE);
			LogUtil.getInstance().info(getClass().getName() + "  temp " + sp.getString(Consts.SPKey_Temp2, ""));
			remoteViews.setTextViewText(R.id.tv_locate_appwiggetlayout, sp.getString(Consts.SPKey_City_name, ""));
			remoteViews.setTextViewText(R.id.tv_temp_appwiggetlayout, 
					sp.getString(Consts.SPKey_Temp1, "") + "°~" +  sp.getString(Consts.SPKey_Temp2, "") +"°");
			remoteViews.setTextViewText(R.id.tv_weatherdesc_appwiggetlayout,
					sp.getString(Consts.SPKey_Weather_desp, ""));
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			ComponentName componentName = new ComponentName(context,XweatherAppWidgetProvider.class );
			appWidgetManager.updateAppWidget(componentName, remoteViews);
			
			Intent i = new Intent(context, WeatherPageJuheActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(i);
			
		};
		
		super.onReceive(context, intent);
	}
	
	
}
