package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class PostCrowd {
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private int[] members;
	
	public PostCrowd() {
		this.title = null;
		this.members = null;
	}
	
	public PostCrowd(String title, int[] members) {
		this.title = title;
		this.members = members;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMembers(int[] members) {
		this.members = members;
	}
}
