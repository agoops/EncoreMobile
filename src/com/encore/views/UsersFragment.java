package com.encore.views;

import java.util.ArrayList;

import util.T;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.encore.R;
import com.encore.UsersFragmentAdapter;
import com.encore.API.APIService;
import com.encore.API.models.Profile;
import com.encore.API.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class UsersFragment extends Fragment{
	private static String tag = "UsersFragment";
	private ListView listView;
	public Button sendRequests;
	UsersFragmentAdapter adapter;
	
	private final static int REQUESTS = 0;
	private final static int USERS = 1;
	private final static String JSON_REQUESTS_KEY = "pending_me";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.users_fragment, container,
				false); 
		listView = (ListView) view.findViewById(R.id.users_list_view);
		adapter = new UsersFragmentAdapter(container.getContext(), 0, null);
		listView.setAdapter(adapter);
		
		
		UsersListReceiver uReceiver = new UsersListReceiver(new Handler());
		/* This is the receiver and method that hits the endpoint for list of friends
		 */
		getUsers(uReceiver);
		
		RequestsListReceiver rReceiver = new RequestsListReceiver(new Handler());
		getPendingFriendRequests(rReceiver);
		
		return view;
	}
	
	public void getPendingFriendRequests(ResultReceiver receiver) {
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		apiIntent.putExtra("receiver", receiver);
		apiIntent.putExtra(T.API_TYPE, T.FRIEND_REQUESTS_PENDING);
		
		getActivity().startService(apiIntent);
		return;
	}
	
	
	public void getUsers(ResultReceiver receiver) {
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		apiIntent.putExtra("receiver", receiver);
		apiIntent.putExtra(T.API_TYPE, T.USERS);
		
		getActivity().startService(apiIntent);
		return;
	}
	
	private class RequestsListReceiver extends ResultReceiver {
		public RequestsListReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == 1) {
				Log.d(tag, "APISerivce returned successful with pending requests");
				
				String result = resultData.getString("result");
				Log.d(tag, "Response for pending requests: " + result);
				ArrayList<User> profiles = convertJsonToListOfUser(result);
				
			} else {
				Log.d(tag, "APIService get friends failed?");

			}
		}
	}
	private class UsersListReceiver extends ResultReceiver {
		public UsersListReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == 1) {
				Log.d(tag, "APISerivce returned successful with friends");
				/*
				 * TODO:WARNING This conversion of json to List<Profile> might
				 * be running on UI thread and that might be bad. EDIT:
				 * http://stackoverflow
				 * .com/questions/13178075/broadcast-receiver
				 * -and-resultreceiver-in-android according to that ^ link, it
				 * seems that the code will run on the service's thread (given
				 * that it is seperate), and update UI with Handler given.
				 */
				String result = resultData.getString("result");
				ArrayList<Profile> profiles = convertJsonToListOfProfile(result);
				adapter.setItemList(profiles);
				adapter.notifyDataSetChanged();
			} else {
				Log.d(tag, "APIService get friends failed?");

			}
		}
	}
	
	public ArrayList<User> convertJsonToListOfUser(String json) {
		Log.d(tag, "CONVERT TO OBJECT STARTED");
		Gson gson = new Gson();
		ArrayList<User> users = new ArrayList<User>();
		JsonParser jsonParser = new JsonParser();
		
		JsonArray usersJson = new JsonArray();
		
		usersJson = jsonParser.parse(json).getAsJsonObject()
				.getAsJsonArray(JSON_REQUESTS_KEY);
		for (JsonElement j : usersJson) {
			User user = gson.fromJson(j, User.class);
			users.add(user);
			Log.d(tag, user.toString());
		}
		return users;
		
	}
	public ArrayList<Profile> convertJsonToListOfProfile(String json) {
		Log.d(tag, "CONVERT TO OBJECT STARTED");
		Gson gson = new Gson();
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		JsonParser jsonParser = new JsonParser();

		JsonArray profilesJson = new JsonArray();

		profilesJson = jsonParser.parse(json).getAsJsonArray();
		for (JsonElement j : profilesJson) {
			Profile profile = gson.fromJson(j, Profile.class);
			profiles.add(profile);
			Log.d(tag, profile.toString());
		}

		return profiles;
	}
}
