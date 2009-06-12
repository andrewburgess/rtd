/**
 * SyncService.java
 * com.burgess.rtd.service
 *
 * Created Jun 12, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 */
public class SyncService extends Service {
	
	@Override
	public void onCreate() {
		
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
