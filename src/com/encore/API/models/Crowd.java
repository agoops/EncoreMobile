package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Crowd {
	@SerializedName("id")
	private int id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private Profile[] members;
	
	public Crowd() {
		title = null;;
		members = null;
		id = -1;
	}

//	public Crowd(String title, DjangoUser[] members, int id) { // Make sure DjangoUser can be ridden
	public Crowd(String title, Profile[] members) {
		this.title = title;
		this.members = members;
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}
	
	public Profile[] getMembers() {
		return this.members;
	}
	
	public int getId() {
		return this.id;
	}
	
}
