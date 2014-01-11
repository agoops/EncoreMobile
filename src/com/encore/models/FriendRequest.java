package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FriendRequest {
	@SerializedName("username")
	private String username;

	@SerializedName("pending_me")
	private FriendRequestProfile[] pendingMe;
	
	@SerializedName("pending_them")
	private FriendRequestProfile[] pendingThem;
	
	// GET: Use "username" field, others null
	// POST: Use pendingMe and pendingThem, username null;
	public FriendRequest() {
		this.username = null;
		this.pendingMe = null;
		this.pendingThem = null;
	}

	public FriendRequest(String username, FriendRequestProfile[] pendingMe, FriendRequestProfile[] pendingThem) {
		this.username = username;
		this.pendingMe = pendingMe;
		this.pendingThem = pendingThem;
	}
	
	public FriendRequestProfile[] getPendingMe() {
		return this.pendingMe;
	}
	
	public FriendRequestProfile[] getPendingThem() {
		return this.pendingThem;
	}

    public List<User> getPendingMeUsers() {
        List<User> pendingMeList = new ArrayList<User>(pendingMe.length);
        for(FriendRequestProfile request : pendingMe) {
            pendingMeList.add(request.getSender());
        }
        return pendingMeList;
    }

    public List<User> getPendingThemUsers() {
        List<User> pendingThemList = new ArrayList<User>(pendingThem.length);
        for(FriendRequestProfile request : pendingThem) {
            pendingThemList.add(request.getRequested());
        }
        return pendingThemList;
    }

    public List<String> getPendingThemUsernames() {
        List<String> pendingThemUsernames = new ArrayList<String>(pendingThem.length);
        for(FriendRequestProfile request : pendingThem) {
            pendingThemUsernames.add(request.getRequested().getUsername());
        }
        return pendingThemUsernames;
    }

	public String toString() {
		return "username: " + username + ", pendingMe: " + pendingMe.toString() + ", pendingThem: " + pendingThem.toString(); 
	}
}
