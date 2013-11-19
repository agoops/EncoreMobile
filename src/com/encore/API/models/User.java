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
	private Date created_at, updated_at;
	

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
		updated_at = null;
	}

	public User(String username, String password, String first_name,
			String last_name, String email, String phone_number ) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.first_name = first_name;
		this.last_name = last_name;
		this.phone_number = phone_number;
	}
	
	public String get_access_token() {
		return this.access_token;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
}
