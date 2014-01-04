package com.encore.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.encore.R;
import com.encore.util.T;

/**
 * Created by babakpourkazemi on 1/1/14.
 */
public class PreviewFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "PreviewFragment";

    private Context mContext;
    private Uri uri;

    private VideoView videoView;
    private MediaController mc;
    private MediaPlayer mp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preview_fragment, container, false);
        mContext = getActivity();

        Bundle args = getArguments();
        uri = args.getParcelable(T.FILEPATH);
        Toast.makeText(getActivity(), uri.getPath().toString(), Toast.LENGTH_LONG).show();

        videoView = (VideoView) view.findViewById(R.id.vidView);
        Log.d(TAG, "Uri path is: " + uri.getPath());
//        mp=new MediaPlayer();
//        try {
//            mp.setDataSource(path);
//            mp.setScreenOnWhilePlaying(true);
//            mp.setDisplay(holder);
//            mp.prepare();
//            mp.start();
//        } catch(IOException e) {
//            Log.e(TAG, "MediaPlayer error");
//            e.printStackTrace()
//        }

        videoView.setVideoPath(uri.getPath());
        videoView.requestFocus();
        videoView.start();

//        mc = new MediaController(mContext);
//        mc.setMediaPlayer(videoView);
//        videoView.setMediaController(mc);
//        videoView.requestFocus();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
        }
    }
}
