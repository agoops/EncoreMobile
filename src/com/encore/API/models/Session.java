package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Session {
	@SerializedName("title")
	private String title;
	
	@SerializedName("crowd")
	private Crowd crowd;
	
	public Session() {
		this.title = null;
		this.crowd = crowd;
	}
	
	public Session(String title, Crowd crowd) {
		this.title = title;
		this.crowd = crowd;
	}
}
