package com.encore.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;

import util.Constants;
import android.util.Log;

import com.encore.API.models.PostSession;
import com.encore.API.models.Session;
import com.encore.API.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

public class API {
	OkHttpClient client;
	private static Gson mGson;

	private static final String TAG = "API";

	private static final String AUTHORIZATION = "Authorization";
	private static String ACCESS_TOKEN = "result+from+creating+user+and+storing+access+token"; // send on every request
	private static final String PROD = "http://rapchat-django.herokuapp.com";
	private static final String QA = "http://rapchat-django.herokuapp.com";
	private static final String BASE_URL = (Constants.DEBUG) ? QA : PROD;

	// Common URLs
	private static final String USERS = BASE_URL + "/users/";
	private static final String SESSIONS = BASE_URL + "/sessions/";
	private static final String CLIPS = BASE_URL + "/clips/";
	private static final String FRIENDS = BASE_URL + USERS + "friends/";
	private static final String CROWDS = BASE_URL + "/crowds/";
	private static final String REQUESTS = FRIENDS + "requests/";
	private static final String REPLY = REQUESTS + "reply/";

	// Users
	private static final String SIGN_IN = USERS + "obtain-token/";
	private static final String ALL_USERS = USERS;
	private static final String GET_USER = USERS + "/find/%s";
	private static final String SIGN_UP = USERS;
	private static final String USER_ME = USERS + "/me";
	private static final String UPDATE_USER = USERS + "%s";
	private static final String DELETE_USER = USERS + "%s";

	// Sessions
	// private static final String ALL_SESSIONS = SESSIONS;
	private static final String CREATE_SESSION = SESSIONS;
	private static final String GET_SESSION = SESSIONS + "%s";
	private static final String GET_SESSIONS = SESSIONS;

	// private static final String ADD_CLIP = SESSIONS + "%s/addClip";

	// Clips
	// private static final String ALL_CLIPS = CLIPS;
	// private static final String CREATE_CLIP = CLIPS;
	// private static final String GET_CLIP = CLIPS + "%s";
	// private static final String UPDATE_CLIP = CLIPS + "%s";
	// private static final String DELETE_CLIP = CLIPS + "%s";

	// Friends
	// private static final String ALL_FRIENDS = FRIENDS;
	// private static final String GET_FRIEND = FRIENDS + "%s";
	// private static final String GET_FRIEND_REQUEST = FRIENDS + "requests";
	// private static final String CREATE_FRIEND_REQUEST = FRIENDS + "requests";
	// private static final String CREATE_FRIEND_REQUEST_REPLY = FRIENDS +
	// "requests/reply";
	//
	// Crowds
	// private static final String ALL_CROWDS = CROWDS;
	// private static final String CREATE_CROWD = CROWDS + "create/";
	// private static final String GET_CROWD = CROWDS + "%s";
	// private static final String UPDATE_CROWD = CROWDS + "%s";
	// private static final String DELETE_CROWD = CROWDS + "%s";

	public API(OkHttpClient client) {
		this.client = client;

	}

	public Gson getGson() {
		if (mGson == null) {
			mGson = new Gson();
		}
		return mGson;
	}

	// ----------- GET ------------
	private <T> T get(String url, Type type) throws IOException {
		URL getUrl = new URL(url);
		HttpURLConnection connection = client.open(getUrl);
		connection.setRequestProperty(AUTHORIZATION, ACCESS_TOKEN);
		InputStream in = null;
		try {
			connection.setRequestMethod("GET");
			Log.d(TAG, connection.toString());
			in = connection.getInputStream();
			Log.d(TAG, in.toString());
			// return getGson().fromJson(new InputStreamReader(in), type);

			/* Adding this section to see response */
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return (T) total.toString();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("API", "" + e.getMessage());
			return null;
		} finally {
			if (in != null)
				in.close();
		}
	}

