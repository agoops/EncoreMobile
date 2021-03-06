package com.encore;

import java.io.IOException;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        Log.d(TAG, "constructor");
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
    	Log.d(TAG, "surfaceCreated");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "ApiError setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    	// empty. Take care of releasing the Camera preview in your activity.
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	// Surface changed, so we reset the preview
    	Log.d(TAG, "surfaceChanged");
    	
        if (mHolder.getSurface() == null) {
          return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
//        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH); 
//		Camera.Parameters parameters = mCamera.getParameters();
//		parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
		

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "ApiError starting camera preview: " + e.getMessage());
        }
    }
}