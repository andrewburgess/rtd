/**
 * NetworkUnavailableException.java
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
 *
 */
public class NetworkUnavailableException extends Throwable {
	private static final long serialVersionUID = 1L;

	public NetworkUnavailableException() {
		super();
	}

	public NetworkUnavailableException(String detailMessage) {
		super(detailMessage);
	}

	public NetworkUnavailableException(Throwable throwable) {
		super(throwable);
	}

	public NetworkUnavailableException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
