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

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.interfaces.view.IConfigureView;

public class ConfigureController {
	private IConfigureView view;
	private String token;
	private String username;
	private SharedPreferences preferences;
	
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
	}
	
	private void populateConfiguration() {
		if (preferences == null) preferences = view.getPreferences();
		
		view.setSyncTime(preferences.getInt(Program.Config.SYNC_TIME, 0));
	}
}
