package com.encore.models;

public class AccessToken {
	private String access_token;
	
	public AccessToken(String access_token) {
		this.access_token = access_token;
	}

	public String get_access_token() {
		return access_token;
	}
}
