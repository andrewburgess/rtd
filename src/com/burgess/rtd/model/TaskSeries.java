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
