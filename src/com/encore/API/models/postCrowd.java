package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class postCrowd {
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private String[] members;
	
	public postCrowd() {
		this.title = null;
		this.members = null;
	}
	
	public postCrowd(String title, String[] members) {
		this.title = title;
		this.members = members;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMembers(String[] members) {
		this.members = members;
	}
}
