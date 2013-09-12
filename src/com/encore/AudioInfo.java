package com.encore;

import android.content.Intent;
import android.util.Log;

public class AudioInfo {
	private static String tag = "AudioInfo";
	Long songStart;
	Long recordStart;
	Long recordEnd;
	String source;
	



	public Long getSongStart() {
		return songStart;
	}
	
	public Long getRecordDuration() {
		return recordEnd - recordStart;
	}

	public void setSongStart(Long songStart) {
		this.songStart = songStart;
		Log.d(tag, String.valueOf(songStart));
	}

	public Long getRecordStart() {
		return recordStart;
	}

	public void setRecordStart(Long recordStart) {
		this.recordStart = recordStart;
		Log.d(tag, String.valueOf(recordStart));

	}

	public Long getRecordEnd() {
		return recordEnd;
	}

	public void setRecordEnd(Long recordEnd) {
		this.recordEnd = recordEnd;
		Log.d(tag, String.valueOf(recordEnd));

	}

	public AudioInfo(String source) {
		this.source = source;
	}
	
	public void setUpIntent(Intent i) {
		Log.d(tag, "audioInfo AUDIO SOURCE: " + source);
		i.putExtra("songStart", songStart);
		i.putExtra("recordStart", recordStart);
		i.putExtra("recordEnd", recordEnd);
		i.putExtra("audioSource", source);
		
	}


	
}
