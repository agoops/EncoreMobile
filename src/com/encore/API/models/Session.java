package com.encore.API.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Session {
	
	@SerializedName("id")
	private int id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("crowd")
	private Crowd crowd;
	
	@SerializedName("is_complete")
	private boolean complete;
	
	@SerializedName("comments")
	private List<Comment> comments;
	
	// GET only, use PostSession for POST
	public Session() {
		this.title = null;
		this.crowd = null;
	}
	
	public Session(String title, Crowd crowd) {
		this.title = title;
		this.crowd = crowd;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Crowd getCrowd() {
		return crowd;
	}

	public void setCrowd(Crowd crowd) {
		this.crowd = crowd;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
