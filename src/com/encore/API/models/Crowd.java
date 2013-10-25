package com.encore.API.models;

import java.sql.Date;

public class Crowd {
	
	private int crowd_id, num_members;
	private Date created_at;
	
	public Crowd() {
		crowd_id = -1;
		num_members = -1;
		created_at = null;
	}

	public Crowd(int crowd_id, int num_members, Date created_at) {
		this.crowd_id = crowd_id;
		this.num_members = num_members;
		this.created_at = created_at;
	}
}
