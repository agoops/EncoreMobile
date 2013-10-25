package com.encore.API.models;

import java.sql.Date;

public class Comment {
	private int comment_id, user_id, session_id; 
	private String text;
	private Date created_at;
	
	public Comment() {
		comment_id = -1;
		user_id = -1;
		session_id = -1;
		text = null;
		created_at = null;
	}

	public Comment(int comment_id, int user_id, int session_id, String text,
			Date created_at) {
		this.comment_id = comment_id;
		this.user_id = user_id;
		this.session_id = session_id;
		this.text = text;
		this.created_at = created_at;
	}

}
