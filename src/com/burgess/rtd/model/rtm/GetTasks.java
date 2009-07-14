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

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

/**
 *
 */
public class GetTasks extends RtmObject {
	public Hashtable<Integer, Hashtable<String, Object>> tasks;
	public ArrayList<Integer> deletedTasks;
	
	public GetTasks() {
		tasks = new Hashtable<Integer, Hashtable<String, Object>>();
		deletedTasks = new ArrayList<Integer>();
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
					if (list.has("deleted")) {
						processDeletedTasks(list.getJSONObject("deleted").get("taskseries"));
					}
					
					if (list.has("taskseries")) {
						taskseries = list.get("taskseries");
						if (taskseries.getClass().equals(JSONArray.class)) {
							for (int j = 0; j < ((JSONArray)taskseries).length(); j++) {
								ts = ((JSONArray)taskseries).getJSONObject(j);
								id = ts.getInt("id");
								tasks.put(id, new Hashtable<String, Object>());
								tasks.get(id).put("list_id", list.getInt("id"));
								tasks.get(id).put("created", Program.DATE_FORMAT.parse(ts.getString("created")));
								tasks.get(id).put("modified", Program.DATE_FORMAT.parse(ts.getString("modified")));
								tasks.get(id).put("name", ts.getString("name"));
								tasks.get(id).put("source", ts.getString("source"));
								tasks.get(id).put("url", ts.getString("url"));
								Integer loc = ts.getString("location_id").length() > 0 ? ts.getInt("location_id") : -1;
								tasks.get(id).put("location_id", loc);
								tasks.get(id).put("notes", getNotes(ts.get("notes")));
								tasks.get(id).put("tags", getTags(ts.get("tags")));
								tasks.get(id).put("tasks", getTasks(ts.get("task")));
							}
						} else {
							ts = (JSONObject)taskseries;
							id = ts.getInt("id");
							tasks.put(id, new Hashtable<String, Object>());
							tasks.get(id).put("list_id", list.getInt("id"));
							tasks.get(id).put("created", Program.DATE_FORMAT.parse(ts.getString("created")));
							tasks.get(id).put("modified", Program.DATE_FORMAT.parse(ts.getString("modified")));
							tasks.get(id).put("name", ts.getString("name"));
							tasks.get(id).put("source", ts.getString("source"));
							tasks.get(id).put("url", ts.getString("url"));
							Integer loc = ts.getString("location_id").length() > 0 ? ts.getInt("location_id") : -1;
							tasks.get(id).put("location_id", loc);
							tasks.get(id).put("notes", getNotes(ts.get("notes")));
							tasks.get(id).put("tags", getTags(ts.get("tags")));
							tasks.get(id).put("tasks", getTasks(ts.get("task")));
						}
					}
				}
			} else {
				throw new RTDException(Program.Error.RTM_ERROR, R.string.error_rtm, true, false);
			}
		} catch (JSONException e) {
			throw new RTDException(Program.Error.JSON_EXCEPTION, R.string.error_task_sync, true, false, e);			
		} catch (ParseException e) {
			throw new RTDException(Program.Error.PARSE_EXCEPTION, R.string.error_date_parse, true, false, e);
		}
	}
	
	private ArrayList<Hashtable<String, Object>> getNotes(Object json) throws JSONException, ParseException {
		ArrayList<Hashtable<String, Object>> notelist = new ArrayList<Hashtable<String, Object>>();
		Hashtable<String, Object> ht;
		
		if (json.getClass().equals(JSONObject.class)) {
			JSONObject n;
			Object x = ((JSONObject)json).get("note");
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
	
	private ArrayList<String> getTags(Object json) throws JSONException {
		ArrayList<String> tags = new ArrayList<String>();
		if (!json.getClass().equals((JSONObject.class)))
			return tags;
		
		Object t = ((JSONObject)json).get("tag");
		if (t.getClass().equals(JSONArray.class)) {
			for (int i = 0; i < ((JSONArray)t).length(); i++) {
				tags.add(((JSONArray)t).getString(i));
			}
		} else {
			tags.add((String)t);
		}
		return tags;
	}
	
	private ArrayList<Hashtable<String, Object>> getTasks(Object json) throws JSONException, ParseException {
		ArrayList<Hashtable<String, Object>> tasks = new ArrayList<Hashtable<String, Object>>();
		Hashtable<String, Object> ht;
		JSONObject n;
		if (json.getClass().equals(JSONArray.class)) {
			for (int i = 0; i < ((JSONArray)json).length(); i++) {
				n = ((JSONArray)json).getJSONObject(i);
				ht = new Hashtable<String, Object>();
				
				ht.put("id", n.getInt("id"));
				ht.put("added", Program.DATE_FORMAT.parse(n.getString("added")));
				if (n.getString("completed").length() > 0)
					ht.put("completed", Program.DATE_FORMAT.parse(n.getString("completed")));
				else
					ht.put("completed", "");
				
				if (n.getString("deleted").length() > 0)
					ht.put("deleted", Program.DATE_FORMAT.parse(n.getString("deleted")));
				else
					ht.put("deleted", "");
				
				if (n.getString("due").length() > 0)
					ht.put("due", Program.DATE_FORMAT.parse(n.getString("due")));
				else
					ht.put("due", "");
				
				ht.put("has_due_time", n.getInt("has_due_time") > 0);
				ht.put("priority", n.getString("priority"));
				ht.put("postponed", n.getInt("postponed"));
				ht.put("estimate", n.getString("estimate"));
				
				tasks.add(ht);
			}
		} else {
			n = ((JSONObject)json);
			ht = new Hashtable<String, Object>();
			
			ht.put("id", n.getInt("id"));
			ht.put("added", Program.DATE_FORMAT.parse(n.getString("added")));
			if (n.getString("completed").length() > 0)
				ht.put("completed", Program.DATE_FORMAT.parse(n.getString("completed")));
			else
				ht.put("completed", "");
			
			if (n.getString("due").length() > 0)
				ht.put("due", Program.DATE_FORMAT.parse(n.getString("due")));
			else
				ht.put("due", "");
			
			if (n.getString("deleted").length() > 0)
				ht.put("deleted", Program.DATE_FORMAT.parse(n.getString("deleted")));
			else
				ht.put("deleted", "");
			
			ht.put("has_due_time", n.getInt("has_due_time") > 0);
			ht.put("priority", n.getString("priority"));
			if (n.getString("postponed").length() > 0)
				ht.put("postponed", n.getInt("postponed"));
			else
				ht.put("postponed", -1);
			ht.put("estimate", n.getString("estimate"));
			
			tasks.add(ht);
		}
		
		return tasks;
	}
	
	private void processDeletedTasks(Object json) throws JSONException, ParseException {
		JSONObject j;
		if (json.getClass().equals(JSONArray.class)) {
			for (int i = 0; i < ((JSONArray)json).length(); i++) {
				j = ((JSONArray)json).getJSONObject(i);
				deletedTasks.add(j.getInt("id"));
			}
		} else {
			j = (JSONObject) json;
			deletedTasks.add(j.getInt("id"));
		}
	}

}
