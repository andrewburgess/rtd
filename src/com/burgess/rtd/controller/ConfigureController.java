/**
 * ConfigureController.java
 * com.burgess.rtd.controller
 *
 * Created Jun 7, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.interfaces.view.IConfigureView;
import com.burgess.rtd.service.SyncService;

public class ConfigureController {
	private static final int FINISHED_SYNC = 1;
	
	private IConfigureView view;
	private String token;
	private String username;
	private SharedPreferences preferences;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FINISHED_SYNC:
					view.setLastSync(preferences.getString(Program.Config.LAST_SYNC, null));
					view.removeDialog(Program.Dialog.SYNCHRONIZE);
					break;
			}
		}
	};
	
	private class SyncThread extends Thread {
		@Override
		public void run() {
			SyncService service = new SyncService(view.getContext());
			service.synchronize();
			
			Message m = new Message();
			m.what = FINISHED_SYNC;
			handler.sendMessage(m);
		}
	}
	
	private SyncThread syncThread;
	
	public ConfigureController(IConfigureView view) {
		this.view = view;
	}
	
	public void initializeView() {
		preferences = view.getPreferences();
		
		refreshAuthStatus();
		populateConfiguration();
	}
	
	public void refreshAuthStatus() {
		token = preferences.getString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
		username = preferences.getString(Program.Config.USERNAME, "");
		
		if (token == Program.DEFAULT_AUTH_TOKEN) {
			view.setAuthStatus("Not authenticated");
		} else {
			view.setAuthStatus(username + " logged in");
		}
	}
	
	public void saveConfiguration() {
		if (preferences == null) preferences = view.getPreferences();
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(Program.Config.SYNC_TIME, view.getSyncTime());
		editor.commit();
		
		//Set a new sync time, start up a new alarm
		if (view.getSyncTime() != Program.Config.SyncValues.MANUALLY) {
			AlarmManager alarm = (AlarmManager)view.getContext().getSystemService(Context.ALARM_SERVICE);
			long interval;
			switch (view.getSyncTime()) {
				case Program.Config.SyncValues.DAY:
					interval = 24 * 3600 * 1000;
					break;
				case Program.Config.SyncValues.HOUR:
					interval = 3600 * 1000;
					break;
				case Program.Config.SyncValues.SIX_HOURS:
					interval = 6 * 3600 * 1000;
					break;
				case Program.Config.SyncValues.TWELVE_HOURS:
					interval = 12 * 3600 * 1000;
					break;
				case Program.Config.SyncValues.WEEK:
					interval = 7 * 24 * 3600 * 1000;
					break;
				default:
					return;
			}
			
			Log.i(Program.LOG, "Going off every: " + interval / 1000 + " seconds");
			
			Intent i = new Intent(view.getContext(), SyncService.class);
			PendingIntent intent = PendingIntent.getBroadcast(view.getContext(), Program.Request.SYNC, i, PendingIntent.FLAG_CANCEL_CURRENT);
			alarm.cancel(intent);
			alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, interval, intent);
		}
	}
	
	public void synchronize() {
		view.showDialog(Program.Dialog.SYNCHRONIZE);
		syncThread = new SyncThread();
		syncThread.start();
	}
	
	private void populateConfiguration() {
		if (preferences == null) preferences = view.getPreferences();
		
		view.setSyncTime(preferences.getInt(Program.Config.SYNC_TIME, Program.Config.SyncValues.MANUALLY));
		view.setLastSync(preferences.getString(Program.Config.LAST_SYNC, null));
	}
}
