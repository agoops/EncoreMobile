package com.encore.models;


import com.google.gson.annotations.SerializedName;

public class Friends {
    @SerializedName("friends")
	private Profile[] friends;
	
	public Friends() {
		this.friends = null;
	}

	public Friends(Profile[] friends) {
		this.friends = friends;
	}
	
	public void setFriends(Profile[] friends) {
		this.friends = friends;
	}

    public Profile[] getFriends() {
        return this.friends;
    }

    public String toString() {
        String result = "";
        for(Profile friend : friends) {
            result += friend.toString() + ",\n";
        }
        return result;
    }
}
