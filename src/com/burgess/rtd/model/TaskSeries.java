/**
 * TaskSeries.java
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
 * Represents a TaskSeries object in the database
 */
public class TaskSeries {
	public static final String ID = "id";
	public static final String CREATED = "created";
	public static final String MODIFIED = "modified";
	public static final String NAME = "name";
	public static final String LIST_ID = "list_id";
	public static final String SOURCE = "source";
	public static final String URL = "url";
	public static final String LOCATION_ID = "location_id";
	public static final String SYNCED = "synced";
	
	public static final String TABLE = "task_series";
	
	public static final String CREATE = "create table task_series (" + 
										  "id integer primary key autoincrement, " +
										  "created datetime default NULL, " +
										  "modified datetime default NULL, " +
										  "name text default '', " +
										  "list_id int, " +
										  "source text default '', " +
										  "url text default '', " +
										  "location_id int default NULL," +
										  "synced boolean default 0);";				
	
	public static final String DESTROY = "drop table if exists task_series";
}
