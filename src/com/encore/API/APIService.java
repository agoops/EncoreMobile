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

/**
 * Created with IntelliJ IDEA.
 * User: babakpourkazemi
 * Date: 9/7/13
 * Time: 9:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class APIService  {

    public APIService() {
        // No instance variables necessary
    }

    // Takes a Map object and converts to json to send in POST request
    public static HttpResponse post(String uri, Map<String, String> mapData) {
//        Map<String, String> comment = new HashMap<String, String>();
//        mapData.put("subject", "Using the GSON library");
        String json = new GsonBuilder().create().toJson(mapData, Map.class);
        return makePostRequest(uri, json);
    }

    // Execute the POST request to the target uri
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