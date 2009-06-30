/**
 * Note.java
 * com.burgess.rtd.model
 *
 * Created Jun 4, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model;

/**
 * Represents a Note stored in the database
 */
public class Note {
	public static final String ID = "_id";
	public static final String TASK_SERIES_ID = "task_series_id";
	public static final String TITLE = "title";
	public static final String BODY = "body";
	public static final String CREATED = "created";
	public static final String MODIFIED = "modified";
	public static final String SYNCED = "synced";
	
	public static final String TABLE = "notes";
	
	public static final String CREATE = "create table notes (" +
										   "_id integer primary key autoincrement, " +
										   "task_series_id integer, " +
										   "title text default '', " +
										   "body text default '', " +
										   "created datetime default NULL, " +
										   "modified datetime default NULL, " +
										   "synced boolean default 0);";
	public static final String DESTROY = "drop table if exists notes";
}
