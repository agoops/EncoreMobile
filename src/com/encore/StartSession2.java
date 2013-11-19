package com.encore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class StartSession2 extends Activity {
	
	boolean isRecording = false;
	String tag = "StartSession2";
	Thread recordThread = null;
	int sampleRateInHz = 11025;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_session);

		setButtonHandlers();
		enableButtons(false);

	}
	
	public void record() {
		Long time1 = System.currentTimeMillis();
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/testaudio.pcm");

		// Delete any previous recording.
		if (file.exists())
			file.delete();

		// Create the new file.
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create "
					+ file.toString());
		}

		try {
			// Create a DataOuputStream to write the audio data into the saved
			// file.
			OutputStream os = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			DataOutputStream dos = new DataOutputStream(bos);

			// Create a new AudioRecord object to record the audio.
			int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz,
					channelConfiguration, audioEncoding);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, sampleRateInHz,
					channelConfiguration, audioEncoding, bufferSize);

			short[] buffer = new short[bufferSize];
			Long time2 = System.currentTimeMillis();
			audioRecord.startRecording();
			Long time3 = System.currentTimeMillis();
			Log.d(tag, "Time to set up audiorecord: " + (time2-time1));
			Log.d(tag,"Time from startRecording() to while loop: " + (time3-time2));
			
			while (isRecording) {
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
				for (int i = 0; i < bufferReadResult; i++)
					dos.writeShort(buffer[i]);
			}

			audioRecord.stop();
			dos.close();

		} catch (Throwable t) {
			Log.e("AudioRecord", "Recording Failed");
		}
	}

	public void play() {
		// Get the file we want to playback.
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/testaudio.pcm");
		// Get the length of the audio stored in the file (16 bit so 2 bytes per
		// short)
		// and create a short array to store the recorded audio.
		int musicLength = (int) (file.length() / 2);
		short[] music = new short[musicLength];
		Log.d(tag,"size of file: " + file.length());
		
		
		try {
			// Create a DataInputStream to read the audio data back from the
			// saved file.
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);

			// Read the file into the music array.
			int i = 0;
			while (dis.available() > 0) {
				music[i] = dis.readShort();
				i++;
			}

			// Close the input streams.
			dis.close();

			// Create a new AudioTrack object using the same parameters as the
			// AudioRecord
			// object used to create the file.
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, musicLength,
					AudioTrack.MODE_STREAM);
			// Start playback
			audioTrack.play();

			// Write the music buffer to the AudioTrack object
			audioTrack.write(music, 0, musicLength);

		} catch (Throwable t) {
			Log.e("AudioTrack", "Playback Failed");
		}
	}
	private void setButtonHandlers() {
		((Button) findViewById(R.id.btnStart)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnPlayback)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnStop, isRecording);
	}


	private View.OnClickListener btnClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnStart: {
				enableButtons(true);
				isRecording = true;
				recordThread = new Thread(new Runnable() {
					public void run() {
						record();
					}
				});
				recordThread.start();
				break;
			}
			case R.id.btnStop: {
				enableButtons(false);
				isRecording = false;
				break;
			}
			case R.id.btnPlayback: {
				try {
					recordThread.join();
				} catch (Exception e) {
				}

				play();
				//finish();

			}
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

