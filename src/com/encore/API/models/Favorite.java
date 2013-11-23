package com.encore.API.models;

import java.sql.Date;

public class Favorite {
	private int favorite_id, user_id, session_id;
	private Date created_at;
	
	public Favorite() {
		favorite_id = -1;
		user_id = -1;
		session_id = -1;
	}

	public Favorite(int favorite_id, int user_id, int session_id,
			Date created_at) {
		this.favorite_id = favorite_id;
		this.user_id = user_id;
		this.session_id = session_id;
		this.created_at = created_at;
	}

}
