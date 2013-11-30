package com.encore.API;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import util.T;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.encore.TokenHelper;
import com.encore.API.models.Crowd;
import com.encore.API.models.User;
import com.encore.API.models.PostCrowd;
import com.google.gson.Gson;
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
		
		if (intent.hasExtra(T.RECEIVER)) {
			Log.d(TAG, "Receiver received");
			resultReceiver = intent.getParcelableExtra("receiver");
		} else {
			Log.d(TAG, "No receiver received");
		}
		
		api = new API(new OkHttpClient(), this);
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
			createSession(intent.getExtras());
			break;
		case T.FRIENDS:
			getFriends(intent.getExtras());
			break;
		case T.FRIEND_REQUEST:
			sendFriendRequest(intent.getExtras());
			break;
		case T.FRIEND_REQUESTS_PENDING:
			getPendingFriendRequests(intent.getExtras());
			break;
		case T.USERS:
			getUsers(intent.getExtras());
			break;
		case T.GET_CROWDS:
			getCrowds(intent.getExtras());
			break;
		case T.CREATE_CROWD:
			createCrowd(intent.getExtras());
			break;
		case T.ADD_CLIP:
			Log.d(TAG, "case ADD_CLIP in API Service");
			addClip(intent.getExtras());
			break;
		case T.ACCEPT_FRIEND_REQUEST:
			acceptFriendRequest(intent.getExtras());
			break;
		case T.GET_SESSIONS:
			getSessions();
			break;
		default:
			break;
		}
	}
	
	
	private void acceptFriendRequest(Bundle data) {
		String username = data.getString(T.USERNAME);
		boolean accepted = data.getBoolean(T.ACCEPTED);
		Log.d(TAG, "Value of accepted in apiservice: " + accepted);
		JSONObject json = null;
		StringEntity entity = null;
		
		try {
			json  = new JSONObject();
			json.put(T.USERNAME, username);
			json.put(T.ACCEPTED, accepted);
			entity = new StringEntity(json.toString());
			String result = api.acceptFriendRequest(entity);
			Log.d(TAG, result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/***
	 * TODO: DOESN"T WORK FUCKKKKKKKKK
	 * @param data
	 */
	private void addClip(Bundle data) {

		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
				.create();
		multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		multipartEntity.setBoundary("---*******");
		multipartEntity.addPart("clip",new FileBody(new File(data.getString(T.FILEPATH))));
		multipartEntity.addTextBody("session", data.getString(T.SESSION));
		HttpEntity entity = multipartEntity.build();
		try {
			String result = api.addClip(entity);
			Log.d(TAG, "FROM createSessions() apiservice" + result);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getPendingFriendRequests(Bundle data) {
		String token = TokenHelper.getToken(this);
		try{
			String result = api.getPendingFriendRequests(token);
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);
		} catch (Exception e) {
			e.printStackTrace();
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

	private void createSession(Bundle data) {
	    MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();   
	    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    
	    multipartEntity.setBoundary("---*******");
	    multipartEntity.addPart("clip", new FileBody(new File(data.getString(T.FILEPATH))));
	    multipartEntity.addTextBody("title", data.getString(T.SESSION_TITLE));
		multipartEntity.addTextBody("crowd", data.getString(T.SESSION_CROWD_ID));
		multipartEntity.addTextBody("use_existing_crowd", data.getBoolean(T.SESSION_USE_EXISTING_CROWD) ? "True": "False");
	    
	    HttpEntity entity = multipartEntity.build();
		try {
			String result = api.createSession(entity);
			Log.d(TAG, "FROM createSessions() apiservice"+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// GET all crowds
	private void getCrowds(Bundle data) {
		Log.d(TAG, "getCrowds() called");
		String token = TokenHelper.getToken(this);
		Crowd[] result = null;
		String json = "";
		try {
			json = api.getCrowds(token);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
		Log.d(TAG, "Got here in getCrowds apiservice");
		Bundle crowdBundle = new Bundle();
		crowdBundle.putString("crowdsJson", json);
		resultReceiver.send(T.GET_CROWDS, crowdBundle);
	}
	
	// POST to crowds
	private void createCrowd(Bundle data) {
		Log.d(TAG, "createCrowd() called");
		String token = TokenHelper.getToken(this);
		Crowd resultCrowd = null;
		
		String crowdTitle = data.getString(T.CROWD_TITLE);
		String[] members = data.getStringArray(T.CROWD_MEMBERS);
		
		PostCrowd pCrowd = new PostCrowd(crowdTitle, members);
		
		try {
			resultCrowd = api.createCrowd(pCrowd, token);
			String postJson = (new Gson()).toJson(resultCrowd);
			Log.d(TAG, "createCrowd() result: " + postJson);
		} catch( Exception e ) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}

	private void getSessions() {
		Log.d(TAG, "getSessions called");

		try {
			String result = api.getSessions();
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);
		} catch (Exception e) {
			e.printStackTrace();
			resultReceiver.send(0,null);
		}
		
	}

}
