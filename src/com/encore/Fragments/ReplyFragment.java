package com.encore.Fragments;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
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
    private int curClipIndex;
    private boolean isPlaying;
    private Uri clipUri;
    private List<Uri> clipUris;
    private VideoView clipVideoView;
    private Button rapbackButton;
    private ProgressBar progressBar;
    private Boolean isComplete;
    private VideoPlayer vp;
    private List<String> downloadedClipUris;
    private BroadcastReceiver receiver;

    private static final String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS) + File.separator + "Rapback" + File.separator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reply_preview, container, false);
        setHasOptionsMenu(true);
        context = getActivity();

        initData();
        getViews();
        setOnClickListeners();

        if(isComplete) {
            setupBroadcastReceiver();
            downloadVideos(clipUris);
        } else {
            playSingleClip();
        }

        return view;
    }

    private void setupBroadcastReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);

                    DownloadManager mgr = (DownloadManager)
                            context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Cursor c = mgr.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {

                            String uriString = c
                                    .getString(c
                                            .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                            Log.d(TAG, "| uriString: " + uriString);
                            String filePath = uriString.split(".mp4")[0];
                            int clipNum = Integer.parseInt(filePath.substring(filePath.length() - 1, filePath.length()));
                            Log.d(TAG, "| clipNum: " + clipNum);

                            downloadedClipUris.set(clipNum, uriString);
                            Log.d(TAG, "| downloadedClipUris: " + downloadedClipUris.toString());
                            playMultipleClips();
                        }
                    }
                    c.close();
                }
            }
        };

        getActivity().registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void initData() {
        clipUris = new ArrayList<Uri>();

        // Get arguments
        isComplete = getArguments().getBoolean(T.IS_COMPLETE);
        sessionId = getArguments().getInt(T.SESSION_ID);
        isPlaying = false;

        if(isComplete) {
            curClipIndex = 0;
            List<String> clipURLs = getArguments().getStringArrayList(T.CLIP_URL);
            downloadedClipUris = new ArrayList<String>(clipURLs.size());
            for (String uri : clipURLs) {
                clipUris.add(Uri.parse(uri));
                downloadedClipUris.add(null);
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

    private void playSingleClip() {
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
    private void playMultipleClips() {
        progressBar.setVisibility(View.GONE);

        if(!isPlaying) {
            // Wait until the current clip has been downloaded
            if(downloadedClipUris.get(curClipIndex) == null) {
                return;
            }

            isPlaying = true;

            vp.playVideo(Uri.parse(downloadedClipUris.get(curClipIndex)));

            MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    curClipIndex = ((curClipIndex + 1) % downloadedClipUris.size());

                    Log.d(TAG, "about to play index: " + curClipIndex);
                    isPlaying = false;
                    playMultipleClips();
                }
            };
            clipVideoView.setOnCompletionListener(onCompletionListener);
        }
    }

    private void downloadVideos(List<Uri> urls) {
        // Delete the previous videos
        File f = new File(DOWNLOAD_DIR);
        deleteRecursive(f);

        int count = 0;
        for(Uri url : urls) {
            Log.d(TAG, "| Queuing new download: " + url.toString());
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.toString()));
//            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + File.separator + "Rapback" + File.separator,
                    "Rapback_clip_" + count + ".mp4");

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            count += 1;
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        Log.d(TAG, "| deleting: " + fileOrDirectory.getAbsolutePath());
        fileOrDirectory.delete();
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

    @Override
    public void onStop()
    {
        getActivity().unregisterReceiver(receiver);
        super.onStop();
    }
}
