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
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.InitialController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IInitialView;
import com.burgess.rtd.model.TaskSeries;

/**
 * Initial activity which allows the controller to determine whether the app
 * needs to be configured or just continue on to the main activity.
 */
public class InitialActivity extends TabActivity implements IInitialView {
	private static final int CONFIGURE_ACTIVITY = 0;
	
	private InitialController controller;
	private RTDError error;
	
	private OnClickListener errorButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			dismissDialog(Program.Dialog.ERROR);
			finish();
		}
	};
	
	private class TaskCursorAdapter extends CursorAdapter {

		public TaskCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			String name = c.getString(c.getColumnIndex(TaskSeries.NAME));
			//String due = c.getString(c.getColumnIndex(Task.DUE_DATE));
			
			TextView tv = (TextView) view.findViewById(R.id.name);
			tv.setText(name);
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.initial_row, parent, false);
			
			String name = c.getString(c.getColumnIndex(TaskSeries.NAME));
			//String due = c.getString(c.getColumnIndex(Task.DUE_DATE));
			
			TextView tv = (TextView) view.findViewById(R.id.name);
			tv.setText(name);
			
			return view;
		}
		
	}
	
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
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case CONFIGURE_ACTIVITY:
				controller.initializeView();
				break;
			default:
				break;
		}
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
		startActivityForResult(intent, CONFIGURE_ACTIVITY);	
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

	@Override
	public void setTasksDueToday(Cursor tasks) {
		ListView view = (ListView) findViewById(R.id.tab1);
		view.setAdapter(new TaskCursorAdapter(this, tasks));
	}
	
	@Override
	public void setTasksDueTomorrow(Cursor tasks) {
		ListView view = (ListView) findViewById(R.id.tab2);
		view.setAdapter(new TaskCursorAdapter(this, tasks));
	}
	
	@Override
	public void setTasksOverdue(Cursor tasks) {
		ListView view = (ListView) findViewById(R.id.tab3);
		view.setAdapter(new TaskCursorAdapter(this, tasks));
	}
}