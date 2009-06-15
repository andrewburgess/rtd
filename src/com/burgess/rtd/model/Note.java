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
	public static final String CREATE = "create table notes (" +
										   "id integer primary key autoincrement, " +
										   "task_id integer, " +
										   "title text default '', " +
										   "body text default '', " +
										   "created datetime default NULL, " +
										   "modified datetime default NULL, " +
										   "synced boolean default 0);";
	public static final String DESTROY = "drop table if exists notes";
}
