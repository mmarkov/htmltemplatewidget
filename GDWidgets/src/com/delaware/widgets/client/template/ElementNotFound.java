package com.delaware.widgets.client.template;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ElementNotFound extends Exception implements IsSerializable {

	private String message;

	private Throwable cause;

	public ElementNotFound() {
		super();
	}

	public ElementNotFound(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
		this.cause = cause;
	}

	public ElementNotFound(String message) {
		super(message);
		this.message = message;
	}

	public ElementNotFound(Throwable cause) {
		super(cause);
		this.cause = cause;
	}

	public String getMessage() {
		return this.message;
	}

	public Throwable getCause() {
		return this.cause;
	}

}
