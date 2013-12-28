package com.encore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AndroidVideoCapture extends Activity implements
		SurfaceHolder.Callback {
	private static String tag = "AndroidVideoCapture";
	private Context context = this;
	private String OUTPUT = "/sdcard/myvideo.mp4";
	private AudioAsyncTask audio;
	private RecordAsyncTask recordTask;
	private String AUDIO_SOURCE = "android.resource://com.encore/raw/beat";             
	private AudioInfo audioInfo;
	private Handler mHandler;
	Button restart;
	Button send;
	MediaRecorder mediaRecorder;
	SurfaceHolder surfaceHolder;
	Camera camera;
	boolean recording;
	
	TextView three;
	TextView two;
	TextView one;
	TextView go;
	Iterator<TextView> iter;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag, "onCreate() AndroidVideoCapture");
		setContentView(R.layout.capture_video);
		recording = false;
//		three = (TextView)findViewById(R.id.three);
//		two = (TextView)findViewById(R.id.two);
//		one = (TextView)findViewById(R.id.one);
//		go = (TextView)findViewById(R.id.go);
		                                            
		List<TextView> views = new ArrayList<TextView>(Arrays.asList(three,two,one,go));
		iter = views.iterator();

		mHandler = new Handler();
		mediaRecorder = new MediaRecorder();
		
		
		SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = myVideoView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		camera = Camera.open(1);
		camera.setDisplayOrientation(90);
		camera.unlock();
		try {
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();
		initMediaRecorder();

		restart = (Button) findViewById(R.id.record);
		restart.setOnClickListener(restartListener);
		send = (Button) findViewById(R.id.send);
		send.setOnClickListener(sendListener);
		recordTask = new RecordAsyncTask();
		audioInfo = new AudioInfo(AUDIO_SOURCE);
		audio = new AudioAsyncTask(this, AUDIO_SOURCE, audioInfo);
		audio.execute(null, null);
        //recordTask.execute(null,null);            		
		
	}
	
	//run time 1: 1323
	//         2: 1321
	//         3: 1290
	//         4: 1353
	//         5: 1522
	//         6: 1277
	
    private void run8secondRecord() {
    	Long start = System.currentTimeMillis();
		mediaRecorder.start();
		Long end = System.currentTimeMillis();
		Long initTime = end - start;
		Log.d(tag, "VideoCamera init time: "+ initTime + " ms");
    }
    
    
    private Button.OnClickListener restartListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			audio.cancel(true);
			if (recording) {
	            mediaRecorder.reset();
	            mediaRecorder.release();
			}
			recreate();
			
		}
    	
    };
	private Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (recording) {
				
				mediaRecorder.stop();
				audioInfo.setRecordEnd(System.currentTimeMillis());
				mediaRecorder.reset();
				mediaRecorder.release();
				
				audio.cancel(true);
				Log.d(tag, "stop pressed, uri: " + OUTPUT);
				Log.d(tag, "audio info duration: " + String.valueOf(audioInfo.getRecordDuration()) + " ms");
				doAction();
				Intent i = setUpAudioInfoIntent(audioInfo);
				startActivity(i);
				//finish();
			} else {
				mediaRecorder.start();
				audioInfo.setRecordStart(System.currentTimeMillis());
				recording = true;
				Log.d(tag, "rec pressed");

				restart.setText("STOP");
			}
		}
	};
	
	private Button.OnClickListener sendListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (recording) {
				return;
			}
			Intent i = new Intent(context, UploadService2.class);
			i.putExtra("path", OUTPUT);
			context.startService(i);
			//go to some home screen
		}
	};
	
	private Intent setUpAudioInfoIntent(AudioInfo audioInfo) {
		Intent intent = new Intent(this, VideoPlayback.class);
		audioInfo.setUpIntent(intent);
		intent.putExtra("OUTPUT", OUTPUT);
		return intent;
	}
	
	
	private void doAction() {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(OUTPUT);
		String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		long timeInmillisec = Long.parseLong( time );
		long duration = timeInmillisec;
		Log.d(tag, "Duration of clip: "+ duration + " ms");
	}
	
	
	private void setResult() {
		Intent data = new Intent();
		data.putExtra("file", OUTPUT);
		setResult(RESULT_OK, data);
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		prepareMediaRecorder();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

	}

	private void initMediaRecorder() {
//		camera = Camera.open(1);
//		camera.setDisplayOrientation(90);
//		camera.unlock();
//		try {
//			camera.setPreviewDisplay(surfaceHolder);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		mediaRecorder.setCamera(camera);
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		CamcorderProfile camcorderProfile_LQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW);
		mediaRecorder.setProfile(camcorderProfile_LQ);
		mediaRecorder.setOutputFile(OUTPUT);
		mediaRecorder.setMaxDuration(8000);
		mediaRecorder.setOnInfoListener(new OnInfoListener() {
		    @Override
		    public void onInfo(MediaRecorder mr, int what, int extra) {                     
		        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
		            mediaRecorder.stop();
		            mediaRecorder.reset();
		            mediaRecorder.release();
		            try {
						camera.reconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
		            camera.stopPreview();
		            camera.release();
		            audio.cancel(true);
		        }          
		    }
		});
	}

	private void prepareMediaRecorder() {
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class AudioAsyncTask extends AsyncTask<Void, Void, Void> {
		private String TAG = "Audio";
		Context context;
		String pathname;
		MediaPlayer player;
		Long startTime;
		Long millisInStart;
		Long millisInEnd;
		AudioInfo audioInfo;
		
		public AudioAsyncTask (Context context, String pathname, AudioInfo audioInfo) {
			this.context = context;
			this.pathname = pathname;
			this.audioInfo = audioInfo;
		}
		
	    @Override
	    protected Void doInBackground(Void... params) {
	    	Uri uri = Uri.parse(pathname);
	    	Log.d(TAG, "here in Audio");
	    	Log.d(TAG, "path: " + pathname);
	       // player = MediaPlayer.create(context, R.raw.loop1); 
	    	player = MediaPlayer.create(context,uri); 
	        player.setLooping(true); // Set looping 
	        player.setVolume(100,100); 
	        //audioInfo.setSongStart(System.currentTimeMillis());
	        player.start(); 
	        publishProgress();
	        Log.d(TAG, "Chillin in doInBackground()");
	        while (!isCancelled() ){
	        	
	        }
	        player.stop();
	        player.reset();
	        player.release();
	        
	        return null;
	    }
	    
	    @Override
	    protected void onProgressUpdate(Void... item) {
	    	startCountdown();
	    }          
	}
	public class RecordAsyncTask extends AsyncTask<Void, Void, Void> {
		//Handler rHandler = new Handler();
		
		@Override
		protected Void doInBackground(Void... params) {
			mHandler.postDelayed(mRecord, 7000);
			return null;
		}
		
	}
	
	private void startCountdown() {
		//mHandler.postDelayed(mUpdateText, 5800); 
		mHandler.postDelayed(mRecord, 6600);
	}
	
	private Runnable mUpdateText = new Runnable() {
        public void run() {
        	
           iter.next().setTextColor(Color.RED);
           if (iter.hasNext()){
        	   mHandler.postDelayed(mUpdateText, 500);
           }
        }
     };
     
     private Runnable mRecord = new Runnable() {
    	 public void run() {
    		   mediaRecorder.start();
    		   
    	 }
     };
}