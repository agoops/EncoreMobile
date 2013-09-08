package com.encore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayback extends Activity {
	String tag = "VideoPlayback";
	VideoView videoView;
	Button playback;
	Button upload;
	Context context;
	String VIDEO_SOURCE;
	String AUDIO_SOURCE;

	Long songStart;
	Long recordStart;
	Long recordEnd;

	Long timeToWait;
	
	Audio audio;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_video);

		videoView = (VideoView) findViewById(R.id.playback_view2);
		playback = (Button) findViewById(R.id.playback2);
		upload = (Button) findViewById(R.id.upload);
		upload.setOnClickListener(startServiceListener);
		context = this;

		Intent i = getIntent();
		songStart = i.getLongExtra("songStart", 0L);
		recordStart = i.getLongExtra("recordStart", 0L);
		recordEnd = i.getLongExtra("recordEnd", 0L);
		AUDIO_SOURCE = i.getStringExtra("audioSource");
		VIDEO_SOURCE = i.getStringExtra("OUTPUT");
		Log.d(tag, "onCreate() VideoPlayback");
		Log.d(tag, "songStart: " + String.valueOf(songStart));
		Log.d(tag, "AUDIO SOURCE: " + AUDIO_SOURCE);
		Log.d(tag, "VIDEO SOURCE: " + VIDEO_SOURCE);
		timeToWait = recordStart - songStart;
		playback.setOnClickListener(playbackOnClickListener);
       
	}

	private Button.OnClickListener playbackOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			startAudio();
			Log.d(tag, "Audio started");
			Uri uri = Uri.parse(VIDEO_SOURCE);
			videoView.setVideoURI(uri);
			MediaController mc = new MediaController(context);
			videoView.setMediaController(mc);
			videoView.requestFocus();
			delayStartVideoView();
		}
	};
	
	private Button.OnClickListener startServiceListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(context, UploadService2.class);
			i.putExtra("path", VIDEO_SOURCE);
			context.startService(i);
			
			//startActivity(new Intent(context, AndroidVideoCapture.class));
		}
		           
	};
              
	@Override
	protected void onPause() {
		super.onPause();
		audio.cancel(true);
	}
	private void startAudio() {
		audio = new Audio(this, AUDIO_SOURCE, null);
		audio.execute(null,null);
	}
	

	private Handler mHandler = new Handler();

	private void delayStartVideoView() {
		mHandler.postDelayed(new Runnable() {
			public void run() {
				videoView.start();
			}
		}, timeToWait);
	}
}
