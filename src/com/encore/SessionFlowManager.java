package com.encore;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import com.encore.Fragments.CameraFragment;
import com.encore.Fragments.ReplyFragment;
import com.encore.models.Clip;
import com.encore.util.T;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionFlowManager extends FragmentActivity {
    private static final String TAG = "SessionFlowManager";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeholder);

        // Flow:
        // Inbox > SessionFlowManager:ClipPreview > SessionFlowManager:Camera > SessionFlowManager:Preview > Send clip, HomeActivity
        // SessionFlowManager:Camera > SessionFlowManager:Preview > SessionFlowManager:CrowdPicker
        int sessionId = getIntent().getIntExtra(T.SESSION_ID, -1);
        String battleReceiver = getIntent().getStringExtra(T.BATTLE_RECEIVER);
        boolean isBattle = getIntent().getBooleanExtra(T.IS_BATTLE, false);
        boolean isComplete = getIntent().getBooleanExtra(T.IS_COMPLETE, false);

        // Add UP navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // So the volume keys work in our app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(sessionId == -1) {
            // New session
            Log.d(TAG, "About to launch camera fragment");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            CameraFragment cameraFragment = new CameraFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(T.SESSION_ID, -1);
            bundle.putBoolean(T.IS_BATTLE, isBattle);
            bundle.putString(T.BATTLE_RECEIVER, battleReceiver);
            Log.d(TAG, "isBattle: " + isBattle);
            Log.d(TAG, "battleReceiver: " + battleReceiver);
            cameraFragment.setArguments(bundle);

            // Open the camera fragment
            ft.add(R.id.fragment_placeholder, cameraFragment);
            ft.commit();
        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ReplyFragment replyFragment = new ReplyFragment();

            if(isComplete) {
                // Complete session - read only, can't rapback
                Intent intent = getIntent();

                // Get clip urls
                String clipsJson = intent.getStringExtra(T.ALL_CLIPS);
                Type listType = new TypeToken<ArrayList<Clip>>() {}.getType();
                List<Clip> clips = (new Gson()).fromJson(clipsJson, listType);

                ArrayList<String> clipURLs = new ArrayList<String>();
                for (Clip c : clips) {
                    clipURLs.add(c.getUrl());
                }

                Bundle bundle = new Bundle();
                bundle.putInt(T.SESSION_ID, sessionId);
                bundle.putStringArrayList(T.CLIP_URL, clipURLs);
                bundle.putBoolean(T.IS_COMPLETE, isComplete);
                replyFragment.setArguments(bundle);

                ft.add(R.id.fragment_placeholder, replyFragment);
                ft.commit();
            } else {
                // Live session - reply to the new session
                Log.d(TAG, "Clip URL: " + getIntent().getStringExtra(T.CLIP_URL));

                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putInt(T.SESSION_ID, sessionId);
                bundle.putString(T.CLIP_URL, intent.getStringExtra(T.CLIP_URL));
                bundle.putBoolean(T.IS_COMPLETE, isComplete);
                replyFragment.setArguments(bundle);

                ft.add(R.id.fragment_placeholder, replyFragment);
                ft.commit();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}