/**
 * Location.java
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
 * Represents a Location object in the database
 */
public class Location {
	public static String getDBCreateString() {
		return "create table locations (" +
				"id integer primary key autoincrement, " +
				"name text default '', " +
				"longitude real default 0.0, " +
				"latitude real default 0.0, " +
				"zoom int default 1, " +
				"address text default '', " +
				"viewable boolean default 0, " +
				"synced boolean default 0);";
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists locations";
	}
}
