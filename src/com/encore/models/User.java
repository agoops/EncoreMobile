package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class User implements UserInfo{

	/*
	 *{"friends": [
	 *	{"id": 2, 
	 *	 "user": {
	 *		"id": 4, "first_name": "Michael", "last_name": "Paris", 
	 *		"email": "mlparis92@gmail.com", "username": "mikeyp", 
	 *		"date_joined": "2013-11-05T03:59:38.518Z"}, 
	 *	 "phone_number": "757-285-6806"}, 
	 *
	 *	{"id": 3, 
	 *	 "user": {
	 *		"id": 5, "first_name": "rhymeslayer", "last_name": "gupta",
	 *		 "email": "ag7bf@virginia.edu", "username": "anskeet", 
	 *		"date_joined": "2013-11-19T01:52:19Z"}, 
	 *	"phone_number": "7572899330"}
	 * ]}
	 */

	@SerializedName("id")
	private int userId;
	
	@SerializedName("first_name")
	private String firstName;
	
	@SerializedName("last_name")
	private String lastName;
	
	@SerializedName("email")
	private String email;
	
	@SerializedName("username")
	private String username;
	
	@SerializedName("date_joined")
	private transient Date dateJoined;
	

	public User() {
		username = null;
		email = null;
		firstName = null;
		lastName = null;
		userId = -1;
		dateJoined = null;
	}

	public User(int userId, String username, String firstName,
			String lastName, String email ) {
		this.userId = userId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.dateJoined = null;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}
	
	@Override
	public String toString() {
		return "ID: " + userId + " Username: " + username + " Name: "
				+ firstName + " " + lastName + " " + " Email: " + email;
	}

	public boolean validate() {
		if (this.userId != -1 && this.username != null && this.firstName != null
				&& this.lastName != null && this.email != null) {
			return true;
		}
		else {
			return false;
		}
	}
}
