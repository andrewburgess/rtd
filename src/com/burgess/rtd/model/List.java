/**
 * List.java
 * com.burgess.rtd.model
 *
 * Created Jun 4, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Represents a List object in the database
 */
public class List {
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String DELETED = "deleted";
	public static final String ARCHIVED = "archived";
	public static final String POSITION = "position";
	public static final String SMART = "smart";
	public static final String SYNCED = "synced";
	public static final String REMOTE_ID = "remote_id";
	
	public static final String TABLE = "lists";
	
	public static final String CREATE = "create table lists (" +
										   "_id integer primary key autoincrement, " +
										   "remote_id integer default NULL, " + 
										   "name text, " +
										   "deleted boolean default 0, " +
										   "archived boolean default 0, " +
										   "position integer default 0, " +
										   "smart boolean default 0, " +
										   "synced boolean default 0);";
	public static final String DESTROY = "drop table if exists lists";
	
	public static long getRemoteId(SQLiteDatabase db, long listId) {
		long remoteId = -1;
		
		Cursor cursor = db.query(List.TABLE, new String[] {List.REMOTE_ID}, 
				List.ID + "=" + listId, null, null, null, null);
		
		cursor.moveToFirst();
		if (cursor.getCount() > 0)
			remoteId = cursor.getLong(0);
		
		cursor.close();
		
		return remoteId;
	}
}
