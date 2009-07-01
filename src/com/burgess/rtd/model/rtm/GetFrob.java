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

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

/**
 * @author Andrew
 *
 */
public class GetFrob extends RTMObject {
	public String frob;

	/* (non-Javadoc)
	 * @see com.burgess.rtd.model.rtm.RtmObject#parse(java.lang.String)
	 */
	@Override
	public void parse(String data) throws RTDException {
		try {
			JSONObject json;
			json = new JSONObject(data).getJSONObject("rsp");
			status = json.getString("stat");
			if (status.equals("ok")) {
				frob = json.getString("frob");
			} else {
				throw new RTDException(Program.Error.RTM_ERROR, R.string.error_rtm, true, false);
			}
		} catch (JSONException e) {
			throw new RTDException(Program.Error.JSON_EXCEPTION, R.string.error_auth_getFrob, true, false);
		}
	}

}
