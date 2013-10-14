package com.encore.API;

import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class APIService {
	
	public final static String DOMAIN = "TBD";
	public String get_users = "/users",
			new_user = "/users/newuser",
			get_session = "/sessions/";
			
	
	public APIService() {
		// No instance variables necessary
	}
	
	// ------------- GET --------------
	public static HttpResponse get(String type, String[] params) {
		return makeGetRequest("change_this");
	}
	
	public static HttpResponse makeGetRequest(String uri) {
		try {
			HttpGet httpGet = new HttpGet(uri);
			
			return new DefaultHttpClient().execute(httpGet);
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
	// Converts the Map<String, String> to JSON, which is sent in the POST payload
	public static HttpResponse post(String uri, Map<String, String> data) {
		// Convert Map<String, String> to json
		String json = new GsonBuilder().create().toJson(data, Map.class);
		
		// Make the post request
		return makePostRequest(uri, json);
	}
	
	// Execute the POST request to the target URI
	public static HttpResponse makePostRequest(String uri, String json) {
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(new StringEntity(json));
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			return new DefaultHttpClient().execute(httpPost);
			
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	// ------------- PUT --------------
	public static HttpResponse put(String uri, Map<String, String> data) {
		// Convert Map<String, String> to json
		String json = new GsonBuilder().create().toJson(data, Map.class);
		
		// Make the PUT request
		return makePutRequest(uri, json);
	}
	
	// Execute the POST request to the target URI
	public static HttpResponse makePutRequest(String uri, String json) {
		try {
			HttpPut httpPut = new HttpPut(uri);
			httpPut.setEntity(new StringEntity(json));
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-type", "application/json");
			
			return new DefaultHttpClient().execute(httpPut);
			
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	// ------------- DELETE (needs work) --------------
	public static HttpResponse delete(String uri, Map<String, String> data) {
		// Convert Map<String, String> to json
		String json = new GsonBuilder().create().toJson(data, Map.class);
		
		// Make the DELETE request
		return makeDeleteRequest(uri, json);
	}
	
	// Execute the DELETE request to the target URI
	public static HttpResponse makeDeleteRequest(String uri, String json) {
		try {
			HttpDelete httpDelete = new HttpDelete(uri);
			httpDelete.setHeader("Accept", "application/json");
			httpDelete.setHeader("Content-type", "application/json");
			
			return new DefaultHttpClient().execute(httpDelete);
			
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
}