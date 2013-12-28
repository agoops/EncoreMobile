package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Like {
    @SerializedName("id")
    private int userId;
    @SerializedName("username")
    private String username;
    @SerializedName("session")
    private Session likedSession;

	public Like() {
        this.userId = -1;
        this.username = null;
        this.likedSession = null;
	}

    public Like(int userId, String username, Session likedSession) {
        this.userId = userId;
        this.username = username;
        this.likedSession = likedSession;
    }

    public Session getLikedSession() {
        return likedSession;
    }
}
