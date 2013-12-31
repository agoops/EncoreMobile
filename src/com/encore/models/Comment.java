package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id")
    private int id;

    @SerializedName("commenter")
    private String commenter;

    @SerializedName("session")
    private int sessionID;

	@SerializedName("text")
	private String text;

    public Comment() {
        this.id = -1;
        this.sessionID = -1;
        this.commenter = null;
        this.text = null;
    }

    public Comment(int id, String commenter, int sessionID, String text) {
        this.id = id;
        this.commenter = commenter;
        this.sessionID = sessionID;
        this.text = text;
    }

    public void setSessionId(int session) {
		this.sessionID = session;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommenter() {
        return commenter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
	public String toString() {
		return "[Commenter: " + commenter + ", Session: " + sessionID + ", Text: " + text + "]";
	}
}
