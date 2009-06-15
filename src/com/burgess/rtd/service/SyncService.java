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

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.List;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.rtm.GetLists;

/**
 *
 */
public class SyncService extends Service {
	private IBinder binder = new SyncBinder();	
	private RTMModel rtm;
	private String token;
	private Database dbHelper;
	
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
}
