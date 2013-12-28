package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 12/27/13.
 */
public class Likes {

    @SerializedName("likes")
    private Like[] likes;

    public Likes() {
        this.likes = null;
    }

    public Likes(Like[] likes) {
        this.likes = likes;
    }

    public Like[] getLikes() {
        return likes;
    }

    public int[] getLikedSessionIds() {
        int[] sessionIds = new int[likes.length];
        for(int i=0; i<likes.length; i++) {
            sessionIds[i] = likes[i].getLikedSession().getId();
        }
        return sessionIds;
    }

    public Session[] getLikedSessions() {
        Session[] likedSessions = new Session[likes.length];
        for(int i=0; i<likes.length; i++) {
            likedSessions[i] = likes[i].getLikedSession();
        }
        return likedSessions;
    }
}
