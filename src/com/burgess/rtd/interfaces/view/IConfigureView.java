/**
 * IConfigureView.java
 * com.burgess.rtd.interfaces.view
 *
 * Created Jun 7, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.interfaces.view;

import android.content.SharedPreferences;
import android.content.Context;

public interface IConfigureView {
	public SharedPreferences getPreferences();
	public void configureAuthentication(boolean isAuthenticated);
	public void setAuthStatus(String status);
	public int getSyncTime();
	public void setSyncTime(int value);
	public Context getContext();
	public void showDialog(int id);
	public void removeDialog(int id);
}
