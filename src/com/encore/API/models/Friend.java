package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Friend {
	private String[] friends;
    
    public Friend() {
            this.friends = null;
    }

    public Friend(String[] friends) {
            this.friends = friends;
    }
    
    public void setFriends(String[] friends) {
            this.friends = friends;
    }
	
//	@SerializedName("id")
//	private int id;
//	
//	@SerializedName("user")
//	private User user;
//	
//	@SerializedName("phone_number")
//	private int phoneNumber;
//	
//	public Friend() {
//		this.id = -1;
//		this.user = null;
//		this.phoneNumber = -1;
//	}
//	
//	public Friend(int id, User user, int phoneNumber) {
//		this.id = id;
//		this.user = user;
//		this.phoneNumber = phoneNumber;
//	}
}
