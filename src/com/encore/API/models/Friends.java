package com.encore.API.models;


public class Friends {
	private Friend[] friends;
	
	public Friends() {
		this.friends = null;
	}

	public Friends(Friend[] friends) {
		this.friends = friends;
	}
	
	public void setFriends(Friend[] friends) {
		this.friends = friends;
	}
}
