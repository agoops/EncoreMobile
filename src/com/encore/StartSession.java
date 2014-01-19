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
import com.encore.util.T;

public class StartSession extends FragmentActivity {
    private static final String TAG = "StartSession";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeholder);

        // Flow:
        // Inbox > StartSession:ClipPreview > StartSession:Camera > StartSession:Preview > Send clip, HomeActivity
        // StartSession:Camera > StartSession:Preview > StartSession:CrowdPicker
        int sessionId = getIntent().getIntExtra(T.SESSION_ID, -1);

        // Add UP navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // So the volume keys work in our app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if(sessionId == -1) {
            // New session
            Log.d(TAG, "About to launch camera fragment");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            CameraFragment cameraFragment = new CameraFragment();

            // Open the camera fragment
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putInt("crowdId", intent.getIntExtra("crowdId", -1));
            bundle.putString("sessionTitle", intent.getStringExtra("sessionTitle"));
            bundle.putInt("sessionId", intent.getIntExtra("sessionId", -1));
            cameraFragment.setArguments(bundle);

            ft.add(R.id.fragment_placeholder, cameraFragment);
            ft.commit();
        } else {
            // Reply to new session
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ReplyFragment replyFragment = new ReplyFragment();

            Log.d(TAG, "Clip URL: " + getIntent().getStringExtra(T.CLIP_URL));

            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putInt(T.SESSION_ID, sessionId);
            bundle.putString(T.CLIP_URL, intent.getStringExtra(T.CLIP_URL));
            replyFragment.setArguments(bundle);

            ft.add(R.id.fragment_placeholder, replyFragment);
            ft.commit();
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