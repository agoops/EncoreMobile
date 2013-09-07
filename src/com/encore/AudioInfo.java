package com.encore;

import android.content.Context;
import android.util.Log;

public class AudioInfo {
	private static String tag = "AudioInfo";
	Long songStart;
	Long recordStart;
	Long recordEnd;
	
	



	public Long getSongStart() {
		return songStart;
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

	public AudioInfo() {
		
	}
	
}
