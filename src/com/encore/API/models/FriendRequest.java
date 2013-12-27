package com.encore.API.models;

import java.sql.Date;

import com.google.gson.annotations.SerializedName;

public class FriendRequest {
	@SerializedName("username")
	private String username;
	
	@SerializedName("pending_me")
	private User[] pendingMe;
	
	@SerializedName("pending_them")
	private User[] pendingThem;
	
	// GET: Use "username" field, others null
	// POST: Use pendingMe and pendingThem, username null;
	public FriendRequest() {
		this.username = null;
		this.pendingMe = null;
		this.pendingThem = null;
	}

	public FriendRequest(String username, User[] pendingMe, User[] pendingThem) {
		this.username = username;
		this.pendingMe = pendingMe;
		this.pendingThem = pendingThem;
	}
	
	public User[] getPendingMe() {
		return this.pendingMe;
	}
	
	public User[] getPendingThem() {
		return this.pendingThem;
	}
	
	public String toString() {
		return "username: " + username + ", pendingMe: " + pendingMe.toString() + ", pendingThem: " + pendingThem.toString(); 
	}
}
