/**
 * AuthToken.java
 * com.burgess.rtd.model.rtm
 *
 * Created Jun 10, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model.rtm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class GetToken extends RtmObject {
	public String username;
	public String fullname;
	public String token;
	public long id;

	@Override
	public void parse(String data) throws JSONException, Exception {
		JSONObject json = new JSONObject(data).getJSONObject("rsp");
		status = json.getString("stat");
		if (status.equals("ok")) {
			JSONObject auth = json.getJSONObject("auth");
			JSONObject user = auth.getJSONObject("user");
			
			token = auth.getString("token");
			username = user.getString("username");
			fullname = user.getString("fullname");
			id = user.getLong("id");
		} else {
			throw new Exception("RTM returned status: " + status);
		}
	}
	
}
