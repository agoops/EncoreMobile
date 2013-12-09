package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Like {
	// ====== Hasn't been updated from the Node version yet =======
	@SerializedName("session")
	private int sessionId;
	
	public Like() {
		this.sessionId = -1;
	}
	
	public Like(int sessionId) {
		this.sessionId = sessionId;
	}
}
