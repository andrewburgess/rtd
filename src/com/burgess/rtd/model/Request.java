/**
 * Request.java
 * com.burgess.rtd.model
 *
 * Created Jun 5, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import android.util.Log;

import com.burgess.rtd.constants.API;
import com.burgess.rtd.constants.Program;

/**
 * Represents a Request string that needs to be submitted to RTM.
 */
public class Request {
	/**
	 * Stores the Request parameters in a key/value structure
	 */
	private Hashtable<String, Object> parameters;
	
	/**
	 * Constructor which uses the default JSON format
	 *
	 * @param name	Request method name
	 */
	public Request(String name) {
		this(name, "json");
	}
	
	/**
	 * Constructor which allows the user to pick a format name
	 *
	 * @param name		Request method name
	 * @param format	Format: json or xml
	 */
	public Request(String name, String format) {
		Log.d(Program.LOG, "Request created (" + name + ", " + format + ")");
		
		parameters = new Hashtable<String, Object>();
		if (!name.equals("")) {
			setParameter("method", name);
		}
		
		setParameter("api_key", API.API_KEY);
		setParameter("format", format);
	}
	
	/**
	 * Sets a parameter in the key/value structure
	 *
	 * @param key
	 * @param value
	 */
	public void setParameter(String key, Object value) {
		parameters.put(key, value);
	}
	
	/**
	 * Builds a request URI
	 */
	@Override
	public String toString() {
		String ret = "";
		
		for (String key : parameters.keySet()) {
			ret += key + "=" + parameters.get(key).toString() + "&";
		}
		
		ret += "api_sig=" + signMethod();
		
		return ret;
	}
	
	/**
	 * Signs the method using MD5 for the api_sig parameter
	 *
	 * @return	An MD5 encoded string for the request
	 */
	private String signMethod() {
		String str = API.SHARED_SECRET;
		Vector<String> v = new Vector<String>(parameters.keySet());
		Collections.sort(v);
		
		for (String key : v) {
			str += key + parameters.get(key);
		}
		
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(str.getBytes(), 0, str.length());
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static String getDBCreateString() {
		return "create table requests (" +
				"id integer primary key autoincrement, " +
				"request text default '', " +
				"created datetime default NULL, " +
				"synced boolean default 0);";
	}
	
	public static String getDBDestroyString() {
		return "drop table if exists requests";
	}
}
