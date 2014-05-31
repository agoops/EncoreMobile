package com.encore.Fragments;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.encore.R;
import com.encore.util.T;

import java.io.File;
import java.io.IOException;

public class CameraFragment extends Fragment implements
        SurfaceHolder.Callback {

    private static final String TAG = "CameraFragment";
    private Context context;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private View view;
    private Button record;
    private ImageView fillerView;

    private File mediaFile;
    private boolean isRecording;
    private boolean oneRecorded;
    private int sessionId;
    private boolean isBattle;
    private String battleReceiver;

    private String thumbnailFilepath;
    private int[] beatIds;
    private static int curBeatIndex;
    private static MediaPlayer mp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.capture_video, container, false);
        setHasOptionsMenu(true);
        context = getActivity();


        initData();
        playFirstBeat();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        if(mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }

        if(camera != null) {
            try {
                camera.reconnect();
                camera.stopPreview();
                camera.release();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initData() {
        // Get sessionId. -1 means we need to create a new session
        sessionId = getArguments().getInt(T.SESSION_ID, -1);

        isBattle = getArguments().getBoolean(T.IS_BATTLE, false);
        battleReceiver = getArguments().getString(T.BATTLE_RECEIVER, null);

        // Stores beat ids
        beatIds = new int[]
                {
                        R.raw.simple_beat1, R.raw.simple_beat2,
                        R.raw.simple_beat1, R.raw.simple_beat4,
                        R.raw.simple_beat5
                };
        curBeatIndex = 0;

        fillerView = (ImageView) view.findViewById(R.id.filler_view);

        surfaceView = (SurfaceView) view.findViewById(R.id.videoview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        Log.d(TAG, "surface is holder null: " + (surfaceHolder == null));
        isRecording = false;
        oneRecorded = false;
        camera = setUpCamera();
    }

    private void playFirstBeat() {
        int beatId = beatIds[curBeatIndex];
        playBeat(beatId);
        incrementBeatIndex();
    }

    private void playBeat(int beatId) {
        if(mp != null) {
            mp.stop();
        }
        mp = MediaPlayer.create(context, beatId);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setLooping(true);
        mp.start();
    }

    private void incrementBeatIndex() {
        curBeatIndex++;
        if(curBeatIndex > 4) {
            curBeatIndex = 0;
        }
    }

    private Camera setUpCamera() {
        // add more checks here to open camera safely...
        Camera c = Camera.open(1);
        c.setDisplayOrientation(90);
        return c;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items in the action bar
        inflater.inflate(R.menu.camera_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "Action Item selected");
        // Handle presses on action bar items
        switch(item.getItemId())
        {
            case R.id.action_beats:
                // Launch a new session
                Log.d(TAG, "Changing beat");
                playBeat(beatIds[curBeatIndex]);
                incrementBeatIndex();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final int maxDurationInMs = 900000;
    private final long maxFileSizeInBytes = Long.MAX_VALUE;
    private final int videoFramesPerSecond = 30;

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
        Log.d(TAG, "surfaceDestroyed()");
    }

    private void setUpButtons() {
        // TODO: 15 second visual
        record = (Button) view.findViewById(R.id.record);

        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Record
                    Log.d(TAG, "Start recording");
                    startRecording();
//                    animateRecordingView(fillerView, 0, 0);
                    Button button = (Button) v;
                    button.setBackgroundColor(getResources().getColor(R.color.button_pressed));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop recording, show preview
                    Log.d(TAG, "Stop recording");
                    stopRecording();
                    Button button = (Button) v;
                    button.setBackgroundColor(getResources().getColor(R.color.button_default));
                    goToPreview();
                }
                return true;
            }
        });
    }

    public void animateRecordingView(ImageView v, int total_width, int duration) {
        int startingHeight = v.getMeasuredHeight();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                0,
                startingHeight
        );

        v.setLayoutParams(params);
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

        // Pass along the filepath for it to play
        Bundle args = new Bundle();
        args.putString(T.FILEPATH, mediaFile.getAbsolutePath());
        args.putInt(T.SESSION_ID, this.sessionId);
        args.putBoolean(T.IS_BATTLE, this.isBattle);
        args.putString(T.BATTLE_RECEIVER, battleReceiver);
        Log.d(TAG, "isBattle: " + isBattle);
        Log.d(TAG, "battleReceiver: " + battleReceiver);
        previewFragment.setArguments(args);

        ft.replace(R.id.fragment_placeholder, previewFragment);
        ft.addToBackStack(null);
        ft.commit();
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
        //mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());
        mediaRecorder.setVideoSize(640,480);
        mediaRecorder.setVideoEncodingBitRate(3000000);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

        mediaRecorder.setMaxFileSize(maxFileSizeInBytes);
        mediaRecorder.setOrientationHint(270);
    }

    private File getOutputMediaFile(){
        Log.d(TAG, "getOutputMediaFile called");
        // TODO: Check the SDCard is mounted using Environment.getExternalStorageState()

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
