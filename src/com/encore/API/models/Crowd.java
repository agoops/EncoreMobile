package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Crowd {
	@SerializedName("id")
	private int id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private DjangoUser[] members;
	
	public Crowd() {
		title = null;;
		members = null;
		id = -1;
	}

	public Crowd(String title, DjangoUser[] members, int id) {
		this.title = title;
		this.members = members;
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}
	
	public DjangoUser[] getMembers() {
		return this.members;
	}
	
	public int getId() {
		return this.id;
	}
	
}
