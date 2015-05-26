package com.xweather.receiver;

import com.xweather.service.Updateservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		Intent i = new Intent(arg0, Updateservice.class);
		arg0.startService(i);
	}

}
