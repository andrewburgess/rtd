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
	public static final String CREATE = "create table lists (" +
										   "id integer primary key autoincrement, " +
										   "name text, " +
										   "deleted boolean default 0, " +
										   "archived boolean default 0, " +
										   "position integer default 0, " +
										   "smart boolean default 0, " +
										   "synced boolean default 0);";
	public static final String DESTROY = "drop table if exists lists";
}
