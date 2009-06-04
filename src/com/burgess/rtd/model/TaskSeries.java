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

import java.util.Date;

/**
 * Represents a TaskSeries object in the database
 */
public class TaskSeries {
	public int id;
	public Date created;
	public Date modified;
	public String name;
	public int listId;
	public String source;
	public String url;
	public int locationId;
	public boolean synced;
	
	public static String getDBCreateString() {
		return "create table task_series (" +
				"id integer primary key autoincrement, " +
				"created datetime default NULL, " +
				"modified datetime default NULL, " +
				"name text default '', " +
				"list_id int, " +
				"source text default '', " +
				"url text default '', " +
				"location_id int default NULL," +
				"synced boolean default 0);";				
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists task_series";
	}
}
