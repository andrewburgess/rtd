package com.burgess.rtd.model.rtm;

import org.json.JSONException;
import org.json.JSONObject;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

public class GetTimeline extends RtmObject {
	public long time;

	@Override
	public void parse(String data) throws RTDException {
		try {
			JSONObject json = new JSONObject(data).getJSONObject("rsp");
			status = json.getString("stat");
			if (status.equals("ok")) {
				time = json.getLong("timeline");
			} else {
				throw new RTDException(Program.Error.RTM_ERROR, R.string.error_rtm, true, false);
			}
		} catch (JSONException e) {
			throw new RTDException(Program.Error.JSON_EXCEPTION, R.string.error_timeline_create, true, false);
		}
	}

}
