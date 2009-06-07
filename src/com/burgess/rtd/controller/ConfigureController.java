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
	
	public ConfigureController(IConfigureView view) {
		this.view = view;
	}
	
	public void initializeView() {
		SharedPreferences preferences = view.getPreferences();
		
		if (preferences.getString(Program.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN) == Program.DEFAULT_AUTH_TOKEN) {
			
		}
	}
}
