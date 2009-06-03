/**
 * InitialController.java
 * com.burgess.rtd.controller
 *
 * Created Jun 3, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.controller;

import android.content.SharedPreferences;
import android.util.Log;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.interfaces.view.IInitialView;

public class InitialController {
	private IInitialView view;
	private SharedPreferences preferences;
	
	/**
	 * Constructor for InitialController. Saves a view, and works from there
	 *
	 * @param view	An InitialView activity
	 */
	public InitialController(IInitialView view) {
		this.view = view;
	}
	
	/**
	 * Checks for specific config values, if present, the application is
	 * configured and the controller starts up the main activity.
	 */
	public void initializeView() {
		preferences = view.getPreferences();
		String token = preferences.getString(Program.AUTH_TOKEN, null);
		
		if (token == null) {
			//Application needs to be configured
			Log.d(Program.LOG, "Needs configuring");
		} else {
			//Application has been set up, start main program
			Log.d(Program.LOG, "All set up");
		}
	}
}
