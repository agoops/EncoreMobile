package com.encore.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.encore.R;
import com.encore.VideoPlayer;
import com.encore.util.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/14/14.
 */
public class ReplyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ReplyFragment";
    View view;
    Context context;
    private int sessionId;
    private Uri clipUri;
    private List<Uri> clipUris;
    private VideoView clipVideoView;
    private Button rapbackButton;
    private ProgressBar progressBar;
    private Boolean isComplete;
    private VideoPlayer vp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reply_preview, container, false);
        setHasOptionsMenu(true);
        context = getActivity();

        initData();
        getViews();
        setOnClickListeners();

        if(isComplete) {
            playVideoList(0);
        } else {
            playVideo();
        }

        return view;
    }

    private void initData() {
        clipUris = new ArrayList<Uri>();

        // Get arguments
        isComplete = getArguments().getBoolean(T.IS_COMPLETE);
        sessionId = getArguments().getInt(T.SESSION_ID);

        if(isComplete) {
            List<String> clipURLs = getArguments().getStringArrayList(T.CLIP_URL);
            for (String uri : clipURLs) {
                clipUris.add(Uri.parse(uri));
            }
        } else {
            String clipUriString = getArguments().getString(T.CLIP_URL);
            clipUri = Uri.parse(clipUriString);
        }
    }

    private void getViews() {
        clipVideoView = (VideoView) view.findViewById(R.id.reply_clip_preview);
        rapbackButton = (Button) view.findViewById(R.id.reply_rapback_button);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_clip_playback);

        vp = new VideoPlayer(clipVideoView, context);
        if(isComplete) {
            rapbackButton.setVisibility(View.GONE);
        }
    }

    private void setOnClickListeners() {
        if(!isComplete) {
            rapbackButton.setOnClickListener(this);
        }
    }

    private void playVideo() {
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

    // TODO: Make this faster
    private void playVideoList(int curClipIndex) {
        progressBar.setVisibility(View.GONE);
        vp.playVideo(clipUris.get(curClipIndex));

//        clipVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });

        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                int curClipIndex = ((vp.getCurClip() + 1) % clipUris.size());
                vp.setCurClip(curClipIndex);

                Log.d(TAG, "about to play index: " + curClipIndex);
                playVideoList(curClipIndex);
            }
        };

        clipVideoView.setOnCompletionListener(onCompletionListener);
    }

//    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
//        protected Long doInBackground(URL... urls) {
//            int count = urls.length;
//            for(int i=0; i<count; i++) {
//                Downloader.downloadFile(urls[i]);
//                if(isCancelled()) break;
//            }
//
//            return 1l;
//        }
//
//        protected void onPostExecute(Long result) {
//
//        }
//    }

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
