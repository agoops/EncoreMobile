package com.encore.API;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import util.T;
import Bus.BusProvider;
import Bus.SessionsEvent;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.encore.TokenHelper;
import com.encore.API.models.Crowd;
import com.encore.API.models.PostSession;
import com.encore.API.models.Session;
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
		case T.CREATE_SESSION:
			createSession(intent);
			break;
		case T.FRIENDS:
			getFriends(intent.getExtras());
			break;
		case T.GET_SESSIONS:
			getSessions();
			break;
		default:
			break;
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
			User user = new User(data.getString(T.USERNAME),
					data.getString(T.PASSWORD), data.getString(T.FIRST_NAME),
					data.getString(T.LAST_NAME), data.getString(T.EMAIL),
					data.getString(T.PHONE_NUMBER));

			api.signUp(user);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}

	private void createSession(Intent intent) {
		Log.d(TAG, "createSession() called");
		Bundle data = intent.getExtras();
		String token = TokenHelper.getToken(this);
		Session result = null;
		PostSession pSession = null;
		try {
			if( !data.getBoolean(T.SESSION_USE_EXISTING_CROWD) ) {
				// If user doesn't want to use an existing crowd...
				
				// TODO: Should be from user input, not this arbitrary list
				String members[] = {"anskeet", "babs"};
				
				// Bundle fields into a PostSession object...
				pSession = new PostSession(data.getString(T.SESSION_TITLE), data.getBoolean(T.SESSION_USE_EXISTING_CROWD), 
						data.getString(T.SESSION_CROWD_TITLE), members, 0);
				
				result = api.createSession(pSession, token);
			} else {
				// else they DO want to use an existing crowd...
				String crowdTitle = "";
				String[] members = {};
				
				// TODO: pass existingCrowd in from a lv of existing crowds on the client app,
				// instead of creating this arbitrary group
				pSession = new PostSession(data.getString(T.SESSION_TITLE), data.getBoolean(T.SESSION_USE_EXISTING_CROWD), 
						null, null, Integer.parseInt(data.getString(T.SESSION_CROWD_ID)));
				
				result = api.createSession(pSession, token);
			}
			
			// Error checking
			if (result == null) {
				Log.e(TAG, "No session created");
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}

	private void getSessions() {
		Log.d(TAG, "getSessions called");

		SessionsEvent event = new SessionsEvent();

		try {
			// event.sessions = api.getSessions();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
		List<Session> dummy = new ArrayList<Session>();
		dummy.add(new Session());
		dummy.add(new Session());
		dummy.add(new Session());
		event.sessions = dummy;
		BusProvider.getInstance().post(event);
	}

	private void login(Bundle data) {

	}

	private void newSession(Bundle data) {

	}

	private void addClip(Bundle data) {

	}

}
