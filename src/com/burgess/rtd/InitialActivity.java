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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.InitialController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IInitialView;
import com.burgess.rtd.model.Task;
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
	
	private OnItemClickListener taskOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i(Program.LOG, "Clicked task: " + id);			
		}
	};
	
	private class TaskCursorAdapter extends CursorAdapter {
		private boolean overdue;
		public TaskCursorAdapter(Context context, Cursor c, boolean overdue) {
			super(context, c);
			
			this.overdue = overdue;
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			String name = c.getString(c.getColumnIndex(TaskSeries.NAME));
			String due = c.getString(c.getColumnIndex(Task.DUE_DATE));
			String priority = c.getString(c.getColumnIndex(Task.PRIORITY));
			
			TextView tv = (TextView) view.findViewById(R.id.name);
			tv.setText(name);
			
			if (c.getInt(c.getColumnIndex(Task.HAS_DUE_TIME)) > 0 && !overdue) {
				SimpleDateFormat df = new SimpleDateFormat("h:mma");
				Date date = new Date();
				try {
					date = Program.DATE_FORMAT.parse(due);
				} catch (ParseException e) {
					
				}
				
				long time = date.getTime();
				time = time + TimeZone.getDefault().getOffset(time);
				date.setTime(time);
				
				tv = (TextView) view.findViewById(R.id.due);
				tv.setText(df.format(date));
			}
			
			if (overdue) {
				SimpleDateFormat df = new SimpleDateFormat("M/dd");
				Date date = new Date();
				try {
					date = Program.DATE_FORMAT.parse(due);
				} catch (ParseException e) {
					
				}
				
				long time = date.getTime();
				time = time + TimeZone.getDefault().getOffset(time);
				date.setTime(time);
				
				tv = (TextView) view.findViewById(R.id.due);
				tv.setText(df.format(date));
			}
			
			tv = (TextView) view.findViewById(R.id.priority);
			if (priority.equals("1"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.high_priority)));
			else if (priority.equals("2"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.medium_priority)));
			else if (priority.equals("3"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.low_priority)));
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.initial_row, parent, false);
			
			view.setId(c.getInt(c.getColumnIndex(TaskSeries.ID)));
			
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
        
        setTitle("Remember the Droid :: Overview");
        
        TabHost th = getTabHost();
        th.addTab(th.newTabSpec("tab_today").setIndicator("Today").setContent(R.id.tab1));
        th.addTab(th.newTabSpec("tab_tomorrow").setIndicator("Tomorrow").setContent(R.id.tab2));
        th.addTab(th.newTabSpec("tab_overdue").setIndicator("Overdue").setContent(R.id.tab3));
        
        controller = new InitialController(this);
        controller.initializeView();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	
    	controller.stop();
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
    	menu.add(0, Program.Menu.LISTS, 0, "Go to Tasks");
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case Program.Menu.CONFIGURE:
    			launchConfigureActivity();
    			return true;
    		case Program.Menu.LISTS:
    			Intent intent = new Intent(this, ListsActivity.class);
    			startActivity(intent);
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
		ListView view = (ListView) findViewById(R.id.today);
		view.setEmptyView(findViewById(R.id.tab1).findViewById(android.R.id.empty));
		view.setAdapter(new TaskCursorAdapter(this, tasks, false));
		view.setOnItemClickListener(taskOnItemClickListener);
	}

	@Override
	public void setTasksDueTomorrow(Cursor tasks) {
		ListView view = (ListView) findViewById(R.id.tomorrow);
		view.setEmptyView(findViewById(R.id.tab2).findViewById(android.R.id.empty));
		view.setAdapter(new TaskCursorAdapter(this, tasks, false));
		view.setOnItemClickListener(taskOnItemClickListener);
	}
	
	@Override
	public void setTasksOverdue(Cursor tasks) {
		ListView view = (ListView) findViewById(R.id.overdue);
		view.setEmptyView(findViewById(R.id.tab3).findViewById(android.R.id.empty));
		view.setAdapter(new TaskCursorAdapter(this, tasks, true));
		view.setOnItemClickListener(taskOnItemClickListener);
	}
}