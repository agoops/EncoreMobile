package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Crowd {
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private Profile[] members;
	
	public Crowd() {
		title = null;;
		members = null;
	}

	public Crowd(String title, Profile[] members) {
		this.title = title;
		this.members = members;
	}

	public String getTitle() {
		return this.title;
	}
	
	public Profile[] getMembers() {
		return this.members;
	}
	
}
