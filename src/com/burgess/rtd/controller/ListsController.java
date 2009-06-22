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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.interfaces.view.IListsView;
import com.burgess.rtd.model.Database;
import com.burgess.rtd.model.List;

/**
 *
 */
public class ListsController {
	private IListsView view;
	private SQLiteDatabase db;
	
	public ListsController(IListsView view) {
		this.view = view;
		
		Database dbHelper = new Database(view.getContext());
		try {
			dbHelper.open();
			db = dbHelper.getDb();
		} catch (RTDException e) {
			view.createErrorDialog(e.error);
		}
	}
	
	public void initializeView() {
		Cursor cursor = db.query(List.TABLE, new String[] {List.ID, List.NAME}, null, null, null, null, List.POSITION + ", " + List.NAME);
		
		view.setListsCursor(cursor);
	}
}
