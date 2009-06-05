/**
 * Request.java
 * com.burgess.rtd.model
 *
 * Created Jun 5, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model;

/**
 * Represents a Request string that needs to be submitted to RTM.
 */
public class Request {
	public static String getDBCreateString() {
		return "create table requests (" +
				"id integer primary key autoincrement, " +
				"request text default '', " +
				"created datetime default NULL, " +
				"synced boolean default 0);";
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists requests";
	}
}
