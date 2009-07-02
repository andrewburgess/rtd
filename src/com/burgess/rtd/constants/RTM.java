/**
* RTM.java
* com.burgess.rtd.constants
*
* Created Jun 3, 2009
*
* Author: Andrew Burgess
* Email: abombm1@gmail.com
* Copyright: 2009
*/
package com.burgess.rtd.constants;
 
public class RTM {
	public static final String PATH = "http://api.rememberthemilk.com/services/rest/?";
	public static final String AUTH_PATH = "http://www.rememberthemilk.com/services/auth/?";
	
	public static class Auth {
		public static final String GET_FROB = "rtm.auth.getFrob";
		public static final String GET_TOKEN = "rtm.auth.getToken";
		public static final String CHECK_TOKEN = "rtm.auth.checkToken";
	}
	
	public static class Lists {
		public static final String GET_LIST = "rtm.lists.getList";
		public static final String ADD = "rtm.lists.add";
		public static final String SET_NAME = "rtm.lists.setName";
		public static final String ARCHIVE = "rtm.lists.archive";
		public static final String UNARCHIVE = "rtm.lists.unarchive";
	}
	
	public static class Locations {
		public static final String GET_LIST = "rtm.locations.getList";
	}
	
	public static class Tasks {
		public static final String GET_LIST = "rtm.tasks.getList";
	}
	
	public static class Test {
		public static final String TEST_ECHO = "rtm.test.echo";
	}
	
	
	public static class Timelines {
		public static final String CREATE = "rtm.timelines.create";
	}
	
	
}
