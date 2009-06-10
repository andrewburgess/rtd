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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	private Context context = this;
	private Button authenticateButton;
	private TextView authstatus;
	
	private OnClickListener authenticateButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, AuthenticateActivity.class);
			startActivityForResult(intent, 0);			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		setContentView(R.layout.configure);
		
		populateSpinner();
		
		authenticateButton = (Button) findViewById(R.id.authbutton);
		authenticateButton.setOnClickListener(authenticateButtonOnClickListener);
		authstatus = (TextView) findViewById(R.id.authstatus);

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


	@Override
	public void setAuthStatus(String status) {
		authstatus.setText(status);
	}
}
