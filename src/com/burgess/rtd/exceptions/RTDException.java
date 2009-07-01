/**
 * RTDException.java
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
 * @author Andrew
 *
 */
public class RTDException extends Throwable {
	private static final long serialVersionUID = 1L;
	public RTDError error;
	
	public RTDException(int errorCode, int errorMessageId, boolean showIssueUrl, boolean isFatal) {
		this.error = new RTDError(errorCode, errorMessageId, showIssueUrl, isFatal);
	}

	public RTDException(int errorCode, int errorMessageId, boolean showIssueUrl, boolean isFatal, String detailMessage) {
		super(detailMessage);
		this.error = new RTDError(errorCode, errorMessageId, showIssueUrl, isFatal);
	}

	public RTDException(int errorCode, int errorMessageId, boolean showIssueUrl, boolean isFatal, Throwable throwable) {
		super(throwable);
		this.error = new RTDError(errorCode, errorMessageId, showIssueUrl, isFatal);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public RTDException(int errorCode, int errorMessageId, boolean showIssueUrl, boolean isFatal, String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		this.error = new RTDError(errorCode, errorMessageId, showIssueUrl, isFatal);
	}

}
