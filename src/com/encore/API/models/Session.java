package com.encore.API.models;

import java.sql.Date;

public class Session {
	private int session_id, num_clips, 
		crowd_id, likes;
	private byte is_complete;
	private String title, video_url;
	private Date created_at;
	
	public Session() {
		session_id = -1;
		num_clips = -1;
		crowd_id = -1;
		likes = -1;
		is_complete = 0;
		title = null;
		video_url = null;
		created_at = null;
	}

	public Session(int session_id, int num_clips, int crowd_id, int likes,
			byte is_complete, String title, String video_url, Date created_at) {
		this.session_id = session_id;
		this.num_clips = num_clips;
		this.crowd_id = crowd_id;
		this.likes = likes;
		this.is_complete = is_complete;
		this.title = title;
		this.video_url = video_url;
		this.created_at = created_at;
	}
}
