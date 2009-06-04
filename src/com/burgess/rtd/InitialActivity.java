/**
 * InitialActivity.java
 * com.burgess.rtd
 *
 * Created Jun 3, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.InitialController;
import com.burgess.rtd.interfaces.view.IInitialView;

/**
 * Initial activity which allows the controller to determine whether the app
 * needs to be configured or just continue on to the main activity.
 */
public class InitialActivity extends Activity implements IInitialView {
	private InitialController controller;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial);
        
        controller = new InitialController(this);
        controller.initializeView();
    }

	@Override
	public SharedPreferences getPreferences() {
		return this.getSharedPreferences(Program.APPLICATION, 0);
	}
}