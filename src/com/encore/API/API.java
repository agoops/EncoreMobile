package com.encore.API;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.entity.StringEntity;

import util.Constants;
import android.util.Log;

import com.encore.API.models.Session;
import com.encore.API.models.User;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

public class API {
	OkHttpClient client; 
	private static Gson mGson;
	
	private static final String TAG = "API";
	
	private static final String ACCESS_TOKEN = "result+from+creating+user+and+storing+access+token"; // send on every request
	private static final String PROD = "http://rapchat.herokuapp.com";
	private static final String QA = "http://rapchat.herokuapp.com";
	private static final String BASE_URL = (Constants.DEBUG) ? QA : PROD;

	// Common URLs
	private static final String USERS = BASE_URL + "/users/";
	private static final String SESSIONS = BASE_URL + "/sessions/";
	private static final String CLIPS = BASE_URL + "/clips/";
	private static final String FRIENDS = USERS + "friends/";
	private static final String CROWDS = BASE_URL + "/crowds/";
	
	// Users
	private static final String ALL_USERS = USERS;
	private static final String GET_USER = USERS + "/find/%s";
	private static final String SIGN_UP = USERS + "create";
	private static final String USER_ME = USERS + "/me";
	private static final String UPDATE_USER = USERS + "%s";
	private static final String DELETE_USER = USERS + "%s";
	
	// Sessions
	private static final String ALL_SESSIONS = SESSIONS;
	private static final String CREATE_SESSION = SESSIONS;
	private static final String GET_SESSION = SESSIONS + "%s";
	private static final String ADD_CLIP = SESSIONS + "%s/addClip";
	
	// Clips
	private static final String ALL_CLIPS = CLIPS;
	private static final String CREATE_CLIP = CLIPS;
	private static final String GET_CLIP = CLIPS + "%s";
	private static final String UPDATE_CLIP = CLIPS + "%s";
	private static final String DELETE_CLIP = CLIPS + "%s";
	
	// Friends
	private static final String ALL_FRIENDS = FRIENDS;
	private static final String GET_FRIEND = FRIENDS + "%s"; // untested
	private static final String GET_FRIEND_REQUEST = FRIENDS + "requests";
	private static final String CREATE_FRIEND_REQUEST = FRIENDS + "requests";
	private static final String CREATE_FRIEND_REQUEST_REPLY = FRIENDS + "requests/reply";
	
	// Crowds
	private static final String ALL_CROWDS = CROWDS;
	private static final String CREATE_CROWD = CROWDS + "create/";
	private static final String GET_CROWD = CROWDS + "%s";
	private static final String UPDATE_CROWD = CROWDS + "%s";
	private static final String DELETE_CROWD = CROWDS + "%s";
	
	public API(OkHttpClient client) {
		this.client = client;
	}
	
	public Gson getGson() {
		if(mGson == null) {
			mGson = new Gson();
		}
		return mGson;
	}
	
	// ----------- GET ------------
	private <T> T get(String url, Type type) throws IOException {
		URL getUrl = new URL(url);
		HttpURLConnection connection = client.open(getUrl);
		connection.setDoInput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		InputStream in = null;
		try {
			connection.setRequestMethod("GET");
			
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} catch(Exception e) {
			Log.e("API", "" + e.getMessage());
			return null;
		}
		finally {
			if(in != null) in.close();
		}
	}
	
	// ------------- POST -----------
	private <T> T post(String url, StringEntity entity, Type type) throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = client.open(postUrl);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		OutputStream out = null;
		InputStream in = null;
		try {
			connection.setRequestMethod("POST");
			
			// Write entity to the output stream
			out = connection.getOutputStream();
			entity.writeTo(out);
			Log.d(TAG, "POST'ing with body: " + entity);
			out.close();
			
			// Read the response code
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK && 
					connection.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
						connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				throw new IOException("Unexpected HTTP response: " +
						connection.getResponseCode() + " " + connection.getResponseMessage());
			}
			
			// Return the response as the given type 
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if(out != null) out.close();
			if(in != null) in.close();
		}
	}
	
	// ------------- PUT -----------
	private <T> T put(String url, StringEntity entity, Type type) throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = client.open(postUrl);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		OutputStream out = null;
		InputStream in = null;
		try {
			Log.d(TAG, "PUT body: " + entity);
			connection.setRequestMethod("PUT");
			
			// Write entity to the output stream
			out = connection.getOutputStream();
			entity.writeTo(out);
			out.close();
			
			// Read the response code
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK && 
					connection.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
						connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				throw new IOException("Unexpected HTTP response: " +
						connection.getResponseCode() + " " + connection.getResponseMessage());
			}
			
			// Return the response as the given type 
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if(out != null) out.close();
			if(in != null) in.close();
		}
	}
	
	// ------------- DELETE -----------
	private <T> T delete(String url, StringEntity entity, Type type) throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = client.open(postUrl);
		InputStream in = null;
		try {
			Log.d(TAG, "DELETE");
			connection.setRequestMethod("DELETE");
			
			// Read the response code
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK && 
					connection.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
						connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				throw new IOException("Unexpected HTTP response: " +
						connection.getResponseCode() + " " + connection.getResponseMessage());
			}
			
			// Return the response as the given type 
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if(in != null) in.close();
		}
	}
	
	public String signUp(User user) throws Exception {
		Log.d(TAG, "signUp called, bodyis : " + getGson().toJson(user).toString());
		
		String url = SIGN_UP;
		User mUser = post(url, new StringEntity(getGson().toJson(user)), User.class);
		
		// Access Token!
		String access_token = user.get_access_token();
		return access_token;
	}
	
	public boolean createSession(Session session) throws Exception { 
		return false;
	}
}