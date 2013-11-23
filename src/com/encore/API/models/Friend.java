package com.encore.API.models;

import java.sql.Date;

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
}
