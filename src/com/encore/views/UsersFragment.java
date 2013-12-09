package com.encore.views;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
import com.google.gson.reflect.TypeToken;

public class UsersFragment extends Fragment{
	private static final String TAG = "UsersFragment";
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

	private class UsersListReceiver extends ResultReceiver {
		public UsersListReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == 1) {
				Log.d(TAG, "APIService returned successful with friends");
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
				Log.d(TAG, "Friends: \n" + result);
				Type listType = new TypeToken<List<Profile>>(){}.getType();
				ArrayList<Profile> profiles2 = (new Gson()).fromJson(result, listType);
				Log.d(TAG, "Num user: " + profiles2.size() + ", 0: " + profiles2.get(0).getFirstName());
				
				ArrayList<Profile> profiles = convertJsonToListOfUser(result);
				adapter.setItemList(profiles);
				adapter.notifyDataSetChanged();
			} else {
				Log.d(TAG, "APIService get friends failed?");
			}
		}
	}
	
	public ArrayList<Profile> convertJsonToListOfUser(String json) {
		Log.d(TAG, "CONVERT TO OBJECT STARTED");
		Gson gson = new Gson();
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		
		JsonParser jsonParser = new JsonParser();
		JsonArray usersJson = new JsonArray();
		
		usersJson = jsonParser.parse(json).getAsJsonArray();
		for (JsonElement j : usersJson) {
			Profile profile = gson.fromJson(j, Profile.class);
			profiles.add(profile);
			Log.d(TAG, profile.toString());
		}
		return profiles;
		
	}
	public ArrayList<Profile> convertJsonToListOfProfile(String json) {
		Log.d(TAG, "CONVERT TO OBJECT STARTED");
		Gson gson = new Gson();
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		JsonParser jsonParser = new JsonParser();

		JsonArray profilesJson = new JsonArray();

		profilesJson = jsonParser.parse(json).getAsJsonArray();
		for (JsonElement j : profilesJson) {
			Profile profile = gson.fromJson(j, Profile.class);
			profiles.add(profile);
			Log.d(TAG, profile.toString());
		}

		return profiles;
	}
}
