package com.encore.API.models;

import java.sql.Date;

import com.google.gson.annotations.SerializedName;

public class DjangoUser {
	@SerializedName("id")
	private int pythonId;
	
	@SerializedName("user")
	private User user;
	
	@SerializedName("phone_number")
	private String phoneNumber;
	
	
	// Django defines its own "user", 
	// which wraps around our own
	public DjangoUser() {
		this.pythonId = -1;
		this.user = null;
		this.phoneNumber = null;
	}

	public DjangoUser(int pythonId, User user, String phoneNumber) {
		this.pythonId = pythonId;
		this.user = user;
		this.phoneNumber = phoneNumber;
	}

	public int getPythonId() {
		return pythonId;
	}

	public User getUser() {
		return user;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
}
