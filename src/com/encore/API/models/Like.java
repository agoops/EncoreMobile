package com.encore.API.models;

import java.sql.Date;

public class Like {
	private int like_id, user_id, session_id;
	private Date created_at;
	
	public Like() {
		like_id = -1;
		user_id = -1;
		session_id = -1;
	}

	public Like(int like_id, int user_id, int session_id, Date created_at) {
		super();
		this.like_id = like_id;
		this.user_id = user_id;
		this.session_id = session_id;
		this.created_at = created_at;
	}

}
