package com.encore.API.models;

import java.sql.Date;

public class FriendRequest {
	private int user_id, requested_id;
	Date created_at;
	
	public FriendRequest() {
		user_id = -1;
		requested_id = -1;
		created_at = null;
	}

	public FriendRequest(int user_id, int requested_id, Date created_at) {
		this.user_id = user_id;
		this.requested_id = requested_id;
		this.created_at = created_at;
	}
}
