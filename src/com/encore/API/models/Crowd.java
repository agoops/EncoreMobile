package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Crowd {
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private DjangoUser[] members;
	
	public Crowd() {
		title = null;;
		members = null;
	}

	public Crowd(String title, DjangoUser[] members) {
		this.title = title;
		this.members = members;
	}

	public String getTitle() {
		return this.title;
	}
	
	public DjangoUser[] getMembers() {
		return this.members;
	}
	
}
