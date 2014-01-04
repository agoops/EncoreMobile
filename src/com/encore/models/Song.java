package com.encore.models;

import java.sql.Date;

public class Song {
	private int song_id, session_id;
	private double duration;
	private String title, artist, song_url;
	private Date created_at;
	
	public Song() {
		song_id = -1;
		session_id = -1;
		duration = -1;
		title = null;
		artist = null;
		song_url = null;
		created_at = null;
	}
	
	public Song(int song_id, int session_id, double duration, String title,
			String artist, String song_url, Date created_at) {
		this.song_id = song_id;
		this.session_id = session_id;
		this.duration = duration;
		this.title = title;
		this.artist = artist;
		this.song_url = song_url;
		this.created_at = created_at;
	}
}
