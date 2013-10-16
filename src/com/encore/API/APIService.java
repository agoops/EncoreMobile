package com.encore.API;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public class APIService {
	
	public static final String DOMAIN = "http://domain.is.not.yet.known";
	
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
	
	// Ignore the next 4 lines
	// GET params:		("GET", ["action", "id"])	
	// POST params:		("POST", ["action", "json_formatted_POST_data"]
	// PUT params:		("PUT", ["action", "json_formatted_PUT_data"]
	// DELETE params: 	("DELETE", ["action", "json_formatted_DELETE_data]
	
	// Example: APIService.connect("post_users", "{ name: \"Babak Pourkazemi\", email: \" bp5xj@virginia.edu \" }", null);
	
	public static String connect(String action, String json_data, String id) {
		try {
			// 1. Create the URL
			String uri = DOMAIN + endpoints.get(action);
			
			if(id != null && id instanceof String) {
				uri += id;
			}
			URL url = new URL(uri);
			
			// 2. Setup the basic connection
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			
			// 3. Get the request method:
			// 	get_users = GET, post_users = POST, etc
			String requestType = action.substring(0, action.indexOf("_")); 
			
			// 4. Execute the request and return
			if(requestType.toUpperCase().equals("GET")) {
				return makeGetRequest(conn, url);
				
			} else if(requestType.toUpperCase().equals("POST")) {
				return makePostRequest(conn, url, json_data);
				
			} else if(requestType.toUpperCase().equals("PUT")) {
				return makePutRequest(conn, url, json_data);
				
			} else if(requestType.toUpperCase().equals("DELETE")) {
				return makeDeleteRequest(conn, url);
				
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	// --------------- GET ---------------
	public static String makeGetRequest(HttpURLConnection conn, URL url) {
		try {
			// Setup the GET request
			conn.setRequestMethod("GET");
			
			// Execute the request
			conn.connect();
			
			// Read the response
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
	
	// ---------------- POST ---------------
	public static String makePostRequest(HttpURLConnection conn, URL url, String json) {
		try {
			// Setup the POST request
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setChunkedStreamingMode(0);
			conn.setRequestProperty("Accept-Encoding", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			
			// Add the data being sent 
			OutputStream outStream = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream, "UTF-8"));
			writer.write(json); // May need to URL encode this: writer.write(URLEncoder.encode(json, "UTF-8"));
			writer.flush();
			writer.close();
			outStream.close();
			
			// Execute the request
			conn.connect();
			
			// Read the response
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
	
	// ---------------- PUT ---------------
	public static String makePutRequest(HttpURLConnection conn, URL url, String json) {
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
	public static String makeDeleteRequest(HttpURLConnection conn, URL url) {
		try {
			// Set up the DELETE request
			conn.setRequestMethod("DELETE");
			
			// Execute
			conn.connect();
			
			// Read the response
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