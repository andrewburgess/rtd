/**
 * IListsView.java
 * com.burgess.rtd.interfaces.view
 *
 * Created Jun 22, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.interfaces.view;

import com.burgess.rtd.exceptions.RTDError;

import android.content.Context;
import android.database.Cursor;

/**
 *
 */
public interface IListsView {
	public Context getContext();
	public void setListsCursor(Cursor cursor);
	public void createErrorDialog(RTDError error);
}
