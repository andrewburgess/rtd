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

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
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
			//TODO: Figure out how to show the user the error
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
			//TODO: Figure out how to show the user the error
		}
		
		synchronize();
	}
	
	public void synchronize() {
		Log.i(Program.LOG, "Last Sync: " + lastSync);
		
		try {
			synchronizeLists();
			synchronizeTasks();
			synchronizeLocations();
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
		}
		
		long time = Calendar.getInstance().getTime().getTime();
		time = time - TimeZone.getDefault().getOffset(time);
		SharedPreferences.Editor editor = context.getSharedPreferences(Program.APPLICATION, 0).edit();
		editor.putString(Program.Config.LAST_SYNC, Program.DATE_FORMAT.format(time));
		editor.commit();
		
		dbHelper.close();
	}
	
	private void synchronizeLists() throws RTDException {
		GetLists lists = new GetLists();
		Request r = new Request(RTM.Lists.GET_LIST);
		r.setParameter("auth_token", token);
		try {
			lists.parse(rtm.execute(RTM.PATH, r));
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
			return;
		}
		
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
	
	@SuppressWarnings("unchecked")
	private void synchronizeTasks() throws RTDException {
		GetTasks tasks = new GetTasks();
		Request r = new Request(RTM.Tasks.GET_LIST);
		r.setParameter("auth_token", token);
		if (lastSync.length() > 0) {
			r.setParameter("last_sync", lastSync);
		}

		try {
			tasks.parse(rtm.execute(RTM.PATH, r));
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
			return;
		}
		
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
				cv.put(Note.TASK_ID, key);
				
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
					cv.put(TaskTag.TASK_ID, key);
					cv.put(TaskTag.TAG_ID, tagId);
					db.insert(TaskTag.TABLE, null, cv);
					cursor.close();
				} else {
					int tagId = cursor.getInt(0);
					cursor.close();
					cursor = db.query(TaskTag.TABLE, new String[] {TaskTag.ID},
									  TaskTag.TAG_ID + "=? AND " + TaskTag.TASK_ID + "=?",
									  new String[] {tagId + "", key + ""}, null, null, null);
					cursor.moveToFirst();
					if (cursor.getCount() == 0) {
						cv = new ContentValues();
						cv.put(TaskTag.TAG_ID, tagId);
						cv.put(TaskTag.TASK_ID, key);
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
	}
	
	private void synchronizeLocations() {
		GetLocations locations = new GetLocations();
		Request r = new Request(RTM.Locations.GET_LIST);
		r.setParameter("auth_token", token);
		try {
			locations.parse(rtm.execute(RTM.PATH, r));
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
			return;
		}
		
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
}
