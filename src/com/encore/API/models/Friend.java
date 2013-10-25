package com.encore.API.models;

import java.sql.Date;

public class Friend {
	private int user_id, friend_id;
	Date created_at;
	
	public Friend() {
		user_id = -1;
		friend_id = -1;
		created_at = null;
	}

	public Friend(int user_id, int friend_id, Date created_at) {
		this.user_id = user_id;
		this.friend_id = friend_id;
		this.created_at = created_at;
	}
}
