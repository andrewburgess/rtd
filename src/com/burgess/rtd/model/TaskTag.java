/**
 * TaskTag.java
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
 * Represents a TaskTag object in the database
 */
public class TaskTag {
	public static String getDBCreateString() {
		return "create table task_tags (" +
				"id integer primary key autoincrement, " +
				"task_id integer, " +
				"tag_id integer);";
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists task_tags";
	}
}
