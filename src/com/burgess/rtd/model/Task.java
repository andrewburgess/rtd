/**
 * Task.java
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
 * Represents a Task stored in the database
 */
public class Task {
	public static final String ID = "id";
	public static final String TASK_SERIES_ID = "task_series_id";
	public static final String DUE_DATE = "due_date";
	public static final String CREATED = "created";
	public static final String COMPLETED = "completed";
	public static final String DELETED = "deleted";
	public static final String PRIORITY = "priority";
	public static final String POSTPONED = "postponed";
	public static final String ESTIMATE = "estimate";
	public static final String SYNCED = "synced";
	
	public static final String CREATE = "create table tasks (" +
										  "id integer primary key autoincrement, " +
										  "task_series_id integer, " +
										  "due_date datetime default NULL, " +
										  "created datetime default NULL, " +
										  "completed datetime default NULL, " +
										  "deleted datetime default NULL, " +
										  "priority char default 'N', " +
										  "postponed integer default 0, " +
										  "estimate text default '', " +
										  "synced boolean default 0);";
	
	public static final String DESTROY = "drop table if exists tasks";
}
