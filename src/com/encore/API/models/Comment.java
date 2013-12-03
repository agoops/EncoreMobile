package com.encore.API.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Comment {
	@SerializedName("user_id")
	private int creator;
	
	@SerializedName("session_id")
	private int session;
	
	@SerializedName("text")
	private String text;
	
	public Comment() {
		this.creator = 0;
		this.session = 0;
		this.text = null;
	}
	
	public Comment(int creator, int session, String text) {
		this.creator = creator;
		this.session = session;
		this.text = text;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}

	public void setSession(int session) {
		this.session = session;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public int getCreator() {
		return this.creator;
	}
	
	@Override 
	public String toString() {
		return "[Creator: "+creator+" Session: "+session+" Text: "+text+"]";
	}
}
