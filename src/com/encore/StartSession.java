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

public class StartSession extends FragmentActivity {
    private static final String TAG = "StartSession";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placeholder);

        // Add UP navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // So the volume keys work in our app
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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