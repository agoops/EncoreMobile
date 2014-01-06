package com.encore.Fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.util.T;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment implements
        SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = "CameraFragment";
    private Context context;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private ImageView record, send;
    private View view;

    private File mediaFile;
    boolean isRecording;
    boolean oneRecorded;
    int sessionId;
    private String thumbnailFilepath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.capture_video, container, false);
        context = getActivity();

        // Get sessionId. -1 means we need to create a new session
        sessionId = getArguments().getInt("sessionId");

        surfaceView = (SurfaceView) view.findViewById(R.id.videoview);
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

        return view;
    }

    @Override
    public void onPause() {
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
    private final long maxFileSizeInBytes = Long.MAX_VALUE;
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
        record = (ImageView) view.findViewById(R.id.record);
        send = (ImageView) view.findViewById(R.id.send);

//        record.setOnClickListener(this);
        send.setOnClickListener(this);

        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Record
                    Log.d(TAG, "Start recording");
                    startRecording();
                    ImageView button = (ImageView) v;
                    button.setBackgroundResource(R.drawable.button_recording_pressed);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop recording, show preview
                    Log.d(TAG, "Stop recording");
                    stopRecording();
                    ImageView button = (ImageView) v;
                    button.setBackgroundResource(R.drawable.button_recording_default);
                    goToPreview();
                }
                return true;
            }
        });
    }

    public void startRecording() {
        try {
            setUpMediaRecorder();
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            isRecording = false;
            oneRecorded = true;
        }
    }

    public void goToPreview() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        PreviewFragment previewFragment = new PreviewFragment();
//        TestCameraFragment previewFragment = new TestCameraFragment();

        // Pass along the filepath for it to play
        Bundle args = new Bundle();
        args.putString(T.FILEPATH, mediaFile.getAbsolutePath());
        args.putInt(T.SESSION_ID, this.sessionId);
        previewFragment.setArguments(args);

        ft.replace(R.id.fragment_placeholder, previewFragment);
        ft.addToBackStack(null);
        ft.commit();
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
                    ImageView button = (ImageView) v;
                    button.setBackgroundResource(R.drawable.button_recording_default);

                    send.setVisibility(View.VISIBLE);
                    // Show the send button, and animate its appearance
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(context,
                            R.animator.rotate_and_fadein);
                    Log.d(TAG, "send.toString(): " + send.toString() + ", set.toString(): " + set.toString());
                    set.setTarget(send);
                    set.start();
                } else {
                    try {
                        setUpMediaRecorder();
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        isRecording = true;
                        ImageView button = (ImageView) v;
                        button.setBackgroundResource(R.drawable.button_recording_pressed);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.send:
                Log.d(TAG, "send clicked");
                if (isRecording || !oneRecorded || !mediaFile.exists()) {
                    return;
                }
                if (sessionId == -1) {
                    // Update the image
                    ImageView sendButton = (ImageView) v;
                    sendButton.setBackgroundResource(R.drawable.ic_action_send_now_pressed);

                    // Start new session flow
                    Log.d(TAG, "Creating new session");
                    generateThumbnail();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    CreateSessionFragment createSessionFragment = new CreateSessionFragment();

                    // Pass the filepath to the session
                    Bundle args = new Bundle();
                    args.putString(T.FILEPATH, mediaFile.getAbsolutePath());
                    args.putString(T.THUMBNAIL_FILEPATH, thumbnailFilepath);
                    createSessionFragment.setArguments(args);

                    ft.replace(R.id.fragment_placeholder, createSessionFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                else {
                    //add recorded clip to current session
                    generateThumbnail();

                    Intent addClipIntent = new Intent(getActivity(), APIService.class);
                    addClipIntent.putExtra(T.API_TYPE, T.ADD_CLIP);
                    addClipIntent.putExtra(T.SESSION_ID, sessionId);
                    addClipIntent.putExtra(T.FILEPATH, mediaFile.getAbsolutePath());
                    addClipIntent.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);
                    getActivity().startService(addClipIntent);
                    getActivity().finish();
                }

                break;
        }
    }

    // Creates a thumbnail from a user's recording
    private void generateThumbnail() {
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(thumbnailFilepath);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mediaFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // 100 is highest quality
            Log.d(TAG, "jpeg thumbnail successfully generated!");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error creating FileOutputStream.");
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

    /** Create a file Uri for saving a video */
    private Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    private  File getOutputMediaFile(){
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
        thumbnailFilepath = mediaStorageDir.getPath() + File.separator
                + "VID_TempRapchat_Thumbnail.jpg";

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
