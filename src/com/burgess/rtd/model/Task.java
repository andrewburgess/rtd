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
	public static String getDBCreateString() {
		return "create table tasks (" +
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
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists tasks";
	}
}
