package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Profile implements UserInfo {
	@SerializedName("id")
	private int profileId;

	@SerializedName("user")
	private User user;

	@SerializedName("phone_number")
	private String phoneNumber;

	@SerializedName("friends")
	private Profile[] friends;

    @SerializedName("num_likes")
    private int numLikes;

    @SerializedName("num_friends")
    private int numFriends;

    @SerializedName("num_raps")
    private int numRaps;

    @SerializedName("profile_picture")
    private URL profilePicture;

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

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumFriends() {
        return numFriends;
    }

    public int getNumRaps() {
        return numRaps;
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

    public URL getProfilePicture() {
        return profilePicture;
    }

    public String getFullName() {
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		return firstName + " " + lastName;
	}
}
