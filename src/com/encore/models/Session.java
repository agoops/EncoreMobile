package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Session {
	
	@SerializedName("id")
	private int id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("is_complete")
	private boolean complete;
	
	@SerializedName("comments")
	private List<Comment> comments = new ArrayList<Comment>();
	
	@SerializedName("likes")
	private int numLikes;
	
	@SerializedName("thumbnail_url")
	private String thumbnailUrl;

    @SerializedName("clip_url")
    private String clipUrl;

    @SerializedName("clips")
    private List<Clip> clips = new ArrayList<Clip>();

    @SerializedName("created")
    private String created;

    @SerializedName("modified")
    private String modified;

    @SerializedName("session_creator")
    private Profile sessionCreator;

    @SerializedName("session_receiver")
    private Profile sessionReceiver;

    @SerializedName("is_battle")
    private boolean isBattle;

	// GET only, use PostSession for POST


    public Session(int id, String title, boolean complete, List<Comment> comments,
                   int numLikes, String thumbnailUrl, String clipUrl,
                   List<Clip> clips, String created, String modified,
                   Profile sessionCreator, Profile sessionReceiver, boolean isBattle) {
        this.id = id;
        this.title = title;
        this.complete = complete;
        this.comments = comments;
        this.numLikes = numLikes;
        this.thumbnailUrl = thumbnailUrl;
        this.clipUrl = clipUrl;
        this.clips = clips;
        this.created = created;
        this.modified = modified;
        this.sessionCreator = sessionCreator;
        this.sessionReceiver = sessionReceiver;
        this.isBattle = isBattle;
    }

    public Session(String title) {
		this.title = title;
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

    public String getClipUrl() { return this.clipUrl;}

    public List<Clip> getClips() {
        return clips;
    }

    public void setClipUrl(String url) { this.clipUrl=url;}

    public String getCreated() { return created; }

    public String getModified() {
        return modified;
    }

    public Profile getSessionCreator() {
        return sessionCreator;
    }

    public Profile getSessionReceiver() {
        return sessionReceiver;
    }

    public boolean isBattle() {
        return isBattle;
    }

    public String toString() {
        return "[" + "Title: " + this.title + ", \nThumbnail URL: " + this.thumbnailUrl +
                " \nClip URL: " + this.clipUrl + " \n]";
	}
}