	// ------------- POST -----------
	private <T> T post(String url, StringEntity entity, Type type)
			throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = client.open(postUrl);
		connection.setRequestProperty(AUTHORIZATION, ACCESS_TOKEN);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		OutputStream out = null;
		InputStream in = null;
		try {
			connection.setRequestMethod("POST");

			// Write entity to the output stream
			out = connection.getOutputStream();
			entity.writeTo(out);
			Log.d(TAG,
					"POST'ing with auth: "
							+ connection.getRequestProperty(AUTHORIZATION)
							+ " and header " + entity);
			out.close();

			// Read the response code
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
					&& connection.getResponseCode() != HttpURLConnection.HTTP_CREATED
					&& connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				throw new IOException("Unexpected HTTP response: "
						+ connection.getResponseCode() + " "
						+ connection.getResponseMessage());
			}

			// Return the response as the given type
			in = connection.getInputStream();

			// return getGson().fromJson(new InputStreamReader(in), type);
			Log.d(TAG, "Just got input stream: " + in.toString());
			/* Adding this section to see response */
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return (T) total.toString();

		} finally {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		}
	}

	// ------------- PUT -----------
	private <T> T put(String url, StringEntity entity, Type type)
			throws IOException {
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
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
					&& connection.getResponseCode() != HttpURLConnection.HTTP_CREATED
					&& connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				throw new IOException("Unexpected HTTP response: "
						+ connection.getResponseCode() + " "
						+ connection.getResponseMessage());
			}

			// Return the response as the given type
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		}
	}

	// ------------- DELETE -----------
	private <T> T delete(String url, StringEntity entity, Type type)
			throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = client.open(postUrl);
		InputStream in = null;
		try {
			Log.d(TAG, "DELETE");
			connection.setRequestMethod("DELETE");

			// Read the response code
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
					&& connection.getResponseCode() != HttpURLConnection.HTTP_CREATED
					&& connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				throw new IOException("Unexpected HTTP response: "
						+ connection.getResponseCode() + " "
						+ connection.getResponseMessage());
			}

			// Return the response as the given type
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if (in != null)
				in.close();
		}
	}

	public String signUp(User user) throws Exception {
		Log.d(TAG, "signUp called, body: " + getGson().toJson(user).toString());

		String url = SIGN_UP;
		User mUser = post(url, new StringEntity(getGson().toJson(user)),
				User.class);

		// Access Token!
		String access_token = user.get_access_token();
		return access_token;
	}

	public String signIn(StringEntity entity) throws Exception {
		Log.d(TAG, "signIn called with entity: " + entity.toString());
		String url = SIGN_IN;
		String result = "emptystringdawg-API.signin worked?";
		try {
			result = post(url, entity, String.class);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	public String getFriends(String token) throws Exception {
		Log.d(TAG, "getFriends called");
		ACCESS_TOKEN = "Token " + token;
		String url = FRIENDS;
		String result = "defaultstringifnoresult";
		try {
			result = get(url, String.class);
			return result;
		} catch (Exception e) {
			Log.d(TAG, "API.getFriends error");
			throw e;
		}

	}

	public Session createSession(PostSession pSession, String token) throws Exception {
		Log.d(TAG, "createSession() called");
		String url = CREATE_SESSION;
		String postResult = null;
		Session result = null;
		ACCESS_TOKEN = "Token " + token;
		try {
			String JSON = getGson().toJson(pSession);
			Log.d(TAG, "JSON: " + JSON);
			
			postResult = post(url, new StringEntity(getGson().toJson(pSession)),
					String.class);
			result = getGson().fromJson(postResult, Session.class);
			
		} catch (Exception e) {
			Log.e(TAG, "createSession() error");
			throw e;
		}
		return result;
	}

	public Session getSession(String id) throws Exception {
		Log.d(TAG, "getSession called with body: "
				+ getGson().toJson(id).toString());

		String url = String.format(GET_SESSION, id);
		return get(url, Session.class);
	}

	// NEEDS TO BE TESTED
	public List<Session> getSessions() throws Exception {
		Log.d(TAG, "getSessions called");

		String url = GET_SESSIONS;
		Type listType = new TypeToken<List<Session>>() {
		}.getType();

		// Query doesn't work, using dummy data for now.
		List<Session> dummy = new ArrayList<Session>();
		dummy.add(new Session());
		dummy.add(new Session());
		dummy.add(new Session());
		return dummy;
		// return get(url, listType);
	}
}