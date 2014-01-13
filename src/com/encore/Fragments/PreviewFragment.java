package com.encore.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preview_fragment, container, false);
        mContext = getActivity();

        Bundle args = getArguments();
        sessionId = args.getInt(T.SESSION_ID);
        uriString = args.getString(T.FILEPATH);

        videoView = (VideoView) view.findViewById(R.id.vidView);
        sendButton = (Button) view.findViewById(R.id.sendBtn);

        //Just here for debugging
        File file = new File(uriString);
        boolean ready = file.canRead();
        Log.d(TAG, "File is ready: " + ready);

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


        return view;
    }


    private void generateThumbnail() {
        //TODO: This thumbnailFilepath shouldn't be generated here. Maybe in oncreate or something
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "RapchatVideos");
        thumbnailFilepath = mediaStorageDir.getPath() + File.separator
                + "VID_TempRapchat_Thumbnail.jpg";
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(thumbnailFilepath);
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(uriString, MediaStore.Video.Thumbnails.MINI_KIND);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // 100 is highest quality
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
                    // Update the image
//                    ImageView sendButton = (ImageView) v;
//                    sendButton.setBackgroundResource(R.drawable.ic_action_send_now_pressed);

                    // Start new session flow
                    Log.d(TAG, "Creating new session");
                    generateThumbnail();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    CreateSessionFragment createSessionFragment = new CreateSessionFragment();

                    // Pass the filepath to the session
                    Bundle args = new Bundle();
                    args.putString(T.FILEPATH, uriString);
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
                    addClipIntent.putExtra(T.FILEPATH, uriString);
                    addClipIntent.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);
                    getActivity().startService(addClipIntent);
                    getActivity().finish();
                }

                break;
        }
    }
}
