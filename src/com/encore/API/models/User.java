package com.encore.API.models;

import java.sql.Date;

public class User {
	private String username, 
		email, 
		first_name, 
		last_name, 
		password, 
		access_token,
		phone_number;
	private byte is_admin;
	private int user_id;
	private Date created_at;
	

	public User() {
		username = null;
		email = null;
		first_name = null;
		last_name = null;
		password = null;
		access_token = null;
		phone_number = null;
		is_admin = 0;
		user_id = -1;
		created_at = null;
	}

	public User(String username, String email, String first_name,
			String last_name, String password, String access_token,
			String phone_number, byte is_admin, int user_id, Date created_at) {
		this.username = username;
		this.email = email;
		this.first_name = first_name;
		this.last_name = last_name;
		this.password = password;
		this.access_token = access_token;
		this.phone_number = phone_number;
		this.is_admin = is_admin;
		this.user_id = user_id;
		this.created_at = created_at;
	}
}
