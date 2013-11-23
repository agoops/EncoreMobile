package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Comment {
	@SerializedName("detail")
	private String detail;
	
	@SerializedName("comments")
	private String[] comments;
	
	public Comment() {
		detail = null;
		comments = null;
	}

	public Comment(String detail, String[] comments) {
		this.detail = detail;
		this.comments = comments;
	}
}
