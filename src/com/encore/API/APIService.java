package com.encore.API;

import util.T;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.encore.API.models.User;
import com.squareup.okhttp.OkHttpClient;

public class APIService extends IntentService {
	public static String TAG = "APIService";
	API api;
	
	public APIService() {
		super("APIService");
		Log.d(TAG, "APIService constructor");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent type " + intent.getIntExtra(T.API_TYPE, -1));
		
		api = new API(new OkHttpClient());
		
		// Where processing occurs
		int apiType = intent.getIntExtra(T.API_TYPE, -1);
		switch(apiType) {
			case T.SIGN_UP:
				signUp(intent.getExtras());
				break;
			case T.GET_SESSION:
				getSession(intent.getExtras());
				break;
			default:
				break;
		}
	}
	
	private void signUp(Bundle data) {
		Log.d(TAG, "signUp called");
		try {
			User user = new User(data.getString(T.USERNAME), data.getString(T.PASSWORD), data.getString(T.FIRST_NAME), 
					data.getString(T.LAST_NAME), data.getString(T.EMAIL), data.getString(T.PHONE_NUMBER));
			
			api.signUp(user);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}
	
	private void getSession(Bundle data) {
		Log.d(TAG, "getSession called");
		try {
			String id = data.getString(T.ID);
			api.getSession(id);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}
	
	private void login(Bundle data) {
		
	}
	
	private void newSession(Bundle data) {
		
	}
	
	private void addClip(Bundle data) {
		
	}
	
}
