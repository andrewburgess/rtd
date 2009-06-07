/**
 * IInitialView.java
 * com.burgess.rtd.interfaces.view
 *
 * Created Jun 3, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.interfaces.view;

import android.content.SharedPreferences;

/**
 * Interface to an InitialView activity for starting an application
 * 
 * @author Andrew Burgess
 */
public interface IInitialView {
	public SharedPreferences getPreferences();
	public void launchConfigureActivity();
}
