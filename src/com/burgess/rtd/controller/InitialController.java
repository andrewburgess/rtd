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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.interfaces.view.IInitialView;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.Task;
import com.burgess.rtd.model.TaskSeries;

public class InitialController {
	private IInitialView view;
	private SharedPreferences preferences;
	private Database dbHelper;
	private SQLiteDatabase db;
	
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
		String token = preferences.getString(Program.Config.AUTH_TOKEN, null);
		
		if (token == null) {
			//Assume first run
			buildDatabase();
			
			Log.d(Program.LOG, "Needs configuring");
			view.launchConfigureActivity();
		} else {
			SharedPreferences.Editor edit = preferences.edit();
			edit.putString(Program.Config.LAST_SYNC, "");
			edit.commit();
			
			dbHelper = new Database(view.getContext());
			try {
				db = dbHelper.open().getDb();
			} catch (RTDException e) {
				view.createErrorDialog(e.error);
				return;
			}
			
			getTasksDueToday();
			getTasksDueTomorrow();
			getTasksOverdue();
			
			dbHelper.close();
		}
	}
	
	private void buildDatabase() {
		dbHelper = new Database(view.getContext());
		try {
			dbHelper.open();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
		dbHelper.close();
	}
	
	private void getTasksDueToday() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long time = Calendar.getInstance().getTimeInMillis();
		time = time - TimeZone.getDefault().getRawOffset();
		String dstart = df.format(time);
		
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTimeInMillis(df.parse(df.format(time)).getTime());
		} catch (ParseException e) {
			RTDError error = new RTDError(Program.Error.PARSE_EXCEPTION, R.string.error_parse_default, true);
			view.createErrorDialog(error);
		}
		cal.add(Calendar.DATE, 1);
		String dend = df.format(cal.getTimeInMillis());
		
		view.setTasksDueToday(db.query(TaskSeries.TABLE + ", " + Task.TABLE,
									   new String[] {TaskSeries.TABLE + "." + TaskSeries.ID, TaskSeries.NAME, Task.DUE_DATE},
									   Task.DUE_DATE + ">=? AND " + Task.DUE_DATE + "<? AND " +
									   Task.COMPLETED + " is NULL AND " + 
									   TaskSeries.TABLE + "." + TaskSeries.ID + "=" + Task.TABLE + "." + Task.TASK_SERIES_ID,
									   new String[] {dstart, dend}, null, null, Task.DUE_DATE + " ASC"));
	}
	
	private void getTasksDueTomorrow() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long time = Calendar.getInstance().getTimeInMillis();
		time = time - TimeZone.getDefault().getRawOffset();
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTimeInMillis(df.parse(df.format(time)).getTime());
		} catch (ParseException e) {
			RTDError error = new RTDError(Program.Error.PARSE_EXCEPTION, R.string.error_parse_default, true);
			view.createErrorDialog(error);
		}
		cal.add(Calendar.DATE, 1);
		String dstart = df.format(cal.getTimeInMillis());
		cal.add(Calendar.DATE, 1);
		String dend = df.format(cal.getTimeInMillis());
		
		view.setTasksDueTomorrow(db.query(TaskSeries.TABLE + ", " + Task.TABLE,
									   new String[] {TaskSeries.TABLE + "." + TaskSeries.ID, TaskSeries.NAME, Task.DUE_DATE},
									   Task.DUE_DATE + ">=? AND " + Task.DUE_DATE + "<? AND " +
									   Task.COMPLETED + " is NULL AND " + 
									   TaskSeries.TABLE + "." + TaskSeries.ID + "=" + Task.TABLE + "." + Task.TASK_SERIES_ID,
									   new String[] {dstart, dend}, null, null, Task.DUE_DATE + " ASC"));
	}
	
	private void getTasksOverdue() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long time = Calendar.getInstance().getTimeInMillis();
		time = time - TimeZone.getDefault().getRawOffset();

		Calendar cal = Calendar.getInstance();
		try {
			cal.setTimeInMillis(df.parse(df.format(time)).getTime());
		} catch (ParseException e) {
			RTDError error = new RTDError(Program.Error.PARSE_EXCEPTION, R.string.error_parse_default, true);
			view.createErrorDialog(error);
		}
		String dend = df.format(cal.getTimeInMillis());
		
		view.setTasksOverdue(db.query(TaskSeries.TABLE + ", " + Task.TABLE,
									   new String[] {TaskSeries.TABLE + "." + TaskSeries.ID, TaskSeries.NAME, Task.DUE_DATE},
									   Task.DUE_DATE + "<? AND " + Task.COMPLETED + " is NULL AND " + 
									   TaskSeries.TABLE + "." + TaskSeries.ID + "=" + Task.TABLE + "." + Task.TASK_SERIES_ID,
									   new String[] {dend}, null, null, Task.DUE_DATE + " ASC"));
	}
}
