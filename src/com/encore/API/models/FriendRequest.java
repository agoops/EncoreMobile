package com.encore.API.models;

import java.sql.Date;

import com.google.gson.annotations.SerializedName;

public class FriendRequest {
	@SerializedName("username")
	private String username;
	
	@SerializedName("pending_me")
	private String[] pendingMe;
	
	@SerializedName("pending_them")
	private String[] pendingThem;
	
	// GET: Use "username" field, others null
	// POST: Use pendingMe and pendingThem, username null;
	public FriendRequest() {
		this.username = null;
		this.pendingMe = null;
		this.pendingThem = null;
	}

	public FriendRequest(String username, String[] pendingMe, String[] pendingThem) {
		this.username = username;
		this.pendingMe = pendingMe;
		this.pendingThem = pendingThem;
	}
}
