package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Profile implements UserInfo {
	@SerializedName("id")
	private int profileId;
	@SerializedName("user")
	private User user;
	@SerializedName("phone_number")
	private String phoneNumber;
	@SerializedName("friends")
	private Profile[] friends;
	
	public Profile() {
		this.profileId =  -1;
		this.user = new User();
		this.phoneNumber = null;
		this.friends = null;
	}
	
	public int getProfileId() {
		return profileId;
	}
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public void setFriends(Profile[] friends) {
		this.friends = friends;
	}
	
	public Profile[] getFriends() {
		return friends;
	}
	
	public String getEmail() {
		return user.getEmail();
	}
	
	@Override
	public String toString() {
		return "P_ID: " + profileId + " user: " + user.toString() + " " + "PhoneNo.: " + phoneNumber;
	}


	@Override
	public String getUsername() {
		return user.getUsername();
	}


	@Override
	public String getFirstName() {
		return user.getFirstName();
	}


	@Override
	public String getLastName() {
		return user.getLastName();
	}
	
	public String getFullName() {
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		return firstName + " " + lastName;
	}
}
