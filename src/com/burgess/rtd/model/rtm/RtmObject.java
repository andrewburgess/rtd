/**
 * RtmObject.java
 * com.burgess.rtd.model.rtm
 *
 * Created Jun 8, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model.rtm;

import com.burgess.rtd.exceptions.RTDException;

/**
 * @author Andrew
 *
 */
public abstract class RtmObject {
	public String status;
	
	public abstract void parse(String data) throws RTDException;
}
