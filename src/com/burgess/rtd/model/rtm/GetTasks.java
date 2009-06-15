/**
 * GetTasks.java
 * com.burgess.rtd.model.rtm
 *
 * Created Jun 15, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model.rtm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

/**
 *
 */
public class GetTasks extends RTMObject {
	private Hashtable<Integer, Hashtable<String, Object>> tasks;
	
	public GetTasks() {
		tasks = new Hashtable<Integer, Hashtable<String, Object>>();
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
				JSONArray lists = json.getJSONObject("tasks").getJSONArray("list");
				JSONObject list;
				Object taskseries;
				JSONObject ts;
				int id;
				for (int i = 0; i < lists.length(); i++) {
					list = lists.getJSONObject(i);
					if (list.has("taskseries")) {
						taskseries = list.get("taskseries");
						if (taskseries.getClass().equals(JSONArray.class)) {
							for (int j = 0; j < ((JSONArray)taskseries).length(); j++) {
								ts = ((JSONArray)taskseries).getJSONObject(j);
								id = ts.getInt("id");
								tasks.put(id, new Hashtable<String, Object>());
								tasks.get(id).put("created", Program.DATE_FORMAT.parse(ts.getString("created")));
								tasks.get(id).put("modified", Program.DATE_FORMAT.parse(ts.getString("modified")));
								tasks.get(id).put("name", ts.getString("name"));
								tasks.get(id).put("source", ts.getString("source"));
								tasks.get(id).put("url", ts.getString("url"));
								Integer loc = ts.getString("location_id").length() > 0 ? ts.getInt("location_id") : -1;
								tasks.get(id).put("location_id", loc);
								tasks.get(id).put("notes", getNotes(ts.get("notes")));
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			Log.e(Program.LOG, e.getMessage());
			
		} catch (ParseException e) {
			
		}
	}
	
	private ArrayList<Hashtable<String, Object>> getNotes(Object notes) throws JSONException, ParseException {
		ArrayList<Hashtable<String, Object>> notelist = new ArrayList<Hashtable<String, Object>>();
		Hashtable<String, Object> ht;
		
		if (notes.getClass().equals(JSONObject.class)) {
			JSONObject n;
			Object x = ((JSONObject)notes).get("note");
			if (x.getClass().equals(JSONArray.class)) {
				for (int i = 0; i < ((JSONArray)x).length(); i++) {
					n = ((JSONArray)x).getJSONObject(i);
					ht = new Hashtable<String, Object>();
					ht.put("id", n.getInt("id"));
					ht.put("body", n.getString("$t"));
					ht.put("created", Program.DATE_FORMAT.parse(n.getString("created")));
					ht.put("modified", Program.DATE_FORMAT.parse(n.getString("modified")));
					ht.put("title", n.getString("title"));
					notelist.add(ht);
				}
			} else {
				n = ((JSONObject)x);
				ht = new Hashtable<String, Object>();
				ht.put("id", n.getInt("id"));
				ht.put("body", n.getString("$t"));
				ht.put("created", Program.DATE_FORMAT.parse(n.getString("created")));
				ht.put("modified", Program.DATE_FORMAT.parse(n.getString("modified")));
				ht.put("title", n.getString("title"));
				notelist.add(ht);
			}
		}
		
		return notelist;
	}

}
