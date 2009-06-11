/**
 * IAuthenticateView.java
 * com.burgess.rtd.interfaces.view
 *
 * Created Jun 8, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.interfaces.view;

import android.content.Context;
import android.content.SharedPreferences;

import com.burgess.rtd.exceptions.RTDError;

/**
 * @author Andrew
 *
 */
public interface IAuthenticateView {
	//Dialog constants
	public static final int GETTING_FROB_DIALOG = 0;
	public static final int FROB_PARSE_ERROR_DIALOG = 1;
	
	public Context getContext();
	public void showDialog(int id);
	public void loadUrl(String url);
	public void createErrorDialog(RTDError error);
	public SharedPreferences getPreferences();
	public void finish();
	public void removeDialog(int id);
}
