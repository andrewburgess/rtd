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

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IConfigureView;
import com.burgess.rtd.service.SyncService;

public class ConfigureController {
	private static final int FINISHED_SYNC = 1;
	
	private IConfigureView view;
	private String token;
	private String username;
	private SharedPreferences preferences;
	private RTDError error;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FINISHED_SYNC:
					if (error == null) {
						view.setLastSync(preferences.getString(Program.Config.LAST_SYNC, null));
						view.removeDialog(Program.Dialog.SYNCHRONIZE);
						break;
					} else {
						view.createErrorDialog(error);
						return;
					}
			}
		}
	};
	
	private class SyncThread extends Thread {
		@Override
		public void run() {
			SyncService service = new SyncService(view.getContext());
			error = service.synchronize();
			
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
		
		token = preferences.getString(Program.Config.AUTH_TOKEN, null);
		if (token == null) {
			SharedPreferences.Editor edit = preferences.edit();
			edit.putString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
			edit.commit();
		}
		
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
		
		SyncService.updateAlarmManager(view.getContext());
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
