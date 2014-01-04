package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class PostComment {
	@SerializedName("session")
	private int sessionId;
	
	@SerializedName("text")
	private String commentText;
	
	public PostComment() {
		this.commentText = null;
		this.sessionId = -1;
	}
	
	public PostComment(int sessionId, String commentText) {
		this.sessionId = sessionId;
		this.commentText = commentText;
	}
}
