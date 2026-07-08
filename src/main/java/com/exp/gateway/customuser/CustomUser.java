package com.exp.gateway.customuser;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUser extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2734675886643552371L;
	private final UUID id;

	public CustomUser(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities) {

		super(username, password, authorities);
		this.id = id;

	}

	public UUID getId() {
		return id;
	}

}
