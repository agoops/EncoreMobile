package com.encore.API;

import android.content.Context;
import android.util.Log;

import com.encore.TokenHelper;
import com.encore.models.PostComment;
import com.encore.models.PostLike;
import com.encore.models.UpdateUser;
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
	private static final String PROD = "http://rapback.herokuapp.com/";
	private static final String QA = "http://rapback.herokuapp.com/";
	private static final String BASE_URL = Constants.DEBUG ? QA:PROD;

	// Common URLs
	private static final String USERS = BASE_URL + "users/";
    private static final String USER_ME = USERS + "me/";
    private static final String USER_OTHER = USERS + "%s";
    private static final String SESSIONS = USER_ME + "sessions/";
    private static final String FRIENDS = USER_ME + "friends/";
    private static final String REQUESTS = FRIENDS + "requests/";
    private static final String REPLY = REQUESTS + "reply/";
    private static final String LIKES = USER_ME + "likes/";
    private static final String CLIP = BASE_URL + "sessions/%s/clips/";
    // Sign up / Log in
	private static final String LOG_IN = USERS + "obtain-token/";
    private static final String SIGN_UP = USERS;
    // Sessions
    private static final String GET_LIVE_SESSIONS = SESSIONS;
    private static final String GET_COMPLETE_SESSIONS = SESSIONS + "complete/";
    private static final String CREATE_SESSION = SESSIONS;
    private static final String SESSION_PAGE = SESSIONS + "?page=%s";
    // Clips
    private static final String GET_CLIP = CLIP;
    private static final String ADD_CLIP = CLIP;
    // Friends
	private static final String SEND_FRIEND_REQUEST = FRIENDS + "requests/";
    // Comments
	private static final String CREATE_COMMENT = BASE_URL + "sessions/%s/comments/";
    // Search
    private static final String SEARCH_USERNAME = BASE_URL + "search/?username=%s";

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
        Log.d(TAG, "Auth header is: " + connection.getRequestProperty(AUTHORIZATION));
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
        connection.setRequestProperty(AUTHORIZATION, ACCESS_TOKEN);
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
		Log.d(TAG, "signUp called, body: " + entity.toString());
        isSigningIn = true;
		String url = SIGN_UP;
        String result = "";
        try {
            result = post(url, entity, String.class);
            isSigningIn = false;
        } catch (Exception e) {
            throw e;
        }

		return result;
	}

	public String logIn(StringEntity entity) throws Exception {
		Log.d(TAG, "logIn called with entity: " + entity.toString());
		String url = LOG_IN;
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
	
	public String getLiveSessions() throws IOException {
		Log.d(TAG, "getLiveSessions called");

		String url = GET_LIVE_SESSIONS;
		String result = "emptyresult";
		try {
			result = get(url, String.class);
			Log.d(TAG, "getLiveSessions resulting JSON: " + result);
		} catch (IOException e) {
			Log.d(TAG, "getLiveSessions() error");
			throw e;
		}
		return result;
	}

    public String getCompleteSessions() throws IOException {
        Log.d(TAG, "getCompleteSessions called");

        String url = GET_COMPLETE_SESSIONS;
        String result = "emptyresult";
        try {
            result = get(url, String.class);
            Log.d(TAG, "getCompleteSessions resulting JSON: " + result);
        } catch (IOException e) {
            Log.d(TAG, "getLiveSessions() error");
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
        String url = String.format(GET_CLIP, Integer.toString(sessionId));
		String resultJSON = null;
		
		try {
			resultJSON = get(url, String.class);
			Log.d(TAG, "getClipStream result: " + resultJSON);
		} catch(Exception e) {
			Log.e(TAG, "getClipStream() error");
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

    public String updateUser(String token, UpdateUser user) throws Exception {
        Log.d(TAG, "updateUser called");
        String url = USER_ME;
        ACCESS_TOKEN = "Token " + token;
        String resultJSON = null;

        try {
            String JSON = getGson().toJson(user);
            Log.d(TAG, "Posting JSON: " + JSON);

            resultJSON = put(url, new StringEntity(JSON), String.class);
            Log.d(TAG, "updateUser result: " + resultJSON);
        } catch(Exception e) {
            Log.d(TAG, "updateUser error");
            throw e;
        }
        return resultJSON;
    }

    public String searchUsername(String token, String username) throws Exception {
        Log.d(TAG, "searchUsername called");
        String url = String.format(SEARCH_USERNAME, username);
        ACCESS_TOKEN = "Token " + token;
        String resultJSON = null;

        try {
            resultJSON = get(url, String.class);
            Log.d(TAG, "searchUsername result: " + resultJSON);
        } catch(Exception e) {
            Log.d(TAG, "searchUsername error");
            throw e;
        }
        return resultJSON;
    }

    public String paginateNextSession(String token, String nextUrl) throws Exception {
        Log.d(TAG, "paginateNextSession called");
        ACCESS_TOKEN = "Token " + token;
        String resultJSON = null;

        try {
            resultJSON = get(nextUrl, String.class);
            Log.d(TAG, "paginateNextSession result: " + resultJSON);
        } catch(Exception e) {
            Log.d(TAG, "paginateNextSession error");
            throw e;
        }
        return resultJSON;
    }

    public String getOtherProfile(String token, String username) throws Exception {
        Log.d(TAG, "getOtherProfile called");
        String url = String.format(USER_OTHER, username);
        ACCESS_TOKEN = "Token " + token;
        String resultJSON = null;

        try {
            resultJSON = get(url, String.class);
            Log.d(TAG, "getOtherProfile result: " + resultJSON);
        } catch(Exception e) {
            Log.d(TAG, "getOtherProfile error");
            throw e;
        }
        return resultJSON;
    }
}
