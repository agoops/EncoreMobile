package com.encore;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class AndroidVideoCapture extends Activity implements
		SurfaceHolder.Callback {
	private static String tag = "AndroidVideoCapture";
	private String OUTPUT = "/sdcard/myvideo.mp4";
	private Audio audio;
	private AudioInfo audioInfo;
	Button myButton;
	MediaRecorder mediaRecorder;
	SurfaceHolder surfaceHolder;
	boolean recording;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "onCreate() AndroidVideoCapture");

		recording = false;

		mediaRecorder = new MediaRecorder();
		initMediaRecorder();

		setContentView(R.layout.capture_video);

		SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = myVideoView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		myButton = (Button) findViewById(R.id.mybutton);
		myButton.setOnClickListener(myButtonOnClickListener);
		audioInfo = new AudioInfo();
		audio = new Audio(this, "");
		audio.execute(null, null);
		audioInfo.setSongStart(System.currentTimeMillis());
	}

	private Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (recording) {
				audioInfo.setRecordEnd(System.currentTimeMillis());
				mediaRecorder.stop();
				mediaRecorder.release();
				
				audio.cancel(true);
				setResult();
				Log.d(tag, "stop pressed");
				//finish();
			} else {
				mediaRecorder.start();
				audioInfo.setRecordStart(System.currentTimeMillis());
				recording = true;
				Log.d(tag, "rec pressed");

				myButton.setText("STOP");
			}
		}
	};
	
	private void setResult() {
		Intent data = new Intent();
		data.putExtra("file", OUTPUT);
		setResult(RESULT_OK, data);
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		prepareMediaRecorder();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	private void initMediaRecorder() {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		CamcorderProfile camcorderProfile_HQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		mediaRecorder.setProfile(camcorderProfile_HQ);
		mediaRecorder.setOutputFile(OUTPUT);
		mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
		mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M
	}

	private void prepareMediaRecorder() {
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}