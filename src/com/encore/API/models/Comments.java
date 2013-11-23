package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Comments {
	@SerializedName("detail")
	private String detail;
	
	@SerializedName("comments")
	private Comment[] comments;
	
	public Comments() {
		detail = null;
		comments = null;
	}

	public Comments(String detail, Comment[] comments) {
		this.detail = detail;
		this.comments = comments;
	}
}
