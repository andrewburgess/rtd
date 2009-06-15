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
	public static final String CREATE = "create table tags (" +
										  "id integer primary key autoincrement, " +
										  "name text default '');";
	public static final String DESTROY = "drop table if exists tags";
}
