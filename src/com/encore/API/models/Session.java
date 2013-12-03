package com.encore.API.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Session {
	
	@SerializedName("id")
	private int id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("crowd_title")
	private String crowdTitle;
	
	@SerializedName("crowd_id")
	private int crowdId;
	
	@SerializedName("is_complete")
	private int complete;
	
	@SerializedName("comments")
	private List<Comment> comments = new ArrayList<Comment>();
	
	// GET only, use PostSession for POST
	public Session() {
		this.title = null;
		this.crowdTitle = null;
		this.crowdId = -1;
	}
	
	public Session(String title, String crowdTitle, int crowdId) {
		this.title = title;
		this.crowdTitle = crowdTitle;
		this.crowdId = crowdId;
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
	
	public String getCrowdTitle() {
		return this.crowdTitle;
	}
	
	public int getCrowdId() {
		return this.id;
	}

	public int isComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public String toString() {
		return "[" + "Title: " + this.title + ", Crowd Title" + this.crowdTitle + ", Crowd ID: " + this.crowdId + "]";
	}
}
