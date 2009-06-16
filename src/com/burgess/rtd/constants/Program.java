/**
 * Program.java
 * com.burgess.rtd.constants
 *
 * Created Jun 3, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.constants;

import java.text.SimpleDateFormat;

public class Program {
	public static final String APPLICATION = "rtd";
	public static final String LOG = "rtd";
	
	
	//Config values
	public static class Config {
		public static final String AUTH_TOKEN = "authtoken";
		public static final String LAST_SYNC = "lastsync";
		public static final String USERNAME = "username";
		public static final String FULLNAME = "fullname";
		public static final String ID = "id";
		public static final String SYNC_TIME = "synctime";
	}
	
	//Default values
	public static final String DEFAULT_AUTH_TOKEN = "0000000000000000";
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	public static class Dialog {
		public static final int GET_FROB = 1;
		public static final int GET_AUTH = 2;
		public static final int ERROR = 3;
		public static final int SYNCHRONIZE = 4;
	}
	
	public static class Error {
		public static final int EXCEPTION = 1;
		public static final int MALFORMED_URL = 2;
		public static final int IO_EXCEPTION = 3;
		public static final int HTTP_EXCEPTION = 4;
		public static final int NETWORK_UNAVAILABLE_EXCEPTION = 5;
		public static final int SQL_EXCEPTION = 6;
		public static final int JSON_EXCEPTION = 7;
		public static final int PARSE_EXCEPTION = 8;
	}
}
