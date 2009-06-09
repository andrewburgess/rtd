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
	public static final String AUTH_TOKEN = "authtoken";
	public static final String LAST_SYNC = "lastsync";
	
	public static final String DEFAULT_AUTH_TOKEN = "0000000000000000";
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
}
