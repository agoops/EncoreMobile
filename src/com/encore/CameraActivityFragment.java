package com.encore;

import java.io.File;
import java.io.IOException;

import util.T;

import com.encore.API.APIService;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class CameraActivityFragment extends Fragment implements
SurfaceHolder.Callback, OnClickListener{
	private View v;
	private MediaRecorder mediaRecorder;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private Button record;
	private Button send;

	private final int maxDurationInMs = 900000;
	private final long maxFileSizeInBytes = 500000;
	private final int videoFramesPerSecond = 20;
	
	private File mediaFile;
	boolean isRecording;
	boolean oneRecorded;
	int sessionId;
	private static String tag = "CameraActivity2";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(tag, "CameraActivityFragment launched");
		v = inflater.inflate(R.layout.capture_video, container, false);
		//Get sessionId. -1 means need to create a new session
		Bundle extras = getArguments();
		sessionId = -1;
		try{
			sessionId = extras.getInt("sessionId",-1);
			Log.d(tag, "this session id is: " + sessionId);
		} catch( NullPointerException e) {
			Log.d(tag, "No passed in sessionId in extras. Have to create new session");
			Log.d(tag, "this session id is: " + sessionId);
		}
		
		

		surfaceView = (SurfaceView) v.findViewById(R.id.videoview);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		Log.d(tag, "surface is holder null: " + (surfaceHolder == null));
		isRecording = false;
		oneRecorded = false;
		camera = setUpCamera();

		
		return v;                 
	}
	
	private Camera setUpCamera() {
		// add more checks here to open camera safely...
		Camera c = Camera.open(1);
		c.setDisplayOrientation(90);
		return c;
	}
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(tag, "onPause()");
		try {
			camera.reconnect();
			camera.stopPreview();
			camera.release();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		camera.stopPreview();
		Log.d(tag, "surfaceChanged()");
		try {
			Log.d(tag, "surfaceHolder null in surfaceCreated: "
					+ (surfaceHolder == null));
			Log.d(tag, "is camera null = " + (camera == null));
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
		Log.d(tag, "surfaceCreated()");

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
	

	private void setUpButtons() {
		record = (Button) v.findViewById(R.id.record);
		send = (Button) v.findViewById(R.id.send);

		record.setOnClickListener(this);
		send.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Log.d(tag, "Something clicked");
		switch (v.getId()) {
		case R.id.record:
			Log.d(tag, "record clicked");
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

		case R.id.send:
			Log.d(tag, "send clicked");
			if (isRecording || !oneRecorded || !mediaFile.exists()) {
				return;
			}
			if (sessionId == -1) {
				//take to screen to collect new information
				Log.d(tag, "create new session with recorded clip...not implemented yet");
				
			}
			else {
				//add recorded clip to current session
				Intent addClipIntent = new Intent(getActivity(), APIService.class);
				addClipIntent.putExtra(T.API_TYPE, T.ADD_CLIP);
				addClipIntent.putExtra(T.SESSION_ID, sessionId);
				addClipIntent.putExtra(T.FILEPATH, mediaFile.getAbsolutePath());
				getActivity().startService(addClipIntent);
				getActivity().finish();
				
			}
		
			break;
		}
	}
	
	private void setUpMediaRecorder() {
		Log.d(tag, "setUpMediaRecorder()");
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
		Log.d(tag, "getOutputMediaFile called");
	    // TODO: To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
		// Android's default directory for pictures and videos
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "RapchatVideos");
	    
	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists()){
	    	Log.d(tag, "The following directory doesn't exist: " + mediaStorageDir);
	        if (!mediaStorageDir.mkdirs()) {
	            Log.d(tag, "failed to create directory");
	            return null;
	        }
	    }
	    
	    String path = mediaStorageDir.getPath() + File.separator +
	    		"VID_TempRapchat.mp4";
	    File mediaFile = new File(path);
	    Log.d(tag, "File path: " + path);
	    try {
	    	// Delete any previous recording
	    	if(mediaFile.exists()) {
	    		mediaFile.delete();
	    		Log.d(tag, "Previously existing file deleted.");
	    	}
			mediaFile.createNewFile();
		} catch (IOException e) {
			Log.d(tag, "Failed to create new file");
			e.printStackTrace();
		}
	    
	    Log.d(tag, "getOutputMediaFile successful");
	    this.mediaFile = mediaFile;
	    return mediaFile;
	}

	
}
