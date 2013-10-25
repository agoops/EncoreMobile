package com.encore.API;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import util.Constants;
import android.util.Log;

import com.encore.API.models.User;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

public class API {
	OkHttpClient client; 
	private static Gson mGson;
	
	// Learning TODO
	// 1. Learn about access tokens
	
	// TODO
	// 1. Reconfigure connect() method to work with "Type" and "params"
	// API.get('likes', 'GET', 15)
	
	// Put the ACCESS_TOKEN's in the body
	private static final String ACCESS_TOKEN = "result+from+creating+user+and+storing+access+token"; // send on every request
	private static final String PROD = "rapchat.herokuapp.com";
	private static final String QA = "rapchat.herokuapp.com";
	private static final String BASE_URL = (Constants.DEBUG) ? QA : PROD;

	// Users
	private static final String USERS = BASE_URL + "/users?access_token=" + ACCESS_TOKEN;
	private static final String CREATE_USER = BASE_URL + "/users/newuser?access_token=" + ACCESS_TOKEN;
	// Sessions
	private static final String SESSIONS = BASE_URL + "/sessions?access_token=" + ACCESS_TOKEN;
	private static final String ADD_CLIP = BASE_URL + "/sessions/addclip?access_token=" + ACCESS_TOKEN;
	private static final String SESSION_COMMENTS = BASE_URL + "/sessions/comments?access_token=" + ACCESS_TOKEN;
	private static final String SESSION_LIKES = BASE_URL + "/session/likes?access_token=" + ACCESS_TOKEN;
	// Comments
	private static final String COMMENTS = BASE_URL + "/comments?access_token=" + ACCESS_TOKEN;
	// Likes
	private static final String LIKES = BASE_URL + "/likes?access_token=" + ACCESS_TOKEN;
	
	// Params
	private static final String USER = "&id=%s";
	private static final String SESSION = "&sessionId=%s";
	private static final String COMMENT = "&commentId=%s";
	private static final String LIKE = "&likeId=%s";
	
	private static final String SIGN_IN = BASE_URL + "/oauth?username=%s&password=%s";
	
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
		HttpURLConnection conn = client.open(getUrl);
		conn.setDoInput(true);
		InputStream in = null;
		try {
			conn.setRequestMethod("GET");
			
			in = conn.getInputStream();
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
		connection.setDoOutput(true);
		OutputStream out = null;
		InputStream in = null;
		try {
			connection.setRequestMethod("POST");
			
			// Write entity to the output stream
			out = connection.getOutputStream();
			String entityOut = EntityUtils.toString(entity); // Not used...
			entity.writeTo(out);
			out.close();
			
			// Read the response code
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Unexpected HTTP response: " +
						connection.getResponseCode() + " " + connection.getResponseMessage());
			}
			
			// Return the response 
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if(out != null) out.close();
			if(in != null) in.close();
		}
	}
	
	public String signIn(String username, String password) throws Exception {
		// Is it bad practice to have the username and password in the URL?
		String url = String.format(SIGN_IN, URLEncoder.encode(username, "UTF-8"), password);
		String access_token = get(url, String.class);
		
		return access_token;
	}
	
	public String create_user(User user) throws Exception {
		StringEntity userEntity = new StringEntity(getGson().toJson(user));
		return post(CREATE_USER, userEntity, User.class);
	}
	
	// ---------------- PUT ---------------
	public String makePutRequest(HttpURLConnection conn, URL url, String json) {
		try {
			// Setup the PUT request
			conn.setRequestMethod("PUT");
			conn.setDoOutput(true);
			conn.setChunkedStreamingMode(0);
			conn.setRequestProperty("Accept-Encoding", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			
			// Write the data being sent
			OutputStream outStream = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
			writer.write(json); // May need to URL encode this: writer.write(URLEncoder.encode(json, "UTF-8"));
			writer.flush();
			writer.close();
			outStream.close();
			
			// Execute the request
			conn.connect();
			
			// Read the response
			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Unexpected HTTP response: "
                        + conn.getResponseCode() + " " + conn.getResponseMessage());
			}
			String results = "";
			int response = conn.getResponseCode();
			
			InputStream instream = conn.getInputStream();
			results = convertStreamToString(instream);
			if(instream != null) {
				instream.close();
			}
			
			return results;
			
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	// ----------------- DELETE ----------------
	public String makeDeleteRequest(HttpURLConnection conn, URL url) {
		try {
			// Set up the DELETE request
			conn.setRequestMethod("DELETE");
			
			// Execute
			conn.connect();
			
			// Read the response
			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Unexpected HTTP response: "
                        + conn.getResponseCode() + " " + conn.getResponseMessage());
			}
			String results = "";
			int response = conn.getResponseCode();
			
			InputStream instream = conn.getInputStream();
			results = convertStreamToString(instream);
			if(instream != null) {
				instream.close();
			}
			
			return results;
			
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	// Converts an InputStream to a String
	private String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
}