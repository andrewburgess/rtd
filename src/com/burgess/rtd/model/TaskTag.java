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
	public static final String ID = "_id";
	public static final String TASK_SERIES_ID = "task_series_id";
	public static final String REMOTE_TASK_SERIES_ID = "remote_task_series_id";
	public static final String TAG_ID = "tag_id";
	
	public static final String TABLE = "task_tags";
	
	public static final String CREATE = "create table task_tags (" +
										  "_id integer primary key autoincrement, " +
										  "task_series_id integer, " +
										  "remote_task_series_id integer default 0, " +
										  "tag_id integer);";
	public static final String DESTROY = "drop table if exists task_tags";
}
