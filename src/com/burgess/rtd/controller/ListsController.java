/**
 * ListsController.java
 * com.burgess.rtd.controller
 *
 * Created Jun 22, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.controller;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.interfaces.view.IListsView;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.List;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.TaskSeries;
import com.burgess.rtd.service.SyncService;

/**
 *
 */
public class ListsController {
	private IListsView view;
	private Database dbHelper;
	private String token;
	private long timeline;
	
	public ListsController(IListsView view) {
		this.view = view;
		
		dbHelper = new Database(view.getContext());
		try {
			dbHelper.open();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
		
		SharedPreferences prefs = view.getContext().getSharedPreferences(Program.APPLICATION, 0);
		token = prefs.getString(Program.Config.AUTH_TOKEN, Program.DEFAULT_AUTH_TOKEN);
		timeline = prefs.getLong(Program.Config.TIMELINE, 0);
	}
	
	public void stop() {
		dbHelper.close();
	}
	
	public void initializeView() {
		view.setupListView(getLists(view.isShowingArchived()));
	}
	
	private Cursor getLists(boolean viewArchived) {
		try {
			return dbHelper.getDb().query(List.TABLE, 
					new String[] {
						List.ID, 
						List.NAME
					}, 
					List.ARCHIVED + "=" + (viewArchived ? "1" : "0") + " AND " +
					List.DELETED + "=0", null, null, null, 
					List.POSITION + ", " + List.NAME);
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
			return null;
		}
	}
	
	public void addList(String name) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(List.NAME, name);
			long id = dbHelper.getDb().insert(List.TABLE, null, cv);
			
			Request r = new Request(RTM.Lists.ADD);
			r.setParameter("auth_token", token);
			r.setParameter("timeline", timeline);
			r.setParameter("name", name);
			
			r.save(dbHelper.getDb(), id, Program.Data.LIST);
			
			SyncService s = new SyncService(view.getContext());
			s.sendRequests();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}

	public void renameList(long listId, String name) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(List.NAME, name);
			dbHelper.getDb().update(List.TABLE, cv, List.ID + "=" + listId, null);
			
			Request r = new Request(RTM.Lists.SET_NAME);
			r.setParameter("auth_token", token);
			r.setParameter("timeline", timeline);
			r.setParameter("list_id", List.getRemoteId(dbHelper.getDb(), listId));
			r.setParameter("name", name);
			
			r.save(dbHelper.getDb(), listId, Program.Data.LIST);
			
			SyncService s = new SyncService(view.getContext());
			
			s.sendRequests();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	public void deleteList(long listId) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(List.DELETED, true);
			dbHelper.getDb().update(List.TABLE, cv, List.ID + "=" + listId, null);
			
			moveTasks(listId);
			
			Request r = new Request(RTM.Lists.DELETE);
			r.setParameter("auth_token", token);
			r.setParameter("timeline", timeline);
			r.setParameter("list_id", List.getRemoteId(dbHelper.getDb(), listId));
			
			r.save(dbHelper.getDb(), listId, Program.Data.LIST);
			
			SyncService s = new SyncService(view.getContext());
			
			s.sendRequests();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	public void setListArchived(long listId, boolean archive) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(List.ARCHIVED, archive);
			dbHelper.getDb().update(List.TABLE, cv, List.ID + "=" + listId, null);
			
			Request r = new Request(archive ? RTM.Lists.ARCHIVE : RTM.Lists.UNARCHIVE);
			r.setParameter("auth_token", token);
			r.setParameter("timeline", timeline);
			r.setParameter("list_id", List.getRemoteId(dbHelper.getDb(), listId));
			
			r.save(dbHelper.getDb(), listId, Program.Data.LIST);
			
			SyncService s = new SyncService(view.getContext());
			
			s.sendRequests();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	private void moveTasks(long listId) throws RTDException {
		Cursor c = dbHelper.getDb().query(List.TABLE, new String[] {List.ID}, List.NAME + "=?", new String[] {"Inbox"}, null, null, null);
		
		c.moveToFirst();
		
		int id = c.getInt(0);
		c.close();
		
		ContentValues cv = new ContentValues();
		cv.put(TaskSeries.LIST_ID, id);
		dbHelper.getDb().update(TaskSeries.TABLE, cv, TaskSeries.LIST_ID + "=" + listId, null);
	}
}
