/**
 * Error.java
 * com.burgess.rtd.exceptions
 *
 * Created Jun 11, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.exceptions;

/**
 * Represents an error object that can be passed around the application from
 * the various components up to the UI
 */
public class RTDError {
	public int errorCode;
	public int errorMessageId;
	public boolean showIssueUrl;
	
	public RTDError(int errorCode, int errorMessageId, boolean showIssueUrl) {
		this.errorCode = errorCode;
		this.errorMessageId = errorMessageId;
		this.showIssueUrl = showIssueUrl;
	}
}
