/**
 * Tag.java
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
 * Represents a Tag stored in the database
 */
public class Tag {
	public static String getDBCreateString() {
		return "create table tags (" +
				"id integer primary key autoincrement, " +
				"name text default '');";
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists tags";
	}
}
