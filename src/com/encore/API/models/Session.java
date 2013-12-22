package com.encore.API.models;

import java.net.URL;
import java.util.ArrayList;
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
	private List<Comment> comments = new ArrayList<Comment>();
	
	@SerializedName("likes")
	private int numLikes;
	
	@SerializedName("thumbnail_url")
	private String thumbnailUrl;
	
	// GET only, use PostSession for POST
	public Session() {
		this.title = null;
		this.crowd = null;
		this.complete = false;
		this.comments = null;
		this.numLikes = 0;
		this.thumbnailUrl = null;
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
	
	public String getCrowdTitle() {
		return this.crowd.getTitle();
	}
	
	public int getCrowdId() {
		return this.crowd.getId();
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
	
	public int getLikes() {
		return numLikes;
	}
	
	public void setLikes(int numLikes) {
		this.numLikes = numLikes;
	}
	
	public String getThumbnailUrl() {
		return this.thumbnailUrl;
	}
	
	public void setThumbnailUrl(String url) {
		this.thumbnailUrl = url;
	}
	
	public String toString() {
		return "[" + "Title: " + this.title + ", \nCrowd Title" + this.crowd.getTitle() + ", \nCrowd ID: " + this.crowd.getId() +
				", \nThumbnail URL: " + this.thumbnailUrl + "]";
	}
}
