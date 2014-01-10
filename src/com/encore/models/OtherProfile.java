package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by babakpourkazemi on 1/8/14.
 */
public class OtherProfile {
    @SerializedName("profile")
    private Profile profile;

    @SerializedName("likes")
    private Like[] likes;

    public OtherProfile() {
        this.profile = null;
        this.likes = null;
    }

    public OtherProfile(Profile profile, Like[] likes) {
        this.profile = profile;
        this.likes = likes;
    }

    public Profile getProfile() {
        return profile;
    }

    public Like[] getLikes() {
        return likes;
    }

    public List<Like> getLikesList() {
        return Arrays.asList(likes);
    }

    public List<Session> getLikedSessions(){
        List<Session> likedSessions = new ArrayList<Session>(likes.length);
        for(Like like : likes) {
            likedSessions.add(like.getLikedSession());
        }
        return likedSessions;
    }

    public Profile[] getFriends() {
        return profile.getFriends();
    }

    public List<Profile> getFriendsProfilesAsList() {
        return new ArrayList<Profile>(Arrays.asList(profile.getFriends()));
    }

    public Set<String> getFriendsUsernamesAsSet() {
        Profile[] friendArray = profile.getFriends();
        Set<String> friendsUsernames = new HashSet<String>(friendArray.length);

        for(Profile friend : friendArray) {
            friendsUsernames.add(friend.getUsername());
        }
        return friendsUsernames;
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public String getFullName() {
        return profile.getFullName();
    }

    public int getNumRaps() {
        return profile.getNumRaps();
    }

    public int getNumLikes() {
        return profile.getNumLikes();
    }

    public int getNumFriends() {
        return profile.getNumFriends();
    }
}
