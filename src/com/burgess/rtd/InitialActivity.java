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

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.InitialController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IInitialView;

/**
 * Initial activity which allows the controller to determine whether the app
 * needs to be configured or just continue on to the main activity.
 */
public class InitialActivity extends TabActivity implements IInitialView {
	private InitialController controller;
	private RTDError error;
	
	private OnClickListener errorButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			dismissDialog(Program.Dialog.ERROR);
			finish();
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
			case Program.Dialog.ERROR:
				dialog = new Dialog(this);
				dialog.setContentView(R.layout.error_dialog);
				dialog.setTitle("Error #" + error.errorCode + " occurred");
				
				TextView tv = (TextView) dialog.findViewById(R.id.error_text);
				tv.setText(error.errorMessageId);
				
				if (!error.showIssueUrl) {
					TextView url = (TextView) dialog.findViewById(R.id.issue_url);
					url.setVisibility(View.INVISIBLE);
				}
				
				Button btn = (Button) dialog.findViewById(R.id.error_button);
				btn.setOnClickListener(errorButtonOnClickListener);
				
				return dialog;
			default:
				return null;
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial);
        
        TabHost th = getTabHost();
        th.addTab(th.newTabSpec("tab_today").setIndicator("Today").setContent(R.id.tab1));
        th.addTab(th.newTabSpec("tab_tomorrow").setIndicator("Tomorrow").setContent(R.id.tab2));
        th.addTab(th.newTabSpec("tab_overdue").setIndicator("Overdue").setContent(R.id.tab3));
        
        controller = new InitialController(this);
        controller.initializeView();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, Program.Menu.CONFIGURE, 0, "Configure");
    	
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case Program.Menu.CONFIGURE:
    			launchConfigureActivity();
    			return true;
    		default:
    			return false;
    	}
    }

	@Override
	public SharedPreferences getPreferences() {
		return this.getSharedPreferences(Program.APPLICATION, 0);
	}

	@Override
	public void launchConfigureActivity() {
		Intent intent = new Intent(this, ConfigureActivity.class);
		startActivity(intent);	
	}
	
	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public void createErrorDialog(RTDError error) {
		this.error = error;
		showDialog(Program.Dialog.ERROR);
	}
}