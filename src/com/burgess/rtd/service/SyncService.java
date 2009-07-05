/**
 * SyncService.java
 * com.burgess.rtd.service
 *
 * Created Jun 12, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.List;
import com.burgess.rtd.model.Location;
import com.burgess.rtd.model.Note;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.Tag;
import com.burgess.rtd.model.Task;
import com.burgess.rtd.model.TaskSeries;
import com.burgess.rtd.model.TaskTag;
import com.burgess.rtd.model.rtm.GetLists;
import com.burgess.rtd.model.rtm.GetLocations;
import com.burgess.rtd.model.rtm.GetTasks;

/**
 * Provides the background synchronization service for the system.
 */
public class SyncService extends BroadcastReceiver {
	private RTMModel rtm;
	private String token;
	private Database dbHelper;
	private SQLiteDatabase db;
	private Cursor cursor;
	private String lastSync;
	private Context context;
	
	private boolean full = false;
	
	private class SyncThread extends Thread {
		public void run() {
			synchronize();
			
			updateAlarmManager(context);
		}
	};
	
	private class UpdateThread extends Thread {
		public void run() {
			try {
				updateRTM();
			} catch (RTDException e) {

			}
		}
	};
	
	public SyncService() {
		
	}
	
	public SyncService(Context context) {
		this.context = context;
		rtm = new RTMModel(context);
		token = context.getSharedPreferences(Program.APPLICATION, 0).getString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
		lastSync = context.getSharedPreferences(Program.APPLICATION, 0).getString(Program.Config.LAST_SYNC, "");
		dbHelper = new Database(context);
		
		try {
			dbHelper.open();
			db = dbHelper.getDb();
		} catch (RTDException e) {
			Log.e(Program.LOG, "Database open error: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(Program.LOG, "Broadcast received");
		
		this.context = context;
		rtm = new RTMModel(context);
		token = context.getSharedPreferences(Program.APPLICATION, 0).getString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
		lastSync = context.getSharedPreferences(Program.APPLICATION, 0).getString(Program.Config.LAST_SYNC, "");
		dbHelper = new Database(context);
		
		try {
			dbHelper.open();
			db = dbHelper.getDb();
		} catch (RTDException e) {
			Log.e(Program.LOG, "Database open error: " + e.getMessage());
			return;
		}
		
		SyncThread syncThread = new SyncThread();
		syncThread.start();
	}
	
	public RTDError synchronize() {
		Log.i(Program.LOG, "Last Sync: " + lastSync);
		
		if (token.equals(Program.DEFAULT_AUTH_TOKEN)) {
			return new RTDError(Program.Error.NOT_AUTHENTICATED, R.string.error_not_authenticated, false, false);
		}
		
		try {
			updateRTM();
			
			synchronizeLists();
			synchronizeTasks();
			synchronizeLocations();
		} catch (RTDException e) {
			Log.e(Program.LOG, "Problem synchronizing: " + e.getMessage());
			return e.error;
		}
		
		long time = Calendar.getInstance().getTime().getTime();
		time = time - TimeZone.getDefault().getOffset(time);
		SharedPreferences.Editor editor = context.getSharedPreferences(Program.APPLICATION, 0).edit();
		editor.putString(Program.Config.LAST_SYNC, Program.DATE_FORMAT.format(time));
		editor.commit();
		
		dbHelper.close();
		
		return null;
	}
	
	public void sendRequests() {
		UpdateThread updateThread = new UpdateThread();
		updateThread.start();
	}
	
	private void updateRTM() throws RTDException {
		Cursor c = db.query(Request.TABLE, new String[] {Request.ID, Request.QUERY,
														 Request.TYPE, Request.LOCAL_ID},
							Request.SYNCED + "=0", null, null, null, null);
		
		if (!c.moveToFirst()) {
			c.close();
			return;
		}
		
		ContentValues cv;
		while (c.isAfterLast() == false) {
			switch (c.getInt(2)) {
				case Program.Data.LIST:
					GetLists lists = new GetLists();
					Request r = new Request();
					r.url = c.getString(1);
					lists.parse(rtm.execute(RTM.PATH, r));
					for (Integer key : lists.lists.keySet()) {
						cv = new ContentValues();
						cv.put(List.ID, key);
						db.update(List.TABLE, cv, List.ID + "=" + c.getInt(3), null);
					}
					
					break;
				default:
					break;
			}
			
			cv = new ContentValues();
			cv.put(Request.SYNCED, true);
			db.update(Request.TABLE, cv, Request.ID + "=" + c.getInt(0), null);
			
			c.moveToNext();
		}
		c.close();
	}
	
	private void synchronizeLists() throws RTDException {
		markAllListsUnsynced();
		
		GetLists lists = new GetLists();
		Request r = new Request(RTM.Lists.GET_LIST);
		r.setParameter("auth_token", token);
		
		lists.parse(rtm.execute(RTM.PATH, r));
		
		processLists(lists);
		
		deleteAllUnsyncedLists();
	}
	
	private void processLists(GetLists lists) {
		Log.i(Program.LOG, "Found " + lists.lists.size() + " lists");
		
		ContentValues cv;
		for (Integer key : lists.lists.keySet()) {
			cv = new ContentValues();
			cv.put(List.ID, key);
			cv.put(List.ARCHIVED, (Boolean)lists.lists.get(key).get("archived"));
			cv.put(List.DELETED, (Boolean)lists.lists.get(key).get("deleted"));
			cv.put(List.NAME, (String)lists.lists.get(key).get("name"));
			cv.put(List.POSITION, (Integer)lists.lists.get(key).get("position"));
			cv.put(List.SMART, (Boolean)lists.lists.get(key).get("smart"));
			cv.put(List.SYNCED, true);
			
			cursor = db.query(List.TABLE, new String[] {List.ID}, List.ID + "=?", 
							  new String[] {key.toString()}, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				db.insert(List.TABLE, null, cv);
			} else {
				db.update(List.TABLE, cv, List.ID + "=?", new String[] {key.toString()});
			}
			cursor.close();
		}
	}
	
	private void synchronizeTasks() throws RTDException {
		GetTasks tasks = new GetTasks();
		Request r = new Request(RTM.Tasks.GET_LIST);
		r.setParameter("auth_token", token);
		if (lastSync.length() == 0) {
			full = true;
		} else {
			full = false;
			r.setParameter("last_sync", lastSync);
		}
		
		tasks.parse(rtm.execute(RTM.PATH, r));
		
		processTasks(tasks);
		
		deleteTasks(tasks.deletedTasks);
	}
	
	@SuppressWarnings("unchecked")
	private void processTasks(GetTasks tasks) {
		Log.i(Program.LOG, "Found " + tasks.tasks.size() + " tasks");
		
		if (full)
			markAllTasksUnsynced();
		
		ContentValues cv;
		ArrayList<Hashtable<String, Object>> x;
		ArrayList<String> y;
		for (Integer key : tasks.tasks.keySet()) {
			cv = new ContentValues();
			cv.put(TaskSeries.ID, key);
			cv.put(TaskSeries.LIST_ID, (Integer)tasks.tasks.get(key).get("list_id"));
			cv.put(TaskSeries.CREATED, Program.DATE_FORMAT.format((Date)tasks.tasks.get(key).get("created")));
			cv.put(TaskSeries.MODIFIED, Program.DATE_FORMAT.format((Date)tasks.tasks.get(key).get("modified")));
			cv.put(TaskSeries.NAME, (String)tasks.tasks.get(key).get("name"));
			cv.put(TaskSeries.SOURCE, (String)tasks.tasks.get(key).get("source"));
			cv.put(TaskSeries.URL, (String)tasks.tasks.get(key).get("url"));
			int loc = (Integer)tasks.tasks.get(key).get("location_id");
			if (loc > 0)
				cv.put(TaskSeries.LOCATION_ID, loc);
			else
				cv.putNull(TaskSeries.LOCATION_ID);
			cv.put(TaskSeries.SYNCED, true);
			
			cursor = db.query(TaskSeries.TABLE, new String[] {TaskSeries.ID}, TaskSeries.ID + "=?", 
							  new String[] {key.toString()}, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				db.insert(TaskSeries.TABLE, null, cv);
			} else {
				db.update(TaskSeries.TABLE, cv, TaskSeries.ID + "=?", new String[] {key.toString()});
			}
			
			cursor.close();
			
			x = (ArrayList<Hashtable<String, Object>>)tasks.tasks.get(key).get("notes");
			
			for (int i = 0; i < x.size(); i++) {
				cv = new ContentValues();
				Integer id = (Integer)x.get(i).get("id");
				cv.put(Note.ID, id);
				cv.put(Note.BODY, (String)x.get(i).get("body"));
				cv.put(Note.CREATED, Program.DATE_FORMAT.format((Date)x.get(i).get("created")));
				cv.put(Note.MODIFIED, Program.DATE_FORMAT.format((Date)x.get(i).get("modified")));
				cv.put(Note.TITLE, (String)x.get(i).get("title"));
				cv.put(Note.SYNCED, true);
				cv.put(Note.TASK_SERIES_ID, key);
				
				cursor = db.query(Note.TABLE, new String[] {Note.ID}, Note.ID + "=?", 
						  new String[] {id.toString()}, null, null, null);
				cursor.moveToFirst();
				if (cursor.getCount() == 0) {
					db.insert(Note.TABLE, null, cv);
				} else {
					db.update(Note.TABLE, cv, Note.ID + "=?", new String[] {id.toString()});
				}
				
				cursor.close();
			}
			
			y = (ArrayList<String>)tasks.tasks.get(key).get("tags");
			
			for (int i = 0; i < y.size(); i++) {
				cursor = db.query(Tag.TABLE, new String[] {Tag.ID}, Tag.NAME + "=?", 
								  new String[] {y.get(i)}, null, null, null);
				cursor.moveToFirst();
				if (cursor.getCount() == 0) {
					cv = new ContentValues();
					cv.put(Tag.NAME, y.get(i));
					long tagId = db.insert(Tag.TABLE, null, cv);
					cv = new ContentValues();
					cv.put(TaskTag.TASK_SERIES_ID, key);
					cv.put(TaskTag.TAG_ID, tagId);
					db.insert(TaskTag.TABLE, null, cv);
					cursor.close();
				} else {
					int tagId = cursor.getInt(0);
					cursor.close();
					cursor = db.query(TaskTag.TABLE, new String[] {TaskTag.ID},
									  TaskTag.TAG_ID + "=? AND " + TaskTag.TASK_SERIES_ID + "=?",
									  new String[] {tagId + "", key + ""}, null, null, null);
					cursor.moveToFirst();
					if (cursor.getCount() == 0) {
						cv = new ContentValues();
						cv.put(TaskTag.TAG_ID, tagId);
						cv.put(TaskTag.TASK_SERIES_ID, key);
						db.insert(TaskTag.TABLE, null, cv);
					}
					cursor.close();
				}
				
			}
			
			x = (ArrayList<Hashtable<String, Object>>)tasks.tasks.get(key).get("tasks");
			
			for (int i = 0; i < x.size(); i++) {
				cv = new ContentValues();
				Integer id = (Integer)x.get(i).get("id");
				cv.put(Task.ID, id);
				if (x.get(i).get("due").getClass().equals(String.class))
					cv.putNull(Task.DUE_DATE);
				else
					cv.put(Task.DUE_DATE, Program.DATE_FORMAT.format((Date)x.get(i).get("due")));
				
				if (x.get(i).get("completed").getClass().equals(String.class))
					cv.putNull(Task.COMPLETED);
				else
					cv.put(Task.COMPLETED, Program.DATE_FORMAT.format((Date)x.get(i).get("completed")));
				
				if (x.get(i).get("deleted").getClass().equals(String.class))
					cv.putNull(Task.DELETED);
				else
					cv.put(Task.DELETED, Program.DATE_FORMAT.format((Date)x.get(i).get("deleted")));
				
				cv.put(Task.ADDED, Program.DATE_FORMAT.format((Date)x.get(i).get("added")));
				cv.put(Task.PRIORITY, (String)x.get(i).get("priority"));
				if ((Integer)x.get(i).get("postponed") == -1)
					cv.putNull(Task.POSTPONED);
				else
					cv.put(Task.POSTPONED, (Integer)x.get(i).get("postponed"));
				cv.put(Task.ESTIMATE, (String)x.get(i).get("estimate"));
				cv.put(Task.HAS_DUE_TIME, (Boolean)x.get(i).get("has_due_time"));
				cv.put(Task.TASK_SERIES_ID, key);
				
				cursor = db.query(Task.TABLE, new String[] {Task.ID}, Task.ID + "=?", 
						  new String[] {id.toString()}, null, null, null);
				cursor.moveToFirst();
				if (cursor.getCount() == 0) {
					db.insert(Task.TABLE, null, cv);
				} else {
					db.update(Task.TABLE, cv, Task.ID + "=?", new String[] {id.toString()});
				}
				cursor.close();
			}
		}
		
		if (full)
			deleteAllUnsyncedTasks();
	}
	
	private void synchronizeLocations() throws RTDException {
		markAllLocationsUnsynced();
		
		GetLocations locations = new GetLocations();
		Request r = new Request(RTM.Locations.GET_LIST);
		r.setParameter("auth_token", token);
		locations.parse(rtm.execute(RTM.PATH, r));
		
		processLocations(locations);
		
		deleteAllUnsyncedLocations();
	}
	
	private void processLocations(GetLocations locations) {
		Log.i(Program.LOG, "Found " + locations.locations.size() + " locations");
		
		ContentValues cv;
		for (Integer key : locations.locations.keySet()) {
			cv = new ContentValues();
			cv.put(Location.ID, key);
			cv.put(Location.ADDRESS, (String)locations.locations.get(key).get("address"));
			cv.put(Location.LATITUDE, (Double)locations.locations.get(key).get("latitude"));
			cv.put(Location.LONGITUDE, (Double)locations.locations.get(key).get("longitude"));
			cv.put(Location.NAME, (String)locations.locations.get(key).get("name"));
			cv.put(Location.SYNCED, true);
			cv.put(Location.VIEWABLE, (Boolean)locations.locations.get(key).get("viewable"));
			cv.put(Location.ZOOM, (Integer)locations.locations.get(key).get("zoom"));
			
			cursor = db.query(Location.TABLE, new String[] {Location.ID}, Location.ID + "=?", 
							  new String[] {key.toString()}, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() == 0) {
				db.insert(Location.TABLE, null, cv);
			} else {
				db.update(Location.TABLE, cv, Location.ID + "=?", new String[] {key.toString()});
			}
			
			cursor.close();
		}
	}
	
	private void markAllListsUnsynced() {
		ContentValues cv = new ContentValues();
		cv.put(List.SYNCED, false);
		db.update(List.TABLE, cv, "1=1", null);
	}
	
	private void deleteTasks(ArrayList<Integer> taskIds) {
		String whereTS = "";
		String whereT = "";
		for (int i = 0; i < taskIds.size(); i++) {
			whereTS = whereTS + TaskSeries.ID + "=" + taskIds.get(i) + " OR ";
			whereT = whereT + Task.TASK_SERIES_ID + "=" + taskIds.get(i) + " OR ";
		}
		whereTS = whereTS + "0=1";
		whereT = whereT + "0=1";
		
		int rows = db.delete(Task.TABLE, whereT, null);
		Log.d(Program.LOG, "Deleted " + rows + " tasks");
		rows = db.delete(TaskSeries.TABLE, whereTS, null);
		Log.d(Program.LOG, "Deleted " + rows + " task series");
		rows = db.delete(Note.TABLE, whereT, null);
		Log.d(Program.LOG, "Deleted " + rows + " notes");
		rows = db.delete(TaskTag.TABLE, whereT, null);
		Log.d(Program.LOG, "Deleted " + rows + " task/tags");
	}
	
	private void deleteAllUnsyncedLists() {
		Cursor cursor = db.query(List.TABLE, new String[] {List.ID}, List.SYNCED + "=0", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(0);
			Log.d(Program.LOG, "Deleting list " + id);
			db.delete(List.TABLE, List.ID + "=" + id, null);
			cursor.moveToNext();
		}
		cursor.close();
	}

	private void markAllTasksUnsynced() {
		ContentValues cv = new ContentValues();
		cv.put(TaskSeries.SYNCED, false);
		db.update(TaskSeries.TABLE, cv, "1=1", null);
	}
	
	private void deleteAllUnsyncedTasks() {
		int taskId;
		Cursor cursor = db.query(TaskSeries.TABLE, new String[] {TaskSeries.ID}, Task.SYNCED + "=0", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			taskId = cursor.getInt(0);
			
			Log.d(Program.LOG, "Deleting task " + taskId);
			
			db.delete(Task.TABLE, Task.TASK_SERIES_ID + "=" + taskId, null);
			db.delete(TaskSeries.TABLE, TaskSeries.ID + "=" + taskId, null);
			db.delete(Note.TABLE, Note.TASK_SERIES_ID + "=" + taskId, null);
			db.delete(TaskTag.TABLE, TaskTag.TASK_SERIES_ID + "=" + taskId, null);
			
			cursor.moveToNext();
		}
		
		cursor.close();
	}
	
	private void markAllLocationsUnsynced() {
		ContentValues cv = new ContentValues();
		cv.put(Location.SYNCED, false);
		db.update(Location.TABLE, cv, "1=1", null);
	}
	
	private void deleteAllUnsyncedLocations() {
		Cursor cursor = db.query(Location.TABLE, new String[] {Location.ID}, List.SYNCED + "=0", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			int id = cursor.getInt(0);
			Log.d(Program.LOG, "Deleting location " + id);
			db.delete(Location.TABLE, Location.ID + "=" + id, null);
			cursor.moveToNext();
		}
		cursor.close();
	}
	
	public static void updateAlarmManager(Context context) {
		int syncTime = context.getSharedPreferences(Program.APPLICATION, 0).getInt(Program.Config.SYNC_TIME, Program.Config.SyncValues.MANUALLY);
		if (syncTime != Program.Config.SyncValues.MANUALLY) {
			AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			long interval;
			switch (syncTime) {
				case Program.Config.SyncValues.TWICE_DAILY:
					interval = 12 * 3600 * 1000;
					break;
				case Program.Config.SyncValues.HALF_HOUR:
					interval = 1800 * 1000;
					break;
				case Program.Config.SyncValues.HOUR:
					interval = 3600 * 1000;
					break;
				case Program.Config.SyncValues.TWO_HOURS:
					interval = 2 * 3600 * 1000;
					break;
				case Program.Config.SyncValues.DAILY:
					interval = 24 * 3600 * 1000;
					break;
				default:
					return;
			}
			
			Log.i(Program.LOG, "Synchronizing every " + interval / 1000 + " seconds");
			
			Intent i = new Intent(context, SyncService.class);
			PendingIntent intent = PendingIntent.getBroadcast(context, Program.Request.SYNC, i, PendingIntent.FLAG_UPDATE_CURRENT);
			alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, intent);
		}
	}
}
