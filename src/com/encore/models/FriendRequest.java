package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class FriendRequest {
	@SerializedName("username")
	private String username;

	@SerializedName("pending_me")
	private FriendRequestProfile[] pendingMe;
	
	@SerializedName("pending_them")
	private FriendRequestProfile[] pendingThem;
	
	// GET: Use "username" field, others null
	// POST: Use pendingMe and pendingThem, username null;
	public FriendRequest() {
		this.username = null;
		this.pendingMe = null;
		this.pendingThem = null;
	}

	public FriendRequest(String username, FriendRequestProfile[] pendingMe, FriendRequestProfile[] pendingThem) {
		this.username = username;
		this.pendingMe = pendingMe;
		this.pendingThem = pendingThem;
	}
	
	public FriendRequestProfile[] getPendingMe() {
		return this.pendingMe;
	}
	
	public FriendRequestProfile[] getPendingThem() {
		return this.pendingThem;
	}
	
	public String toString() {
		return "username: " + username + ", pendingMe: " + pendingMe.toString() + ", pendingThem: " + pendingThem.toString(); 
	}
}
