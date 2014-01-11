package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by babakpourkazemi on 12/27/13.
 */
public class Likes {

    private static final String TAG = "Likes";

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

    public List<String> getLikedSessionTitles() {
        List<String> sessionTitles = new ArrayList<String>(likes.length);
        for(int i=0; i<likes.length; i++) {
            String title = likes[i].getLikedSession().getTitle();
            sessionTitles.add(title);
        }
        return sessionTitles;
    }

    public Session[] getLikedSessions() {
        Session[] likedSessions = new Session[likes.length];
        for(int i=0; i<likes.length; i++) {
            likedSessions[i] = likes[i].getLikedSession();
        }
        return likedSessions;
    }

    public ArrayList<Session> getLikedSessionsList() {
        ArrayList<Session> likedSessions = new ArrayList<Session>(likes.length);
        for(int i=0; i<likes.length; i++) {
            likedSessions.add(likes[i].getLikedSession());
        }
        return likedSessions;
    }
}
