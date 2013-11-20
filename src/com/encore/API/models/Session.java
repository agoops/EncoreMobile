package com.encore.API.models;

import java.sql.Date;

public class Session {
	private int session_id, num_clips, 
		crowd_id, num_likes, num_shares, num_comments;
	private byte is_complete;
	private String title, video_url;
	private Date created_at;
	
	public Session() {
		session_id = -1;
		num_clips = -1;
		crowd_id = -1;
		num_likes = -1;
		num_shares = -1;
		num_comments = -1;
		is_complete = 0;
		title = null;
		video_url = null;
		created_at = null;
	}

	public Session(int session_id, int num_clips, int crowd_id, int likes,
			byte is_complete, String title, String video_url, Date created_at, 
			int shares, int comments) {
		this.session_id = session_id;
		this.num_clips = num_clips;
		this.crowd_id = crowd_id;
		this.num_likes = likes;
		this.num_shares = shares;
		this.num_comments = comments;
		this.is_complete = is_complete;
		this.title = title;
		this.video_url = video_url;
		this.created_at = created_at;
	}

	public int getNum_likes() {
		return num_likes;
	}

	public int getNum_shares() {
		return num_shares;
	}

	public int getNum_comments() {
		return num_comments;
	}

	public String getTitle() {
		return title;
	}

	public String getVideo_url() {
		return video_url;
	}

	public Date getCreated_at() {
		return created_at;
	}
	
	
}
