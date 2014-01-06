package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Crowd {
	@SerializedName("id")
	private int id;
	
	@SerializedName("title")
	private String title;
	
	@SerializedName("members")
	private Profile[] members;

	public Crowd() {
		title = null;;
		members = null;
		id = -1;
	}

	public Crowd(String title, Profile[] members) {
		this.title = title;
		this.members = members;
	}

	public String getTitle() {
		return this.title;
	}
	
	public Profile[] getMembers() {
		return this.members;
	}

    public String[] getMembersFirstNames() {
        String[] names = new String[members.length];
        for(int i=0; i<members.length; i++) {
            names[i] = members[i].getFirstName();
        }
        return names;
    }
	
	public int getId() {
		return this.id;
	}

    public int getNumMembers() {
        return members.length;
    }

    public String toString() {
        return "Crowd id: " + id + ", title: " + title;
    }
	
}
