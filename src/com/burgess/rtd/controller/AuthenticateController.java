/**
 * AuthenticateController.java
 * com.burgess.rtd.controller
 *
 * Created Jun 8, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.controller;

import org.json.JSONException;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.interfaces.view.IAuthenticateView;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.rtm.GetFrob;
import com.burgess.rtd.model.rtm.GetToken;
import com.burgess.rtd.model.rtm.Timeline;

/**
 * Handles Authenticating the application with RTM
 *
 * @author Andrew Burgess
 */
public class AuthenticateController {
	/**
	 * Handle obtaining a frob from RTM
	 */
	private static final int FROB = 1;
	private static final int TOKEN = 2;
	
	/**
	 * View to control
	 */
	private IAuthenticateView view;
	/**
	 * Provides access to RTM data services
	 */
	private RTMModel rtm;
	/**
	 * Frob object parsed from RTM data services
	 */
	private GetFrob frob;
	
	private GetToken token;
	
	/**
	 * Handles things while a thread is working.
	 */
	private Handler handler = new Handler() {
		/**
		 * Handle a message from a thread
		 * 
		 * @param msg	The message passed along from the thread
		 */
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case FROB:
					view.dismissDialog();
					Request r = new Request("");
					r.setParameter("perms", "delete");
					r.setParameter("frob", frob.frob);
					view.loadUrl(RTM.AUTH_PATH + r.toString());
					break;
				case TOKEN:
					saveAuthDetails();
					view.dismissDialog();
					view.finish();
					break;
			}
		}
	};
	
	/**
	 * Thread which gets a frob from the RTM service to determine the URL
	 * to authenticate with
	 */
	private Thread getFrobThread = new Thread() {
		/**
		 * Runs the thread
		 */
		public void run() {
			Request r = new Request(RTM.GET_FROB);
			frob = new GetFrob();
			try {
				frob.parse(rtm.execute(RTM.PATH, r));
			} catch (JSONException e) {
				
			} catch (Exception e) {
				
			}
			
			Message m = new Message();
			m.what = FROB;
			handler.sendMessage(m);
		}
	};
	
	private Thread getAuthTokenThread = new Thread() {
		public void run() {
			Request request = new Request(RTM.GET_TOKEN);
			request.setParameter("frob", frob.frob);
			token = new GetToken();
			try {
				token.parse(rtm.execute(RTM.PATH, request));
			} catch (JSONException e) {
				
			} catch (Exception e) {
				
			}
			
			request = new Request(RTM.TIMELINE_CREATE);
			request.setParameter("auth_token", token.token);
			Timeline timeline = new Timeline();
			try {
				timeline.parse(rtm.execute(RTM.PATH, request));
			} catch (JSONException e) {
				
			} catch (Exception e) {
				
			}
			
			Message m = new Message();
			m.what = TOKEN;
			handler.sendMessage(m);
		}
	};
	
	/**
	 * Creates a new controller
	 *
	 * @param view	A view that will be controlled by this class
	 */
	public AuthenticateController(IAuthenticateView view) {
		this.view = view;
		
		rtm = new RTMModel(view.getContext());
	}
	
	/**
	 * Initializes the view by starting a dialog box and then getting the URL
	 * for the WebView
	 */
	public void initializeView() {
		view.createDialog("Obtaining Auth URL");
		
		getFrobThread.start();
	}
	
	public void getAuthToken() {
		view.createDialog("Getting Auth Token");
		getAuthTokenThread.start();
	}
	
	private void saveAuthDetails() {
		if (token == null)
			return;
		
		SharedPreferences.Editor editor = view.getPreferences().edit();
		editor.putString(Program.AUTH_TOKEN, token.token);
		editor.putString(Program.FULLNAME, token.fullname);
		editor.putString(Program.USERNAME, token.username);
		editor.putLong(Program.ID, token.id);
		editor.commit();
	}
}
