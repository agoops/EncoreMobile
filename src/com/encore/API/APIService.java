package com.encore.API;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import util.T;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.encore.TokenHelper;
import com.encore.API.models.User;
import com.squareup.okhttp.OkHttpClient;

public class APIService extends IntentService {
	public static String TAG = "APIService";
	API api;
	ResultReceiver resultReceiver = null;

	public APIService() {
		super("APIService");
		Log.d(TAG, "APIService constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent type " + intent.getIntExtra(T.API_TYPE, -1));
		if (intent.hasExtra("receiver")) {
			Log.d(TAG, "receiver!!!!!!!!");
			resultReceiver = intent.getParcelableExtra("receiver");
		} else {
			Log.d(TAG, "noReceiver??");
		}
		api = new API(new OkHttpClient());
		// Where processing occurs
		int apiType = intent.getIntExtra(T.API_TYPE, -1);
		switch (apiType) {
		case T.SIGN_IN:
			signIn(intent.getExtras());
			break;
		case T.SIGN_UP:
			signUp(intent.getExtras());
			break;
		case T.START_SESSION:
			startSession(intent.getExtras());
			break;
		case T.FRIENDS:
			getFriends(intent.getExtras());
			break;
		case T.FRIEND_REQUEST:
			sendFriendRequest(intent.getExtras());
			break;
		case T.USERS:
			getUsers(intent.getExtras());
			break;
		
		default:
			break;
		}
	}
	
	private void sendFriendRequest(Bundle data) {
		String token = TokenHelper.getToken(this);
		
		String username = data.getString(T.USERNAME);
		JSONObject json = null;
		StringEntity entity = null;
		/**
		 * TODO: 
		 * problem with POST body (maybe not right username) is somewhere here.
		 */
		try{
			json = new JSONObject();
			json.put(T.USERNAME, username);
			Log.d(TAG, "json for entity: " + json.toString());
			entity = new StringEntity(json.toString());
			Log.d(TAG, entity.toString());
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
		
		try {
			String result = api.sendFriendRequest(token, entity);
			Log.d(TAG, "response from sendFriendRequest: " + result);
		} catch (Exception e ){
			e.printStackTrace();
		}
		
	}
	
	
	private void getFriends(Bundle data) {
		Log.d(TAG, "getFriends called");
		String token = TokenHelper.getToken(this);
		Log.d(TAG, "token: " + token);
		if (token == null) {
			Log.d(TAG, "No token in shared prefs");
			
		}
		try {
			String result = api.getFriends(token);
			Log.d(TAG, "result from api: " + result);
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void getUsers(Bundle data) {
		Log.d(TAG, "getUsers called");
		String token = TokenHelper.getToken(this);
		Log.d(TAG, "token: " + token);
		if (token == null) {
			Log.d(TAG, "No token in shared prefs");
		}
		try {
			String result = api.getUsers(token);
			Log.d(TAG, "result from api: " + result);
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void signIn(Bundle data) {
		Log.d(TAG, "signIn called");

		try {
			String username = data.getString(T.USERNAME);
			String password = data.getString(T.PASSWORD);
			JSONObject json = new JSONObject();
			json.put(T.USERNAME, username);
			json.put(T.PASSWORD, password);
			StringEntity entity = new StringEntity(json.toString());

			String result = api.signIn(entity);

			String token = (new JSONObject(result)).getString("token");

			TokenHelper.updateToken(this, token);

			Log.d(TAG, "result from api: " + result);
			resultReceiver.send(1, new Bundle());

		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
			e.printStackTrace();
			resultReceiver.send(0, null);
		}

	}

	private void signUp(Bundle data) {
		Log.d(TAG, "signUp called");
		try {
//			User user = new User(data.getString(T.USERNAME),
//					data.getString(T.PASSWORD), data.getString(T.FIRST_NAME),
//					data.getString(T.LAST_NAME), data.getString(T.EMAIL),
//					data.getString(T.PHONE_NUMBER));
			User user = new User();
			api.signUp(user);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}

	private void startSession(Bundle data) {
		Log.d(TAG, "startSession calledd");
		try {

		} catch (Exception e) {
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
