package com.encore.API.models;

public class Clip {

	private int clip_id, creator_id, session_id;
	private double length;
	
	public Clip() {
		clip_id = -1;
		creator_id = -1;
		session_id = -1;
		length = -1;
	}
	
	public Clip(int clip_id, int creator_id, int session_id, double length) {
		this.clip_id = clip_id;
		this.creator_id = creator_id;
		this.session_id = session_id;
		this.length = length;
	}
}
