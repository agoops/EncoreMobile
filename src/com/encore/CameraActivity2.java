package com.encore;

import java.io.File;

import Fragments.RecordSessionFragment;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

public class CameraActivity2 extends FragmentActivity {

	private Context context = this;
	boolean isRecording;
	boolean oneRecorded;
	int sessionId;
	private static String tag = "CameraActivity2";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeholder);

		Bundle extras = getIntent().getExtras();
		sessionId = -1;
		try {
			sessionId = extras.getInt("sessionId", -1);
			Log.d(tag, "this session id is: " + sessionId);
		} catch (NullPointerException e) {
			Log.d(tag,
					"No passed in sessionId in extras. Have to create new session");
			Log.d(tag, "this session id is: " + sessionId);
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		CameraActivityFragment recordFragment = new CameraActivityFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("sessionId", sessionId);
		recordFragment.setArguments(bundle);

		ft.add(R.id.fragment_placeholder, recordFragment);
		ft.commit();

	}


}
