/**
 * IListTasksView.java
 * com.burgess.rtd.interfaces.view
 *
 * Created Jun 24, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.interfaces.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

/**
 *
 */
public interface IListTasksView {
	public long getListId();
	public void setupTaskList(Cursor cursor);
	public Context getContext();
	public SharedPreferences getPreferences();
}
