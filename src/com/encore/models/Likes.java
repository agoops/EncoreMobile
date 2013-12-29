package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;

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

    public HashSet<Integer> getLikedSessionIds() {
        HashSet<Integer> sessionIds = new HashSet<Integer>(likes.length);
        for(int i=0; i<likes.length; i++) {
            int id = likes[i].getLikedSession().getId();
            sessionIds.add(id);
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
