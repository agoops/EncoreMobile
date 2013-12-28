package com.encore;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioPlayback {
	private String tag = "AudioPlayback";
	
	String filepath = null;
	public AudioPlayback (String filepath) {
		this.filepath = filepath;
	}
	
	public void play() {
		try {
			this.playAudioFileViaAudioTrack();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void playAudioFileViaAudioTrack() throws IOException {
		Long startTime = System.currentTimeMillis();
		// We keep temporarily filePath globally as we have only two sample
		// sounds now..
		if (filepath == null)
			return;

		int intSize = android.media.AudioTrack.getMinBufferSize(44100,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);

		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);

//		if (at == null) {
//			Log.d("TCAudio", "audio track is not initialised ");
//			return;
//		}

		int count = 512 * 1024; // 512 kb
		// Reading the file..
		byte[] byteData = null;
		File file = null;
		file = new File(filepath);

		byteData = new byte[(int) count];
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int bytesread = 0, ret = 0;
		int size = (int) file.length();
		
		Long timeTaken = System.currentTimeMillis() - startTime;
		Log.d(tag, "set up audio track time: " + timeTaken);
		at.play();
		while (bytesread < size) {
			ret = in.read(byteData, 0, count);
			if (ret != -1) {
				// Write the byte array to the track
				at.write(byteData, 0, ret);
				bytesread += ret;
			} 
			else
				break;
		}
		in.close();
		at.stop();
		at.release();
	}

}
