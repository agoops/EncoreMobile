package com.encore.API;

import android.content.Context;
import android.util.Log;

import com.encore.TokenHelper;
import com.encore.models.Crowd;
import com.encore.models.PostComment;
import com.encore.models.PostCrowd;
import com.encore.models.PostLike;
import com.encore.util.Constants;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
//import com.encore.models.Crowds;

public class API {
	OkHttpClient client;
	private static Gson mGson;

	private static final String TAG = "API";

	/***************************************
	 * 
	 * Different endpoints the client can hit
	 * 
	 * *****************************************
	 */
	private boolean isSigningIn = false;
	private static final String AUTHORIZATION = "Authorization";
	private static String ACCESS_TOKEN = "invalidaccesstoken";
	private static final String PROD = "http://rapchat-django.herokuapp.com/";
	private static final String QA = "http://rapchat-django.herokuapp.com/";
	private static final String BASE_URL = Constants.DEBUG ? QA:PROD;

	// Common URLs
	private static final String USERS = BASE_URL + "users/";
    private static final String USER_ME = USERS + "me/";
    private static final String SESSIONS = USER_ME + "sessions/";
    private static final String FRIENDS = USER_ME + "friends/";
    private static final String CROWDS = USER_ME + "crowds/";
    private static final String REQUESTS = FRIENDS + "requests/";
    private static final String REPLY = REQUESTS + "reply/";
    private static final String LIKES = USER_ME + "likes/";
    // Users
	private static final String SIGN_IN = USERS + "obtain-token/";
    private static final String SIGN_UP = USERS;
    private static final String UPDATE_USER = USERS + "%s/";
    private static final String DELETE_USER = USERS + "%s/";
    // Sessions
    private static final String GET_SESSIONS = SESSIONS;
    private static final String CREATE_SESSION = SESSIONS;
    private static final String GET_SESSION = BASE_URL + "sessions/%s/";
    // Clips
    private static final String GET_CLIP_STREAM = SESSIONS + "clip/";
    private static final String ADD_CLIP = BASE_URL + "sessions/%s/clips/";
    // Friends
	private static final String SEND_FRIEND_REQUEST = FRIENDS + "requests/";
    // Crowds
	private static final String GET_CROWDS = CROWDS;
    private static final String CREATE_CROWD = CROWDS;
    // Comments
	private static final String CREATE_COMMENT = BASE_URL + "sessions/%s/comments/";

	public API(OkHttpClient client, Context context) {
		this.client = client;
		this.ACCESS_TOKEN = "Token " + TokenHelper.getToken(context);

	}

	public Gson getGson() {
		if (mGson == null) {
			mGson = new Gson();
		}
		return mGson;
	}

	/**********************************************
	 * 
	 * GET, POST, PUT, DELETE api service call methods
	 * 
	 * *********************************************
	 */

	// ----------- GET ------------
	private <T> T get(String url, Type type) throws IOException {
		URL getUrl = new URL(url);
		HttpURLConnection connection = client.open(getUrl);
		connection.setRequestProperty(AUTHORIZATION, ACCESS_TOKEN);
		InputStream in = null;
		try {
			connection.setRequestMethod("GET");
			in = connection.getInputStream();
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
			Log.e("API", e.getMessage() + " ");
			return null;
		} finally {
			if (in != null)
				in.close();
		}
	}

