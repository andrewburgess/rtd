/**
 * GetLists.java
 * com.burgess.rtd.model.rtm
 *
 * Created Jun 15, 2009
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
public class GetLists extends RTMObject {
	public Hashtable<Integer, Hashtable<String, Object>> lists;
	
	public GetLists() {
		lists = new Hashtable<Integer, Hashtable<String, Object>>();
	}

	/* (non-Javadoc)
	 * @see com.burgess.rtd.model.rtm.RtmObject#parse(java.lang.String)
	 */
	@Override
	public void parse(String data) throws RTDException {
		try {
			JSONObject json = new JSONObject(data).getJSONObject("rsp");
			status = json.getString("stat");
			if (status.equals("ok")) {
				if (json.has("lists")) {
					JSONArray l = json.getJSONObject("lists").getJSONArray("list");
					JSONObject list;
					int id;
					for (int i = 0; i < l.length(); i++) {
						list = l.getJSONObject(i);
						id = list.getInt("id");
						lists.put(id, new Hashtable<String,  Object>());
						lists.get(id).put("name", list.getString("name"));
						lists.get(id).put("position", list.getInt("position"));
						lists.get(id).put("archived", list.getInt("archived") > 0);
						lists.get(id).put("deleted", list.getInt("deleted") > 0);
						lists.get(id).put("smart", list.getInt("smart") > 0);
					}
				} else if (json.has("list")) {
					JSONObject list = json.getJSONObject("list");
					int id = list.getInt("id");
					lists.put(id, new Hashtable<String,  Object>());
					lists.get(id).put("name", list.getString("name"));
					lists.get(id).put("position", list.getInt("position"));
					lists.get(id).put("archived", list.getInt("archived") > 0);
					lists.get(id).put("deleted", list.getInt("deleted") > 0);
					lists.get(id).put("smart", list.getInt("smart") > 0);
				}
			} else {
				throw new RTDException(Program.Error.RTM_ERROR, R.string.error_rtm, true, false);
			}
		} catch (JSONException e) {
			throw new RTDException(Program.Error.JSON_EXCEPTION, R.string.error_list_sync, true, false, e);
		}
	}
}