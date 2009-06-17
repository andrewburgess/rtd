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
	
	public static final String TABLE = "lists";
	
	public static final String CREATE = "create table lists (" +
										   "_id integer primary key autoincrement, " +
										   "name text, " +
										   "deleted boolean default 0, " +
										   "archived boolean default 0, " +
										   "position integer default 0, " +
										   "smart boolean default 0, " +
										   "synced boolean default 0);";
	public static final String DESTROY = "drop table if exists lists";
}
