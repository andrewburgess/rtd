package com.burgess.rtd.model.rtm;

import org.json.JSONException;
import org.json.JSONObject;

public class Timeline extends RtmObject {
	public long time;

	@Override
	public void parse(String data) throws JSONException, Exception {
		JSONObject json = new JSONObject(data).getJSONObject("rsp");
		status = json.getString("status");
		if (status.equals("ok")) {
			time = json.getLong("timeline");
		} else {
			throw new Exception("RTM return status: " + status);
		}
	}

}
