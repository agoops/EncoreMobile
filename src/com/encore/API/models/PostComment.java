package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class PostComment {
	@SerializedName("id")
	private int sessionId;
	
	@SerializedName("comment_text")
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
