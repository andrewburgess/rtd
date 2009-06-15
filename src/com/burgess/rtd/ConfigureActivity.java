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
import android.app.Dialog;
import android.app.ProgressDialog;
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
	private static final int REQUEST_AUTHENTICATE = 0;
	
	private ConfigureController controller;
	private Context context = this;
	private Button btnAuthenticate;
	private TextView tvAuthStatus;
	private Spinner spinSync;
	
	private OnClickListener authenticateButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, AuthenticateActivity.class);
			startActivityForResult(intent, REQUEST_AUTHENTICATE);			
		}
	};
	
	private OnClickListener saveOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			controller.saveConfiguration();
			finish();
		}
	};
	
	private OnClickListener cancelOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			finish();
		}
	};
	
	private OnClickListener syncOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			controller.synchronize();
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
			case Program.Dialog.SYNCHRONIZE:
				dialog = new ProgressDialog(this);
				((ProgressDialog)dialog).setMessage("Synchronizing");
				dialog.setTitle("Hold Your Horses");
				return dialog;
			default:
				return null;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		setContentView(R.layout.configure);
		
		populateSpinner();
		
		btnAuthenticate = (Button) findViewById(R.id.authbutton);
		btnAuthenticate.setOnClickListener(authenticateButtonOnClickListener);
		tvAuthStatus = (TextView) findViewById(R.id.authstatus);
		
		Button btn = (Button) findViewById(R.id.save);
		btn.setOnClickListener(saveOnClickListener);
		
		btn = (Button) findViewById(R.id.cancel);
		btn.setOnClickListener(cancelOnClickListener);
		
		btn = (Button) findViewById(R.id.sync);
		btn.setOnClickListener(syncOnClickListener);

		controller = new ConfigureController(this);
		controller.initializeView();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_AUTHENTICATE:
				controller.refreshAuthStatus();
				break;
			default:
				break;
		}
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
			authstatus.setText(getPreferences().getString(Program.Config.AUTH_TOKEN, ""));
		} else {
			authbutton.setText("Authenticate");
			authstatus.setText("Not authenticated");
		}
	}
	
	private void populateSpinner() {
		spinSync = (Spinner) findViewById(R.id.spinner);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.sync_types, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinSync.setAdapter(adapter);
	}


	@Override
	public void setAuthStatus(String status) {
		tvAuthStatus.setText(status);
	}

	@Override
	public int getSyncTime() {
		return (int) spinSync.getSelectedItemId();
	}

	@Override
	public void setSyncTime(int value) {
		spinSync.setSelection(value);		
	}

	@Override
	public Context getContext() {
		return this;
	}
}
