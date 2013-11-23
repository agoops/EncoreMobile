package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Reply {
	@SerializedName("username")
	private String username;
	
	@SerializedName("accepted")
	private boolean accepted;
	
	public Reply() {
		this.username = null;
		this.accepted = false;
	}
	
	public Reply(String username, boolean accepted) {
		this.username = username;
		this.accepted = accepted;
	}
}
