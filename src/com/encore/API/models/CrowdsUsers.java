package com.encore.API.models;

import java.sql.Date;

public class CrowdsUsers {
	
	private int user_id, crowd_id;
	private Date created_at;
	
	public CrowdsUsers() {
		user_id = -1;
		crowd_id = -1;
		created_at = null;
	}

	public CrowdsUsers(int user_id, int crowd_id, Date created_at) {
		this.user_id = user_id;
		this.crowd_id = crowd_id;
		this.created_at = created_at;
	}

}
