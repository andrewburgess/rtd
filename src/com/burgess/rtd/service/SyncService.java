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
import java.util.Date;
import java.util.Hashtable;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.List;
import com.burgess.rtd.model.Note;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.Tag;
import com.burgess.rtd.model.Task;
import com.burgess.rtd.model.TaskSeries;
import com.burgess.rtd.model.TaskTag;
import com.burgess.rtd.model.rtm.GetLists;
import com.burgess.rtd.model.rtm.GetTasks;

/**
 *
 */
public class SyncService extends Service {
	private IBinder binder = new SyncBinder();	
	private RTMModel rtm;
	private String token;
	private Database dbHelper;
	private Cursor cursor;
	
	public class SyncBinder extends Binder {
		public SyncService getService() {
			return SyncService.this;
		}
	}
	
	public SyncService() {
		rtm = new RTMModel(this);
		token = getSharedPreferences(Program.APPLICATION, 0).getString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
		dbHelper = new Database(this);
		
		try {
			dbHelper.open();
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
		}
	}
	
	public SyncService(Context context) {
		rtm = new RTMModel(context);
		token = context.getSharedPreferences(Program.APPLICATION, 0).getString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
		dbHelper = new Database(context);
		
		try {
			dbHelper.open();
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
		}
	}
	
	@Override
	public void onCreate() {
		Log.i(Program.LOG, "Service started");
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	
	public void synchorinze() {
		try {
			synchronizeLists();
			synchronizeTasks();
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
		}
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
			dbHelper.getDb().insert(List.TABLE, null, cv);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void synchronizeTasks() throws RTDException {
		GetTasks tasks = new GetTasks();
		Request r = new Request(RTM.Tasks.GET_LIST);
		r.setParameter("auth_token", token);
		try {
			tasks.parse(rtm.execute(RTM.PATH, r));
		} catch (RTDException e) {
			//TODO: Figure out how to show the user the error
			return;
		}
		
		ContentValues cv;
		SQLiteDatabase db = dbHelper.getDb();
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
			db.insert(TaskSeries.TABLE, null, cv);
			
			x = (ArrayList<Hashtable<String, Object>>)tasks.tasks.get(key).get("notes");
			
			for (int i = 0; i < x.size(); i++) {
				cv = new ContentValues();
				cv.put(Note.ID, (Integer)x.get(i).get("id"));
				cv.put(Note.BODY, (String)x.get(i).get("body"));
				cv.put(Note.CREATED, Program.DATE_FORMAT.format((Date)x.get(i).get("created")));
				cv.put(Note.MODIFIED, Program.DATE_FORMAT.format((Date)x.get(i).get("modified")));
				cv.put(Note.TITLE, (String)x.get(i).get("title"));
				cv.put(Note.SYNCED, true);
				cv.put(Note.TASK_ID, key);
				
				db.insert(Note.TABLE, null, cv);
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
				cv.put(Task.ID, (Integer)x.get(i).get("id"));
				if ((Boolean)x.get(i).get("has_due_time"))
					cv.put(Task.DUE_DATE, Program.DATE_FORMAT.format((Date)x.get(i).get("due")));
				else
					cv.putNull(Task.DUE_DATE);
				
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
				cv.put(Task.TASK_SERIES_ID, key);
				
				db.insert(Task.TABLE, null, cv);
			}
		}
	}
}
