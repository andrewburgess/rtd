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
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.SharedPreferences;
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
			dbHelper = new Database(view.getContext());
			try {
				dbHelper.open();
			} catch (RTDException e) {
				view.createErrorDialog(e.error);
				return;
			}
			
			getTasksDueToday();
			getTasksDueTomorrow();
			getTasksOverdue();
		}
	}
	
	public void stop() {
		dbHelper.close();
	}
	
	private void buildDatabase() {
		dbHelper = new Database(view.getContext());
		try {
			dbHelper.open();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	private void getTasksDueToday() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar cal = new GregorianCalendar();
		try {
			cal.setTime(df.parse(df.format(Calendar.getInstance().getTime())));
		} catch (ParseException e) {
			RTDError error = new RTDError(Program.Error.PARSE_EXCEPTION, R.string.error_parse_default, true, false);
			view.createErrorDialog(error);
		}
		
		cal.add(Calendar.MILLISECOND, -1 * TimeZone.getDefault().getOffset(cal.getTimeInMillis()));
		
		String dstart = Program.DATE_FORMAT.format(cal.getTime());
		cal.add(Calendar.DATE, 1);
		String dend = Program.DATE_FORMAT.format(cal.getTime());
		try {
			view.setTasksDueToday(dbHelper.getDb().query(TaskSeries.TABLE + ", " + Task.TABLE,
									   new String[] {
														TaskSeries.TABLE + "." + TaskSeries.ID, 
														TaskSeries.NAME, Task.DUE_DATE,
														Task.PRIORITY,
														Task.HAS_DUE_TIME
													},
									   Task.DUE_DATE + ">=? AND " + Task.DUE_DATE + "<? AND " +
									   Task.COMPLETED + " is NULL AND " + 
									   TaskSeries.TABLE + "." + TaskSeries.ID + "=" + Task.TABLE + "." + Task.TASK_SERIES_ID,
									   new String[] {dstart, dend}, null, null, Task.DUE_DATE + " ASC"));
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	private void getTasksDueTomorrow() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar cal = new GregorianCalendar();
		try {
			cal.setTime(df.parse(df.format(Calendar.getInstance().getTime())));
		} catch (ParseException e) {
			RTDError error = new RTDError(Program.Error.PARSE_EXCEPTION, R.string.error_parse_default, true, false);
			view.createErrorDialog(error);
		}
		
		cal.add(Calendar.MILLISECOND, -1 * TimeZone.getDefault().getOffset(cal.getTimeInMillis()));
		
		cal.add(Calendar.DATE, 1);
		String dstart = Program.DATE_FORMAT.format(cal.getTime());
		cal.add(Calendar.DATE, 1);
		String dend = Program.DATE_FORMAT.format(cal.getTime());
		
		try {
			view.setTasksDueTomorrow(dbHelper.getDb().query(TaskSeries.TABLE + ", " + Task.TABLE,
										  new String[] {
															TaskSeries.TABLE + "." + TaskSeries.ID, 
															TaskSeries.NAME, Task.DUE_DATE,
															Task.PRIORITY,
															Task.HAS_DUE_TIME
													   },
									   Task.DUE_DATE + ">=? AND " + Task.DUE_DATE + "<? AND " +
									   Task.COMPLETED + " is NULL AND " + 
									   TaskSeries.TABLE + "." + TaskSeries.ID + "=" + Task.TABLE + "." + Task.TASK_SERIES_ID,
									   new String[] {dstart, dend}, null, null, Task.DUE_DATE + " ASC"));
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	private void getTasksOverdue() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar cal = new GregorianCalendar();
		try {
			cal.setTime(df.parse(df.format(Calendar.getInstance().getTime())));
		} catch (ParseException e) {
			RTDError error = new RTDError(Program.Error.PARSE_EXCEPTION, R.string.error_parse_default, true, false);
			view.createErrorDialog(error);
		}
		
		cal.add(Calendar.MILLISECOND, -1 * TimeZone.getDefault().getOffset(cal.getTimeInMillis()));
		
		String dend = Program.DATE_FORMAT.format(cal.getTime());
		
		try {
			view.setTasksOverdue(dbHelper.getDb().query(TaskSeries.TABLE + ", " + Task.TABLE,
									  new String[] {
												   		TaskSeries.TABLE + "." + TaskSeries.ID, 
												   		TaskSeries.NAME, Task.DUE_DATE,
												   		Task.PRIORITY,
												   		Task.HAS_DUE_TIME
												   },
									   Task.DUE_DATE + "<? AND " + Task.COMPLETED + " is NULL AND " + 
									   TaskSeries.TABLE + "." + TaskSeries.ID + "=" + Task.TABLE + "." + Task.TASK_SERIES_ID,
									   new String[] {dend}, null, null, Task.DUE_DATE + " ASC"));
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
}
