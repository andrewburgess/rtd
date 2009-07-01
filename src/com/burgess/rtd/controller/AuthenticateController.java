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

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.exceptions.RTDException;
import com.burgess.rtd.interfaces.view.IAuthenticateView;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.rtm.GetFrob;
import com.burgess.rtd.model.rtm.GetToken;
import com.burgess.rtd.model.rtm.GetTimeline;

/**
 * Handles Authenticating the application with RTM
 *
 * @author Andrew Burgess
 */
public class AuthenticateController {
	/**
	 * Handle obtaining a frob from RTM
	 */
	private static final int ERROR = -1;
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
	private GetTimeline timeline;
	
	private RTDError error;
	
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
				case ERROR:
					view.createErrorDialog(error);
					break;
				case FROB:
					view.removeDialog(Program.Dialog.GET_FROB);
					Request r = new Request("");
					r.setParameter("perms", "delete");
					r.setParameter("frob", frob.frob);
					view.loadUrl(RTM.AUTH_PATH + r.toString());
					break;
				case TOKEN:
					saveAuthDetails();
					view.removeDialog(Program.Dialog.GET_AUTH);
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
			Message m = new Message();
			
			try {
				Request r = new Request(RTM.Auth.GET_FROB);
				frob = new GetFrob();
				frob.parse(rtm.execute(RTM.PATH, r));
			} catch (RTDException e) {
				error = e.error;
				m.what = ERROR;
				handler.sendMessage(m);
				return;
			}

			m.what = FROB;
			handler.sendMessage(m);
		}
	};
	
	private Thread getAuthTokenThread = new Thread() {
		public void run() {
			Message m = new Message();
			
			try {
				Request request = new Request(RTM.Auth.GET_TOKEN);
				request.setParameter("frob", frob.frob);
				token = new GetToken();
				token.parse(rtm.execute(RTM.PATH, request));
				
				request = new Request(RTM.Timelines.CREATE);
				request.setParameter("auth_token", token.token);
				
				timeline = new GetTimeline();
				timeline.parse(rtm.execute(RTM.PATH, request));
			} catch (RTDException e) {
				error = e.error;
				m.what = ERROR;
				handler.sendMessage(m);
				return;
			}
			
			
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
		view.showDialog(Program.Dialog.GET_FROB);
		
		getFrobThread.start();
	}
	
	public void getAuthToken() {
		view.showDialog(Program.Dialog.GET_AUTH);
		getAuthTokenThread.start();
	}
	
	private void saveAuthDetails() {
		if (token == null)
			return;
		
		SharedPreferences.Editor editor = view.getPreferences().edit();
		editor.putString(Program.Config.AUTH_TOKEN, token.token);
		editor.putLong(Program.Config.TIMELINE, timeline.time);
		editor.putString(Program.Config.FULLNAME, token.fullname);
		editor.putString(Program.Config.USERNAME, token.username);
		editor.putLong(Program.Config.ID, token.id);
		editor.commit();
	}
}
