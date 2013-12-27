package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Favorite {
	
	
	// ======== Unused in Django ==========
	
	
	@SerializedName("session")
	private int sessionId;
	
	public Favorite() {
		this.sessionId = -1;
	}
	
	public Favorite(int sessionId) {
		this.sessionId = sessionId;
	}
}
