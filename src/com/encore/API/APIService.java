package com.encore.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import android.net.http.AndroidHttpClient;

import com.google.gson.GsonBuilder;

public class APIService {
	
	public static final String DOMAIN = "domain.is.not.yet.known";
	
	private static final Map<String, String> endpoints = new HashMap<String, String>();
	static {
		// Users
		endpoints.put("get_users", "/users/"); // + :id
		endpoints.put("put_users", "/users/");
		endpoints.put("post_users", "/users/newuser/");
		
		// Sessions
		endpoints.put("get_sessions", "/sessions/"); // + :id
		endpoints.put("post_sessions", "/sessions/");
		endpoints.put("add_clip_to_session", "/sessions/addclip/"); // + :sessionId
		endpoints.put("get_session_comments", "/sessions/comments/"); // + :sessionId
		endpoints.put("post_session_comments", "/sessions/comments"); // + :sessionId
		endpoints.put("post_like", "sessions/likes/"); // + :sessionId
		endpoints.put("delete_like", "sessions/likes/"); // + :sessionId
		
		// Comments
		endpoints.put("get_all_comments", "/comments/");
		endpoints.put("get_comment", "/comments/"); // + :commentId
		endpoints.put("put_comment", "/comments/"); // + :commentId
		
		// Likes
		endpoints.put("get_all_likes", "/likes/");
		endpoints.put("get_like", "/likes/"); // + :likeId
	}
	
	public APIService() {
		// No instance variables necessary
	}
	
	// GET params:		("GET", ["action", "id"])	
	// POST params:		("POST", ["action", "json_formatted_POST_data"]
	// PUT params:		("PUT", ["action", "json_formatted_PUT_data"]
	// DELETE params: 	("DELETE", ["action", "json_formatted_DELETE_data]
	
	// Example: APIService.connect("post_users", "{ name: \"Babak Pourkazemi\", email: \" bp5xj@virginia.edu \" }", null);
	
	public static String connect(String action, String json_data, String id) {
		// 1. Create the URI
		String uri = DOMAIN + endpoints.get(action);
		if(id != null && id instanceof String) {
			uri += id;
		}
		
		// 2. Get the right request type:
		// 		get_users = GET, post_users = POST, etc
		String requestType = action.substring(0, action.indexOf("_")); 
		
		// 3. Execute the request and return
		if(requestType.toUpperCase().equals("GET")) {
			return makeGetRequest(uri);
			
		} else if(requestType.toUpperCase().equals("POST")) {
			return makePostRequest(uri, json_data);
			
		} else if(requestType.toUpperCase().equals("PUT")) {
			return makePutRequest(uri, json_data);
			
		} else if(requestType.toUpperCase().equals("DELETE")) {
			return makeDeleteRequest(uri);
			
		}
		return "";
	}
	
	// --------------- GET ---------------
	public static String makeGetRequest(String uri) {
		try {
			// Create a new client to communicate with our RESTful services
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			HttpGet httpGet = new HttpGet(uri);
			
			// Execute request and grab the HttpEntity - 
			// which includes the Header, Content-Type, and payload
			HttpEntity entity = client.execute(httpGet).getEntity();
			
			// Convert response to a string
			String results = "";
			if(entity != null) {
				InputStream instream = entity.getContent();
				results = convertStreamToString(instream);
				
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
	
	// ------------- POST --------------
	public static String post(String uri, Map<String, String> data) {
		// Convert Map<String, String> to JSON
		String json = new GsonBuilder().create().toJson(data, Map.class);
		
		// Make the post request
		return makePostRequest(uri, json);
	}
	
	// Execute the POST request to the target URI
	public static String makePostRequest(String uri, String json) {
		try {
			// Create a new client to communicate with our RESTful services
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			
			// Setup the POST request
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			// Execute request and grab the HttpEntity - 
			// which includes the Header, Content-Type, and payload
			HttpEntity entity = client.execute(httpPost).getEntity();
			
			// Convert response to a string
			String results = "";
			if(entity != null) {
				InputStream instream = entity.getContent();
				results = convertStreamToString(instream);
				
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
	
	// ---------------- PUT ---------------
	public static String makePutRequest(String uri, String json) {
		try {
			// Create a new client to communicate with our RESTful services
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			
			// Setup the POST request
			HttpPut httpPut = new HttpPut(uri);
			httpPut.setEntity(new StringEntity(json));
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-type", "application/json");
			
			// Execute request and grab the HttpEntity - 
			// which includes the Header, Content-Type, and payload
			HttpEntity entity = client.execute(httpPut).getEntity();
			
			// Convert response to a string
			String results = "";
			if(entity != null) {
				InputStream instream = entity.getContent();
				results = convertStreamToString(instream);
				
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
	public static String makeDeleteRequest(String uri) {
		try {
			// Create a new client to communicate with our RESTful services
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			
			// Setup the POST request
			HttpDelete httpDelete = new HttpDelete(uri);
			
			// Execute request and grab the HttpEntity - 
			// which includes the Header, Content-Type, and payload
			HttpEntity entity = client.execute(httpDelete).getEntity();
			
			// Convert response to a string
			String results = "";
			if(entity != null) {
				InputStream instream = entity.getContent();
				results = convertStreamToString(instream);
				
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
	private static String convertStreamToString(InputStream is) {
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