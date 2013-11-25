package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Crowds {
	@SerializedName("crowds")
	private Crowd[] crowds;
	
	public Crowds() {
		this.crowds = null;
	}
	
	public Crowds(Crowd[] crowds) {
		this.crowds = crowds;
	}
	
	public Crowd[] getCrowds() {
		return this.crowds;
	}
}
