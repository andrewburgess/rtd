/**
 * ConfigureActivity.java
 * com.burgess.rtd
 *
 * Created Jun 4, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ConfigureController;
import com.burgess.rtd.interfaces.view.IConfigureView;

/**
 * Allows the user to configure the application
 */
public class ConfigureActivity extends Activity implements IConfigureView {
	private ConfigureController controller;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.configure);
		
		populateSpinner();

		controller = new ConfigureController(this);
		controller.initializeView();
	}


	@Override
	public SharedPreferences getPreferences() {
		SharedPreferences prefs = this.getSharedPreferences(Program.APPLICATION, 0);
		return prefs;
	}
	
	@Override
	public void configureAuthentication(boolean isAuthenticated) {
		Button authbutton = (Button)findViewById(R.id.authbutton);
		TextView authstatus = (TextView)findViewById(R.id.authstatus);
		if (isAuthenticated) {
			authbutton.setText("Re-authenticate");
			authstatus.setText(getPreferences().getString(Program.AUTH_TOKEN, ""));
		} else {
			authbutton.setText("Authenticate");
			authstatus.setText("Not authenticated");
		}
	}
	
	private void populateSpinner() {
		Spinner s = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.sync_types, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    s.setAdapter(adapter);
	}
}
