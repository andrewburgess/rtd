/**
 * RTM.java
 * com.burgess.rtd.model
 *
 * Created Jun 8, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

public class RTMModel {
	private static long lastAccess = 0;
	private static final int wait = 1000;
	private static final int BUFFER_LENGTH = 1024;
	
	private Context context;
	private Request request;
	
	public RTMModel(Context context) {
		this.context = context;
	}
	
	public String execute(String url, Request request) 	throws RTDException {
		this.request = request;
		NetworkInfo mobile = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (mobile.getState() == NetworkInfo.State.CONNECTED || wifi.getState() == NetworkInfo.State.CONNECTED) {
			pause();
			
			try {
				Log.i(Program.LOG, "Request: " + url + request.toString());
				URL u = new URL(url + request.toString());
				String data = readData(openConnection(u));
				lastAccess = Calendar.getInstance().getTimeInMillis();
				if (data != null) {
					Log.d(Program.LOG, "Response: " + data);
					return data;
				}
				
				return null;
			} catch (MalformedURLException e) {
				Log.e(Program.LOG, "Problem forming URL: " + request + "\n" + e.getMessage());
				throw new RTDException(Program.Error.MALFORMED_URL, R.string.error_default, true, e);
			}
		} else {
			Log.e(Program.LOG, "Network is not available");
			throw new RTDException(Program.Error.NETWORK_UNAVAILABLE_EXCEPTION, R.string.error_network_unavailable, false);
		}
	}
	
	private void pause() {
		while (Calendar.getInstance().getTimeInMillis() < lastAccess + wait);
	}
	
	private InputStream openConnection(URL url) throws RTDException {
		int response = -1;
		
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(true);
			connection.setAllowUserInteraction(false);
			connection.setRequestMethod("GET");
			
			connection.connect();
			
			response = connection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				return (InputStream) connection.getInputStream();
			} else {
				Log.e(Program.LOG, "HTTP Error: " + response);
				throw new RTDException(Program.Error.HTTP_EXCEPTION, R.string.error_default, true);
			}
		} catch (MalformedURLException e) {
			Log.e(Program.LOG, "Problem forming URL: " + request + "\n" + e.getMessage());
			throw new RTDException(Program.Error.MALFORMED_URL, R.string.error_default, true, e);
		} catch (IOException e) {
			Log.e(Program.LOG, "Problem opening the connection: " + e.getMessage());
			throw new RTDException(Program.Error.IO_EXCEPTION, R.string.error_default, true, e);
		}
	}
	
	private String readData(InputStream in) throws RTDException {
		InputStreamReader reader = new InputStreamReader(in);
		int charRead;
		String data = "";
		char[] buffer = new char[BUFFER_LENGTH];
		try {
			while ((charRead = reader.read(buffer)) > 0) {
				data += String.copyValueOf(buffer, 0, charRead);
				buffer = new char[BUFFER_LENGTH];
			}
			
			in.close();
		} catch (IOException e) {
			Log.e(Program.LOG, e.getMessage());
			throw new RTDException(Program.Error.IO_EXCEPTION, R.string.error_default, true, e);
		}
		
		return data;
	}
	
}
