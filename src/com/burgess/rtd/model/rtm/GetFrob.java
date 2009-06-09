/**
 * GetFrob.java
 * com.burgess.rtd.model.rtm
 *
 * Created Jun 8, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model.rtm;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrew
 *
 */
public class GetFrob extends RtmObject {
	public String frob;

	/* (non-Javadoc)
	 * @see com.burgess.rtd.model.rtm.RtmObject#parse(java.lang.String)
	 */
	@Override
	public void parse(String data) {
		try {
			JSONObject json = new JSONObject(data).getJSONObject("rsp");
			status = json.getString("stat");
			if (status.equals("ok")) {
				frob = json.getString("frob");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
