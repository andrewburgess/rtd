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

import android.os.Handler;
import android.os.Message;

import com.burgess.rtd.constants.RTM;
import com.burgess.rtd.interfaces.view.IAuthenticateView;
import com.burgess.rtd.model.RTMModel;
import com.burgess.rtd.model.Request;
import com.burgess.rtd.model.rtm.GetFrob;

/**
 * @author Andrew
 *
 */
public class AuthenticateController {
	private static final int FROB = 1;
	
	private IAuthenticateView view;
	private RTMModel rtm;
	private GetFrob frob;
	
	private Handler handler = new Handler() {
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
	
	private Thread getFrobThread = new Thread() {
		public void run() {
			Request r = new Request(RTM.GET_FROB);
			frob = new GetFrob();
			frob.parse(rtm.execute(RTM.PATH, r));
			
			Message m = new Message();
			m.what = FROB;
			handler.sendMessage(m);
		}
	};
	
	public AuthenticateController(IAuthenticateView view) {
		this.view = view;
		
		rtm = new RTMModel(view.getContext());
	}
	
	public void initializeView() {
		getFrobThread.start();
		view.createDialog("Obtaining Auth URL");
	}
}
