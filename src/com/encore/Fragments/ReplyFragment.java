package com.encore.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.encore.R;
import com.encore.VideoPlayer;
import com.encore.util.T;

/**
 * Created by babakpourkazemi on 1/14/14.
 */
public class ReplyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ReplyFragment";
    View view;
    Context context;
    private int sessionId;
    private Uri clipUri;
    private VideoView clipVideoView;
    private Button rapbackButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reply_preview, container, false);
        setHasOptionsMenu(true);
        context = getActivity();

        initData();
        getViews();
        setOnClickListeners();
        playVideo();

        return view;
    }

    private void initData() {
        // Get arguments
        sessionId = getArguments().getInt(T.SESSION_ID);
        String clipUriString = getArguments().getString(T.CLIP_URL);
        clipUri = Uri.parse(clipUriString);
    }

    private void getViews() {
        clipVideoView = (VideoView) view.findViewById(R.id.reply_clip_preview);
        rapbackButton = (Button) view.findViewById(R.id.reply_rapback_button);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_clip_playback);
    }

    private void setOnClickListeners() {
        rapbackButton.setOnClickListener(this);
    }

    private void playVideo() {
        VideoPlayer vp = new VideoPlayer(clipVideoView, context);
        vp.playVideo(clipUri);

        // Loop video
        clipVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                mp.setLooping(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.reply_rapback_button:
                // Launch camera fragment to rapback
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment cameraFragment = new CameraFragment();

                Bundle args = new Bundle();
                args.putInt(T.SESSION_ID, sessionId);
                cameraFragment.setArguments(args);

                ft.replace(R.id.fragment_placeholder, cameraFragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            default:
                break;
        }
    }
}
