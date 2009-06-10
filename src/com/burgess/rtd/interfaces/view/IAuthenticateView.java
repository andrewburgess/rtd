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

/**
 * @author Andrew
 *
 */
public interface IAuthenticateView {
	//Dialog constants
	public static final int GETTING_FROB_DIALOG = 0;
	public static final int FROB_PARSE_ERROR_DIALOG = 1;
	
	public Context getContext();
	public void createDialog(String message);
	public void dismissDialog();
	public void loadUrl(String url);
	public void createErrorDialog(int id);
	public SharedPreferences getPreferences();
	public void finish();
}
