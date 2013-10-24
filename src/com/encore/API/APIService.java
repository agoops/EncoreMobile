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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import util.Constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

public class APIService {
	OkHttpClient client; 
	private static Gson mGson;
	// Learning TODO
	// 1. Learn about access tokens
	// 2. Go over correct Gson usage
	
	// TODO
	// 1. Reconfigure connect() method to work with "Type" and "params"
	// API.get('likes', 'GET', 15)
	
	// Put the ACCESS_TOKEN's in the body
	private static final String ACCESS_TOKEN = "result+from+creating+user+and+storing+access+token"; // send on every request
	private static final String PROD = "rapchat.herokuapp.com";
	private static final String QA = "rapchat.herokuapp.com";
	private static final String BASE_URL = (Constants.DEBUG) ? QA : PROD;

	// Users
	private static final String users = BASE_URL + "/users?access_token=" + ACCESS_TOKEN;
	private static final String create_user = BASE_URL + "/users/newuser?access_token=" + ACCESS_TOKEN;
	
	// Sessions
	private static final String sessions = BASE_URL + "/sessions?access_token=" + ACCESS_TOKEN;
	private static final String add_clip = BASE_URL + "/sessions/addclip?access_token=" + ACCESS_TOKEN;
	private static final String session_comments = BASE_URL + "/sessions/comments?access_token=" + ACCESS_TOKEN;
	private static final String session_likes = BASE_URL + "/session/likes?access_token=" + ACCESS_TOKEN;
	
	// Comments
	private final String comments = BASE_URL + "/comments?access_token=" + ACCESS_TOKEN;
	
	// Likes
	private final String likes= BASE_URL + "/likes?access_token=" + ACCESS_TOKEN;
	
	// Params
	private static final String USER = "&id=%s";
	private static final String SESSION = "&sessionId=%s";
	private static final String COMMENTS = "&commentId=%s";
	private static final String LIKES = "&likeId=%s";
	
	public APIService(OkHttpClient client) {
		this.client = client;
	}
	
//	private <T> T get(String url, StringEntity entity, Type type) throws IOException {
//		
//	}
	
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
			String entityOut = EntityUtils.toString(entity);
			entity.writeTo(out);
			out.close();
			
			// Read the response code
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Unexpected HTTP response: " +
						+ connection.getResponseCode() + " " + connection.getResponseMessage());
			}
			
			// Return the response 
			in = connection.getInputStream();
			return getGson().fromJson(new InputStreamReader(in), type);
		} finally {
			if(out != null) out.close();
			if(in != null) in.close();
		}
	}
	
	public Gson getGson() {
		if(mGson == null) {
			mGson = new Gson();
		}
		return mGson;
	}
	
	public String connect(String action, String type, String params) {
		try {
			// 1. Create the URL
			String url_action = action;
			if(params != null && params instanceof String) {
				url_action = url_action + "&id=" + params ;
			}
			URL url = new URL(url_action);
			
			// 2. Setup the basic connection
			HttpURLConnection conn = client.open(url);
			conn.setDoInput(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);
			
			// 3. Execute the request and return
			if(type.toUpperCase().equals("GET")) {
				return makeGetRequest(conn, url);
				
			} else if(type.toUpperCase().equals("POST")) {
				return makePostRequest(conn, url, params);
				
			} else if(type.toUpperCase().equals("PUT")) {
				return makePutRequest(conn, url, params);
				
			} else if(type.toUpperCase().equals("DELETE")) {
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
	public String makeGetRequest(HttpURLConnection conn, URL url) {
		try {
			// Setup the GET request
			conn.setRequestMethod("GET");
			
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
	
	// ---------------- POST ---------------
	public String makePostRequest(HttpURLConnection conn, URL url, String json) {
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
			if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("Unexpected HTTP response: "
                        + conn.getResponseCode() + " " + conn.getResponseMessage());
			}
			
			String results = "";
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