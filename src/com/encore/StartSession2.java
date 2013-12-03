package com.encore;

import Fragments.RecordSessionFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class StartSession2 extends FragmentActivity {

	private static final String TAG = "StartSession2.java";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeholder);
		
		Log.d(TAG, "About to launch a RecordSessionFragment");
		// Create a new transaction and RecordSessionFragment
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		RecordSessionFragment recordFragment = new RecordSessionFragment();
		
		// Pass the crowdId to the recordFragment
		// If it doesn't exist, crowdId == -1
		Intent intent = getIntent();
		int crowdId = intent.getIntExtra("crowdId", -1);
		Bundle bundle = new Bundle();
		bundle.putInt("crowdId", crowdId);
		bundle.putString("sessionTitle", intent.getStringExtra("sessionTitle"));
		bundle.putInt("sessionId", intent.getIntExtra("sessionId", -1));
		Log.d(TAG, "crowdId, sessionTitle: " + crowdId + ", " + intent.getStringExtra("sessionTitle"));
		recordFragment.setArguments(bundle);
		
		// Add the RecordSessionFragment to and commit the transaction
		ft.add(R.id.fragment_placeholder, recordFragment);
		ft.commit();
	}
}
