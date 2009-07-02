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
import android.database.sqlite.SQLiteDatabase;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.interfaces.view.IListsView;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.List;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.service.SyncService;

/**
 *
 */
public class ListsController {
	private IListsView view;
	private Database dbHelper;
	private SQLiteDatabase db;
	private String token;
	private long timeline;
	
	public ListsController(IListsView view) {
		this.view = view;
		
		dbHelper = new Database(view.getContext());
		try {
			dbHelper.open();
			db = dbHelper.getDb();
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
		return db.query(List.TABLE, 
				new String[] {
					List.ID, 
					List.NAME
				}, 
				List.ARCHIVED + "=" + (viewArchived ? "1" : "0") + " AND " +
				List.DELETED + "=0", null, null, null, 
				List.POSITION + ", " + List.NAME);
	}

	public void renameList(long listId, String name) {
		ContentValues cv = new ContentValues();
		cv.put(List.NAME, name);
		db.update(List.TABLE, cv, List.ID + "=" + listId, null);
		
		Request r = new Request(RTM.Lists.SET_NAME);
		r.setParameter("auth_token", token);
		r.setParameter("timeline", timeline);
		r.setParameter("list_id", listId);
		r.setParameter("name", name);
		
		r.save(db, listId, Program.Data.LIST);
		
		SyncService s = new SyncService(view.getContext());
		
		s.sendRequests();
	}
	
	public void setListArchived(long listId, boolean archive) {
		ContentValues cv = new ContentValues();
		cv.put(List.ARCHIVED, archive);
		db.update(List.TABLE, cv, List.ID + "=" + listId, null);
		
		Request r = new Request(archive ? RTM.Lists.ARCHIVE : RTM.Lists.UNARCHIVE);
		r.setParameter("auth_token", token);
		r.setParameter("timeline", timeline);
		r.setParameter("list_id", listId);
		
		r.save(db, listId, Program.Data.LIST);
		
		SyncService s = new SyncService(view.getContext());
		
		s.sendRequests();
	}
}
