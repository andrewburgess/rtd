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

import android.os.Handler;
import android.os.Message;

import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.interfaces.view.IAuthenticateView;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.rtm.GetFrob;

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
				
			}
			
			Message m = new Message();
			m.what = FROB;
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
}
