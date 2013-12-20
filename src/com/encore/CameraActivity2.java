package com.encore;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity2 extends Activity implements SurfaceHolder.Callback {

	private Context context = this;
	private MediaRecorder mediaRecorder;
	private SurfaceHolder surfaceHolder;
	private Camera camera;

	private String OUTPUT = "/sdcard/myvideo.mp4";
	boolean recording;
	private String tag = "CameraActivity2";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.capture_video);

		SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = myVideoView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		Log.d(tag, "surface is holder null: " + (surfaceHolder == null));
		camera = setUpCamera();
		// try {
		// camera.setPreviewDisplay(surfaceHolder);
		// camera.startPreview();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
//		mediaRecorder = setUpMediaRecorder();

	}

	@Override
	protected void onPause() {
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

	private Camera setUpCamera() {
		Camera c = Camera.open();
		c.setDisplayOrientation(90);
		return c;
	}

	private Camera breakdownCamera() {

		return null;
	}

	private MediaRecorder setUpMediaRecorder() {
		Log.d(tag, "setUpMediaRecorder()");
		MediaRecorder mRecorder = new MediaRecorder();
		camera.unlock();
		mRecorder.setCamera(camera);
		mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		CamcorderProfile camcorderProfile_LQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW);
		mRecorder.setProfile(camcorderProfile_LQ);
		mRecorder.setOutputFile(OUTPUT);
		mRecorder.setOrientationHint(90);
		return mRecorder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		camera.stopPreview();
		Log.d(tag, "surfaceChanged()");
		try {
			// setPreviewDisplay needs to be called after surfaceCreated returns
			Log.d(tag, "surfaceHolder null in surfaceCreated: "
					+ (surfaceHolder == null));
			Log.d(tag, "is camera null = " + (camera==null));
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			// mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// mediaRecorder.prepare();
			// mediaRecorder.start();

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(tag, "surfaceCreated()");

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
