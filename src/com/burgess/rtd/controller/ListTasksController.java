/**
 * ListTasksController.java
 * com.burgess.rtd.controller
 *
 * Created Jun 24, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.interfaces.view.IListTasksView;
import com.burgess.rtd.model.Database;

/**
 *
 */
public class ListTasksController {
	private IListTasksView view;
	private Database dbHelper;
	private SQLiteDatabase db;
	
	public ListTasksController(IListTasksView view) {
		this.view = view;
		
		dbHelper = new Database(view.getContext());
		try {
			dbHelper.open();
			db = dbHelper.getDb();
		} catch (RTDException e) {
			
		}
	}
	
	public void initializeView() {
		long id = view.getListId();
		
		Cursor cursor = db.rawQuery("SELECT task_series._id AS _id, task_series.name, tasks.due_date, tasks.has_due_time, tasks.priority " +
				"FROM task_series " +
				"INNER JOIN tasks ON task_series._id = tasks.task_series_id " +
				"WHERE list_id = ? AND completed IS NULL " +
				"ORDER BY priority, due_date, name ", new String[] {"" + id});
		
		view.setTaskListCursor(cursor);
	}
}
