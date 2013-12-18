package com.encore;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraActivity extends Activity {
	private static final String TAG = "CameraActivity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private MediaRecorder mMediaRecorder;
	private Boolean isRecording = false;
	private Button captureButton;
	private static Context mContext;
	private static File mediaFile;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate called");
		setContentView(R.layout.camera_preview);
		mContext = this;
		
		// These next two lines don't do anything
		// I'm just using them to 'clear out' the MR and C
		releaseMediaRecorder();
		releaseCamera();
		
		// Create an instance of the camera
		mCamera = getCameraInstance();
		mCamera.setDisplayOrientation(90);
		
		// Set our custom CameraPreview as the content
		mPreview = new CameraPreview(mContext, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview_frame);
		preview.addView(mPreview);
		
		// Add a listener to the Capture button
		captureButton = (Button) findViewById(R.id.capture_button);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Capture button clicked");
				if(isRecording) {
					// stop recording and release camera
					Log.d(TAG, "Stopping, releasing MediaRecorder now");
					stopMediaRecorder();
					releaseMediaRecorder();
					
					setCaptureButtonText("Capture");
					isRecording = false;
//					mCamera.stopPreview();
//					releaseCamera();
				} else {
					// initialize video camera
					if(prepareVideoRecorder()) {
						// Camera is available and unlocked, MediaRecorder is prepared,
						// start recording!
						Log.d(TAG, "Starting MediaRecorder now");
						startMediaRecorder();
						setCaptureButtonText("Stop");
						isRecording = true;
					} else {
						// Prepare didn't work, release the camera
						releaseMediaRecorder();
					}
				}
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "releasing MediaRecorder");
		releaseMediaRecorder();
		Log.d(TAG, "releasing camera");
		releaseCamera();
	}
	
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(0); // 0 = rear, 1 = front
		} catch(Exception e) {
			// TODO: Fail gracefully... 
			Log.d(TAG, "Camera is in use...");
		}
		// Returns null if the camera is unavailable
		return c; 
	}
	
	private boolean prepareVideoRecorder() {
		Log.d(TAG, "prepareVideoRecorder called");
		mCamera.lock();
		mCamera.unlock();
		mMediaRecorder = new MediaRecorder();
		
		// Step 1. Set camera to MediaRecorder
		mMediaRecorder.setCamera(mCamera);
		
		// Step 2. Set the audio/video sources
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		
		// Step 3. Set a Camcorder profile
		mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
		
		// Step 4. Set the output file
		mMediaRecorder.setOutputFile(getOutputMediaFile().toString());
		
		// Step 5. Set the preview output
		mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
		
		// Step 6. Prepare configured MediaRecorder
		try {
			mMediaRecorder.prepare();
		} catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
		
		Log.d(TAG, "prepareVideoRecorder successful");
		return true;
	}
	
	private void startMediaRecorder() {
		try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            releaseMediaRecorder();
        } catch(Exception e){
            e.printStackTrace();
            releaseMediaRecorder();
        }
	}
	
	private void stopMediaRecorder() {
		try {
			mMediaRecorder.stop();
			Log.d(TAG, "MediaRecorder stopped");
		} catch(RuntimeException e) {
			mediaFile.delete();
			Log.d(TAG, "sucessfully deleted file");
		}
	}
	
	/** Create a File for saving an image or video */
	// TODO: Look over and tweak this
	private static File getOutputMediaFile(){
		Log.d(TAG, "getOutputMediaFile called");
	    // TODO: To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		
		// Android's default directory for pictures and videos
	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "RapchatVideos");
	    
	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists()){
	    	Log.d(TAG, "The following directory doesn't exist: " + mediaStorageDir);
	        if (!mediaStorageDir.mkdirs()) {
	            Log.d(TAG, "failed to create directory");
	            return null;
	        }
	    }
	    
	    String path = mediaStorageDir.getPath() + File.separator +
	    		"VID_TempRapchat.mp4";
	    mediaFile = new File(path);
	    Log.d(TAG, "File path: " + path);
	    try {
	    	// Delete any previous recording
	    	if(mediaFile.exists()) {
	    		mediaFile.delete();
	    		Log.d(TAG, "Previously existing file deleted.");
	    	}
			mediaFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "Failed to create new file");
			e.printStackTrace();
		}
	    
	    Log.d(TAG, "getOutputMediaFile successful");
	    return mediaFile;
	}
	
	private void releaseMediaRecorder() {
		if(mMediaRecorder != null) {
			mMediaRecorder.reset(); 	// Clear recorder configuration
			mMediaRecorder.release(); 	// Release the recorder object
			mMediaRecorder = null;
			mCamera.lock();
		}
	}
	
	private void releaseCamera() {
		if(mCamera != null) {
			mCamera.release();	// Release camera for other applications
			mCamera = null;
		}
	}
	
	private void setCaptureButtonText(String text) {
		captureButton.setText(text);
	}
}