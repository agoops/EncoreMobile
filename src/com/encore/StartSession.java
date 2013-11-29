package com.encore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class StartSession extends Activity {
	private static String tag = "StartSession";
	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder = null;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	
	String filepath = "/sdcard/voice8K16bitmono.pcm";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_session_fragment);

		setButtonHandlers();
		enableButtons(false);

		int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
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

	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we
									// use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format

	private void startRecording() {
		Long startTime = System.currentTimeMillis();
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

		recorder.startRecording();
		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");
		Long timeTaken = System.currentTimeMillis() - startTime;
		Log.d(tag, "setUp time: " + timeTaken);
		recordingThread.start();
	}

	// convert short to byte
	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}

	private void writeAudioDataToFile() {
		// Write the output audio in byte

		short sData[] = new short[BufferElements2Rec];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (isRecording) {
			// gets the voice output from microphone to byte format

			int whateverThisIs = recorder.read(sData, 0, BufferElements2Rec);
			System.out.println("recorder.read() returns: "+ whateverThisIs);
			for (int i = 0; i < 10; ++i) {
				System.out.println("value to file: [ "+sData[i]+" ]");
			}

			try {
				// // writes the data to file from buffer
				// // stores the voice buffer
				byte bData[] = short2byte(sData);
				os.write(bData, 0, BufferElements2Rec * BytesPerElement);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		// stops the recording activity
		if (null != recorder) {
			isRecording = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			recordingThread = null;
		}
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnStart: {
				Long startTime = System.currentTimeMillis();
				enableButtons(true);
				startRecording();
				Long timeTaken = System.currentTimeMillis() - startTime;
				Log.d(tag, "seconds taken: " + timeTaken);
				break;
			}
			case R.id.btnStop: {
				enableButtons(false);
				stopRecording();
				break;
			}
			case R.id.btnPlayback: {
				AudioPlayback track = new AudioPlayback(filepath);
				track.play();
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

// private String tag = "StartSession";
// Button record;
//
// boolean recording;
//
//
//
// @Override
// public void onCreate(Bundle savedInstanceState) {
// super.onCreate(savedInstanceState);
// Log.d(tag, "onCreate() StartSession");
// setContentView(R.layout.start_session);
// recording = false;
// record = (Button) findViewById (R.id.record_audio);
//
// setUp();
// }
//
//
//
// public void record(View view) {
// Button record = ((Button) view);
//
// if (recording) { //STOP RECORDING
//
// record.setTextColor(Color.BLACK);
// recording = false;
// }
//
// else { //START RECORDING
// record.setTextColor(Color.RED);
// recording = true;
// }
// }
//
// public void setUp() {
//
// }
// }
