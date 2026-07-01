package com.exp.gateway.exception;

import org.springframework.security.core.AuthenticationException;

public class ServerException extends AuthenticationException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2159298868223307129L;

	public ServerException(String msg) {
		super(msg);
	
	}

}
