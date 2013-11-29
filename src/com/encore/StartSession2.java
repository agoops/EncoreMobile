package com.encore;

import util.T;
import Fragments.RecordSessionFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.encore.API.APIService;
import com.encore.API.models.Crowd;

public class StartSession2 extends FragmentActivity {

	private static final String TAG = "StartSession2.java";

	boolean isRecording = false;
	String tag = "StartSession2";
	Thread recordThread = null;
	int sampleRateInHz = 11025;
	String filepath = "/storage/sdcard0/testaudio.pcm";
	private boolean yes_checked = false;
	
	private String jsonResult = "";
	private Crowd[] crowds = null;
	
	ListView crowdsLV;
	
	APIService api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "StartSession2 entered");
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
		recordFragment.setArguments(bundle);
		
		// Add the RecordSessionFragment to and commit the transaction
		ft.add(R.id.fragment_placeholder, recordFragment);
		ft.commit();
	}

//=======
//
//	/**********************************************
//	 * 
//	 * 
//	 * Button Logic 
//	 * 
//	 * 
//	 *******************************************/
//	@Override
//	public void onClick(View v) {
//	// TODO: Move add_clip stuff to the "reply" button
//	// TODO: Replying will launch RecordSessionFragment with info in the bundle specifying whether a crowd is already included or not.
//	// TODO: If not included, the "next" button and all of its crowd-picking logic should appear
//	// TODO: If included, the crowd should be passed in and the "send" button and all of its logic should appear 
//		switch (v.getId()) {
//		
//		case (R.id.get_sessions): {
//			
//			Intent apiIntent = new Intent(this, APIService.class);
//			apiIntent.putExtra(T.API_TYPE, T.GET_SESSIONS);
//			GetSessionsReceiver gsReceiver = new GetSessionsReceiver(new Handler());
//			apiIntent.putExtra("receiver", gsReceiver);
//			startService(apiIntent);
//			break;
//		}
//		
//		case (R.id.add_clip): {
//			Log.d(TAG, "Add Clip clicked");
//			Intent apiIntent = new Intent(this,APIService.class);
//			apiIntent.putExtra(T.API_TYPE, T.ADD_CLIP);
//			apiIntent.putExtra(T.DURATION, 69);
//			apiIntent.putExtra(T.SESSION, 14);
//			apiIntent.putExtra(T.FILEPATH, filepath);
//			startService(apiIntent);
//			break;
//		}
//		default: {
//			break;
//		}
//		}
//	}
//	
//	private class GetSessionsReceiver extends ResultReceiver {
//
//		public GetSessionsReceiver(Handler handler) {
//			super(handler);
//		}
//		
//
//    	@Override
//    	protected void onReceiveResult(int resultCode, Bundle resultData) {
//    		if (resultCode == 1) {
//    			Log.d(TAG, "APIService returned successful");
//    			String json = resultData.getString("result");
//    			Log.d(TAG, json);
//    		}
//    		else {
//    			Log.d(TAG, "resultCode in GetSessionsReceiver is 0. Error");
//    		
//    		}
//    	}
//		
//	}
//	private void setButtonHandlers() {
//		((Button) findViewById(R.id.btnStart))
//				.setOnClickListener((OnClickListener) this);
//		((Button) findViewById(R.id.btnStop))
//				.setOnClickListener((OnClickListener) this);
//		((Button) findViewById(R.id.btnPlayback))
//				.setOnClickListener((OnClickListener) this);
//		((Button) findViewById(R.id.send_session))
//				.setOnClickListener((OnClickListener) this);
//		((Button) findViewById(R.id.get_sessions)).setOnClickListener(this);
//		((Button) findViewById(R.id.add_clip)).setOnClickListener(this);
//	}
}
