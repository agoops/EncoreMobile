package com.encore;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity2 extends Activity implements
		SurfaceHolder.Callback, OnClickListener {
	
	private static final String TAG = "CameraActivity2";
	private Context context = this;
	private MediaRecorder mediaRecorder;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private ImageView record;
	private ImageView send;

	private File mediaFile;
	boolean isRecording;
	boolean oneRecorded;
	int sessionId;
	private String thumbnailFilepath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get sessionId. -1 means need to create a new session
		Bundle extras = getIntent().getExtras();
		sessionId = -1;
		try{
			sessionId = extras.getInt("sessionId",-1);
			Log.d(TAG, "this session id is: " + sessionId);
		} catch( NullPointerException e) {
			Log.d(TAG, "No passed in sessionId in extras. Have to create new session");
			Log.d(TAG, "this session id is: " + sessionId);
		}


		setContentView(R.layout.capture_video);

		surfaceView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		Log.d(TAG, "surface is holder null: " + (surfaceHolder == null));
		isRecording = false;
		oneRecorded = false;
		camera = setUpCamera();
		// try {
		// camera.setPreviewDisplay(surfaceHolder);
		// camera.startPreview();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// mediaRecorder = setUpMediaRecorder();

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause()");
		try {
			camera.reconnect();
			camera.stopPreview();
			camera.release();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Camera setUpCamera() {
		// add more checks here to open camera safely...
		Camera c = Camera.open(1);
		c.setDisplayOrientation(90);
		return c;
	}

	private final int maxDurationInMs = 900000;
	private final long maxFileSizeInBytes = 500000;
	private final int videoFramesPerSecond = 20;
	
	

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		camera.stopPreview();
		Log.d(TAG, "surfaceChanged()");
		try {
			Log.d(TAG, "surfaceHolder null in surfaceCreated: "
					+ (surfaceHolder == null));
			Log.d(TAG, "is camera null = " + (camera == null));
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
			
			setUpButtons();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated()");

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	private void setUpButtons() {
		record = (ImageView) findViewById(R.id.record);
//		send = (ImageView) findViewById(R.id.send);

		record.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "Something clicked");
		switch (v.getId()) {
		case R.id.record:
			Log.d(TAG, "record clicked");
			if (isRecording) {
				mediaRecorder.stop();
				isRecording = false;
				oneRecorded = true;
				Button button = (Button) v;
				button.setText("Record");
				button.setTextColor(Color.WHITE);
			} else {
				try {
					setUpMediaRecorder();
					mediaRecorder.prepare();
					mediaRecorder.start();
					isRecording = true;
					Button button = (Button) v;
					button.setText("Stop");
					button.setTextColor(Color.RED);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;

//		case R.id.send:
//            Log.d(TAG, "send clicked");
//            if (isRecording || !oneRecorded || !mediaFile.exists()) {
//                return;
//            }
//            if (sessionId == -1) {
//                //take to screen to collect new information
//                Log.d(TAG, "create new session with recorded clip...not implemented yet");
//            }
//            else {
//                //add recorded clip to current session
//                generateThumbnail();
//
//                Intent addClipIntent = new Intent(this, APIService.class);
//                addClipIntent.putExtra(T.API_TYPE, T.ADD_CLIP);
//                addClipIntent.putExtra(T.SESSION_ID, sessionId);
//                addClipIntent.putExtra(T.FILEPATH, mediaFile.getAbsolutePath());
//                addClipIntent.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);
//                startService(addClipIntent);
//                finish();
//            }
//
//            break;
		}
	}
	
	// Creates a thumbnail from a user's recording
	private void generateThumbnail() {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(thumbnailFilepath);
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mediaFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // 100 is highest quality
			Log.d(TAG, "jpeg generated!!!");
		} catch (FileNotFoundException e) {
			Log.d(TAG, "ApiError creating FileOutputStream.");
			e.printStackTrace();
		}
	}
	
	private void setUpMediaRecorder() {
		Log.d(TAG, "setUpMediaRecorder()");
		 mediaRecorder = new MediaRecorder();
		camera.stopPreview();
		 camera.unlock();
		mediaRecorder.setCamera(camera);
		
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setMaxDuration(maxDurationInMs);
		mediaRecorder.setOutputFile(getOutputMediaFile().getAbsolutePath());
		mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
		mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());

		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

		mediaRecorder.setMaxFileSize(maxFileSizeInBytes);
		mediaRecorder.setOrientationHint(270);

	

	}
	
	private  File getOutputMediaFile(){
		Log.d(TAG, "getOutputMediaFile called");
	    // TODO: To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
		// Android's default directory for pictures and videos
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "Rapback");
	    
	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists()){
	    	Log.d(TAG, "The following directory doesn't exist: " + mediaStorageDir);
	        if (!mediaStorageDir.mkdirs()) {
	            Log.d(TAG, "failed to create directory");
	            return null;
	        }
	    }
	    
	    String path = mediaStorageDir.getPath() + File.separator +
	    		"VID_TempRapback.mp4";
	    thumbnailFilepath = mediaStorageDir.getPath() + File.separator
	    		+ "VID_TempRapback_Thumbnail.jpg";
	    
	    File mediaFile = new File(path);
	    Log.d(TAG, "File path: " + path);
	    try {
	    	// Delete any previous recording
	    	if(mediaFile.exists()) {
	    		mediaFile.delete();
	    		Log.d(TAG, "Previously existing file deleted.");
	    	}
			mediaFile.createNewFile();
		} catch (IOException e) {
			Log.d(TAG, "Failed to create new file");
			e.printStackTrace();
		}
	    
	    Log.d(TAG, "getOutputMediaFile successful");
	    this.mediaFile = mediaFile;
	    return mediaFile;
	}

}