	// ------------- POST -----------
	private <T> T post(String url, HttpEntity entity, Type type)
			throws IOException {
		URL postUrl = new URL(url);
		HttpURLConnection connection = client.open(postUrl);
		if(!isSigningIn) {
			connection.setRequestProperty(AUTHORIZATION, ACCESS_TOKEN);
		}
		isSigningIn = false; // Reset to !isSigningIn
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		OutputStream out = null;
		InputStream in = null;
		try {
			connection.setRequestMethod("POST");

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

	// ---------------------POST CLIP----------------------

	private String postClip(HttpEntity entity, String url) throws Exception {
		HttpClient client = new DefaultHttpClient();
	    HttpPost post = new HttpPost(url);
	    post.addHeader(AUTHORIZATION, ACCESS_TOKEN);
	    post.setEntity(entity);
	    
	    HttpResponse response;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
//			e.printStackTrace();
			throw e;
		} catch (IOException e) {
//			e.printStackTrace();
			throw e;
		}
		
	    entity = response.getEntity();
	    BufferedReader r = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}
		return total.toString();
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

	/*************************************************************
	 * 
	 * Helper methods to take in the kind of request occurring, and execute the
	 * proper method above.
	 * 
	 * *
	 * 
	 * @throws IOException
	 *             *************************************************************
	 *             *
	 */

	public String acceptFriendRequest(StringEntity entity) throws IOException {
		String url = REPLY;
		String result = "emptyresult_failed";

		try {
			result = post(url, entity, String.class);
		} catch (IOException e) {
			throw e;
		}
		return result;
	}

	public String addClip(HttpEntity entity, int sessionId)
			throws Exception {

		// String url, StringEntity entity, Type type, String path

		String url = String.format(ADD_CLIP, Integer.toString(sessionId));
		
		String result = "emptyresult_failed";
		try {
			result = postClip(entity, url);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String signUp(StringEntity entity) throws Exception {
		Log.d(TAG, "signUp called, body: ");

		String url = SIGN_UP;
		String result = post(url, entity, String.class);

		return result;
	}

	public String signIn(StringEntity entity) throws Exception {
		Log.d(TAG, "signIn called with entity: " + entity.toString());
		String url = SIGN_IN;
		isSigningIn = true;
		String result = "emptystringdawg-API.signin worked?";
		try {
			result = post(url, entity, String.class);
		} catch (Exception e) {
			throw e;
		}
		Log.d(TAG, "Result is " + result);
		return result;
	}

	public String sendFriendRequest(String token, StringEntity entity)
			throws Exception {
		ACCESS_TOKEN = "Token " + token;
		String url = SEND_FRIEND_REQUEST;
		String result = "emptystirngdawg-sendfriendrequest probably didn't work";

		try {
			result = post(url, entity, String.class);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	public String getPendingFriendRequests(String token) throws Exception {
		ACCESS_TOKEN = "Token " + token;
		String url = REQUESTS;
		String result = "emptystringiffailed??";

		try {
			result = get(url, String.class);
			return result;
		} catch (Exception e) {
			Log.d(TAG, "API.getPendingFriendRequests() error");
			throw e;
		}
	}

	public String getUsers(String token) throws Exception {
		Log.d(TAG, "getUsers called");
		ACCESS_TOKEN = "Token " + token;
		String url = USERS;
		String result = "defaultstringifnoresult";
		try {
			result = get(url, String.class);
			return result;
		} catch (Exception e) {
			Log.d(TAG, "API.getUsers error");
			throw e;
		}
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
	
	// POST to sessions/
	// Creates a new session
	public String createSession(HttpEntity entity) throws Exception {
		
		Log.d(TAG, "createSession() called");
		String url = CREATE_SESSION;
		String postResult = null;
		String result = null;
		try {
			postResult = postClip(entity, url);
			result = postResult;
			Log.d(TAG, "POST result: " + postResult);
//			result = getGson().fromJson(postResult, String.class);

		} catch (Exception e) {
			Log.e(TAG, "createSession() error");
			throw e;
		}
		return result;
	}
	
	// GET crowds/
	// Returns all crowds
	public String getCrowds(String token) throws Exception {
		Log.d(TAG, "getCrowds called");
		ACCESS_TOKEN = "Token " + token;
		String url = GET_CROWDS;
		String json = "";
        ArrayList<Crowd> result;

		// try {
		// json = get(url, Crowds.class);
		// result = getGson().fromJson(json, Crowds.class);
		// } catch(Exception e) {
		// Log.e(TAG, "getCrowds() error");
		// throw e;
		// }

		try {
			json = get(url, Crowd.class);
			Log.d(TAG, "get crowds JSON: " + json);
		} catch(Exception e) {
			Log.e(TAG, "getCrowds() error");
			throw e;
		}
		
		return json;
	}
	
	// POST crowds/
	// Creates a new crowd
	public Crowd createCrowd(PostCrowd pCrowd, String token) throws Exception {
		Log.d(TAG, "createCrowd called");
		ACCESS_TOKEN = "Token " + token;
		String url = CREATE_CROWD;
		String postResult = null;
		Crowd resultCrowd = null;
		
		try {
			String JSON = getGson().toJson(pCrowd);
			Log.d(TAG, "Posting JSON: " + JSON);
			
			postResult = post(url, new StringEntity(JSON), String.class);
			
			resultCrowd = getGson().fromJson(postResult, Crowd.class);
		} catch (Exception e) {
			Log.e(TAG, "createCrowd() error");
			throw e;
		}
		return resultCrowd;
	}
	
	public String getSessions() throws IOException {
		Log.d(TAG, "getSessions called");

		String url = GET_SESSIONS;
		String result = "emptyresult";
		try {
			result = get(url, String.class);
			Log.d(TAG, "getSessions resulting JSON: " + result);
		} catch (IOException e) {
			Log.d(TAG, "getSessions() error");
			throw e;
		}
		return result;
	}
	
	public String createComment(PostComment pComment, String token, int sessionId) throws Exception {
		Log.d(TAG, "createComment called");
		ACCESS_TOKEN = "Token " + token;
		String url = String.format(CREATE_COMMENT, String.valueOf(sessionId));
		String resultJSON = null;
		
		try {
			String JSON = getGson().toJson(pComment);
			Log.d(TAG, "Posting JSON: " + JSON);
			resultJSON = post(url, new StringEntity(JSON), String.class);
		} catch (Exception e) {
			Log.e(TAG, "createComment() error");
			throw e;
		}
		return resultJSON;
	}
	
	public String createLike(PostLike like, String token) throws Exception {
		Log.d(TAG, "createLike called");
		ACCESS_TOKEN = "Token " + token;
		String url = LIKES;
		String resultJSON = null;
		
		try {
			String JSON = getGson().toJson(like);
			Log.d(TAG, "Posting JSON: " + JSON);
			resultJSON = post(url, new StringEntity(JSON), String.class);
		} catch (Exception e) {
			Log.e(TAG, "createLike() error");
			throw e;
		}
		return resultJSON;
	}
	
	public String getClipStream(int sessionId) throws Exception {
		Log.d(TAG, "getClipStream called");
		String url = SESSIONS + sessionId + "/clips/";
		String resultJSON = null;
		
		try {
			resultJSON = get(url, String.class);
			Log.d(TAG, "getClipStream result: " + resultJSON);
		} catch(Exception e) {
			Log.e(TAG, "createComment() error");
			throw e;
		}
		return resultJSON;
	}
	
	public String getMe(String token) throws Exception {
		Log.d(TAG, "getMe called");
		String url = USER_ME;
		ACCESS_TOKEN = "Token " + token;
		String resultJSON = null;
		
		try {
			resultJSON = get(url, String.class);
			Log.d(TAG, "getMe result: " + resultJSON);
		} catch(Exception e) {
			Log.d(TAG, "getMe() error");
			throw e;
		}
		return resultJSON;
	}

    public String getLikes(String token) throws Exception {
        Log.d(TAG, "getLikes called");
        String url = LIKES;
        ACCESS_TOKEN = "Token " + token;
        String resultJSON = null;

        try {
            resultJSON = get(url, String.class);
            Log.d(TAG, "getLikes result: " + resultJSON);
        } catch(Exception e) {
            Log.d(TAG, "getLikes() error");
            throw e;
        }
        return resultJSON;
    }
}
