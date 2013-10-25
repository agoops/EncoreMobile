package com.encore.API;

import util.T;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class APIService extends IntentService {
	public static String TAG = "APIService";
	
	API api;
	
	public APIService() {
		super("APIService");
	}
	
	//TODO:
	// Add all the appropriate endpoint handlers (to API and APIService, and update T accordingly)
	@Override
	protected void onHandleIntent(Intent intent) {
		// Where processing occurs
		int apiType = intent.getIntExtra(T.API_TYPE, -1);
		switch(apiType) {
		case T.SIGN_IN:
			signIn(intent.getExtras());
			break;
		default:
			break;
		}
	}
	
	private void signIn(Bundle data) {
		try {
			api.signIn(data.getString(T.EMAIL), data.getString(T.PASSWORD));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}
}
