package com.encore.API;

import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class APIService {
	
	public APIService() {
		// No instance variables necessary
	}
	
	// Converts the Map<String, String> to JSON, which is sent in the POST payload
	public static HttpResponse post(String uri, Map<String, String> mapData) {
		// Convert Map<String, String> to json
		String json = new GsonBuilder().create().toJson(mapData, Map.class);
		
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
}