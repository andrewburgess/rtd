/**
 * GetLocations.java
 * com.burgess.rtd.model.rtm
 *
 * Created Jun 17, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model.rtm;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

/**
 *
 */
public class GetLocations extends RTMObject {
	public Hashtable<Integer, Hashtable<String, Object>> locations;
	
	public GetLocations() {
		locations = new Hashtable<Integer, Hashtable<String, Object>>();
	}
	
	/* (non-Javadoc)
	 * @see com.burgess.rtd.model.rtm.RTMObject#parse(java.lang.String)
	 */
	@Override
	public void parse(String data) throws RTDException {
		try {
			JSONObject json = new JSONObject(data).getJSONObject("rsp");
			status = json.getString("stat");
			if (status.equals("ok")) {
				JSONArray locs = json.getJSONObject("locations").getJSONArray("location");
				JSONObject loc;
				Hashtable<String, Object> l;
				for (int i = 0; i < locs.length(); i++) {
					loc = locs.getJSONObject(i);
					l = new Hashtable<String, Object>();
					
					l.put("name", loc.getString("name"));
					l.put("longitude", loc.getDouble("longitude"));
					l.put("latitude", loc.getDouble("latitude"));
					l.put("zoom", loc.getInt("zoom"));
					l.put("address", loc.getString("address"));
					l.put("viewable", loc.getInt("viewable") > 0);
					
					locations.put(loc.getInt("id"), l);
				}
			} else {
				throw new RTDException(Program.Error.RTM_ERROR, R.string.error_rtm, true, false);
			}
		} catch (JSONException e) {
			throw new RTDException(Program.Error.JSON_EXCEPTION, R.string.error_parse_default, true, false, e);
		}
	}

}
