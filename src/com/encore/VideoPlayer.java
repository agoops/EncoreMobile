package com.encore;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer {
	private final static String TAG = "VideoPlayer";
	VideoView videoView;
	Context context;
    int curClip;

	public VideoPlayer(VideoView view, Context cntext) {
		videoView = view;
		context = cntext;
	}

	public void playVideo(Uri uri) {
        Log.d(TAG, "playVideo()");
        videoView.setVideoURI(uri);
        MediaController mc = new MediaController(context);
        videoView.setMediaController(mc);
        videoView.requestFocus();
        videoView.start();
   	}

    public void setCurClip(int clipNumber) {
        curClip = clipNumber;
    }

    public int getCurClip() {
        return curClip;
    }
}