package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Sessions {
	@SerializedName("sessions")
	private Session[] sessions;
	
	public Sessions() {
		this.sessions = null;
	}
	
	public Sessions(Session[] sessions) {
		this.sessions = sessions;
	}
	
	public Session[] getSessions() {
		return this.sessions;
	}
}
