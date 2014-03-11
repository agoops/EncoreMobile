package com.encore.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.util.T;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by babakpourkazemi on 1/1/14.
 */
public class PreviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PreviewFragment";

    private Context mContext;
    private int sessionId;
    private String uriString;

    private String thumbnailFilepath;

    private VideoView videoView;
    private Button sendButton;
    private EditText sessionTitle;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.preview_fragment, container, false);
        mContext = getActivity();

        getViews();
        initData();

        return view;
    }

    private void getViews() {
        videoView = (VideoView) view.findViewById(R.id.vidView);
        sendButton = (Button) view.findViewById(R.id.sendBtn);
        sessionTitle = (EditText) view.findViewById(R.id.preview_session_title);
    }

    private void initData() {
        Bundle args = getArguments();
        sessionId = args.getInt(T.SESSION_ID);
        uriString = args.getString(T.FILEPATH);

        if(sessionId == -1) {
            sessionTitle.setVisibility(View.VISIBLE);
        }

        sendButton.setOnClickListener(this);
        videoView.setVideoPath(uriString);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.requestFocus();
        videoView.start();

        // ULTRA HACK to get focus on edittext
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                sessionTitle.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                sessionTitle.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 200);

        // Hide keyboard on enter key
        sessionTitle.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    InputMethodManager imm =
                            (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sessionTitle.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void generateThumbnail() {
        //TODO: This thumbnail Filepath shouldn't be generated here. Maybe in oncreate or something
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Rapback");
        thumbnailFilepath = mediaStorageDir.getPath() + File.separator
                + "VID_TempRapback_Thumbnail.jpg";
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(thumbnailFilepath);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uriString, MediaStore.Video.Thumbnails.MINI_KIND);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            Log.d(TAG, "jpeg thumbnail successfully generated!");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error creating FileOutputStream.");
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.sendBtn:
                if (sessionId == -1) {
                    // Start new session flow
                    Log.d(TAG, "Creating new session");
                    String enteredTitle = sessionTitle.getText().toString();
                    if(enteredTitle.length() > 0) {
                        generateThumbnail();

                        Intent createNewSession = new Intent(mContext, APIService.class);
                        createNewSession.putExtra(T.API_TYPE, T.CREATE_SESSION);
                        createNewSession.putExtra(T.SESSION_TITLE, enteredTitle);
                        createNewSession.putExtra(T.FILEPATH, uriString);
                        createNewSession.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);
                        getActivity().startService(createNewSession);

                        getActivity().finish();
                    } else {
                        Toast.makeText(mContext, "Enter a title", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                else {
                    //add recorded clip to current session
                    generateThumbnail();

                    Intent addClipIntent = new Intent(getActivity(), APIService.class);
                    addClipIntent.putExtra(T.API_TYPE, T.ADD_CLIP);
                    addClipIntent.putExtra(T.SESSION_ID, sessionId);
                    addClipIntent.putExtra(T.FILEPATH, uriString);
                    addClipIntent.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);
                    getActivity().startService(addClipIntent);
                    getActivity().finish();
                }

                break;
        }
    }
}
