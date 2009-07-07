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
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String ZOOM = "zoom";
	public static final String ADDRESS = "address";
	public static final String VIEWABLE = "viewable";
	public static final String SYNCED = "synced";
	public static final String REMOTE_ID = "remote_id";
	
	public static final String TABLE = "locations";
	
	public static final String CREATE = "create table locations (" +
										   "_id integer primary key autoincrement, " +
										   "remote_id integer default 0, " +
										   "name text default '', " +
										   "longitude real default 0.0, " +
										   "latitude real default 0.0, " +
										   "zoom int default 1, " +
										   "address text default '', " +
										   "viewable boolean default 0, " +
										   "synced boolean default 0);";
	public static final String DESTROY = "drop table if exists locations";
}
