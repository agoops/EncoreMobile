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
import android.widget.ListView;

import com.encore.FriendRequestFragmentAdapter;
import com.encore.R;
import com.encore.API.APIService;
import com.encore.API.models.Profile;
import com.encore.API.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FriendRequestsFragment extends Fragment {

	private static String tag = "FriendRequestsFragment";
	private ListView listView;
	private FriendRequestFragmentAdapter adapter;
	private String JSON_REQUESTS_KEY = "pending_me";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.users_fragment, container,
				false);
		listView = (ListView) view.findViewById(R.id.users_list_view);

		
		adapter = new FriendRequestFragmentAdapter(container.getContext(), 0, new ArrayList<User>());
		listView.setAdapter(adapter);

		/* This is the receiver and method that hits the endpoint for list of friend reqeusts
		 */
		FriendRequestListReceiver mReceiver = new FriendRequestListReceiver(new Handler());
		getRequests(mReceiver);
		return view;
	}
	
	private void getRequests(ResultReceiver receiver) {
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		apiIntent.putExtra("receiver", receiver);
		apiIntent.putExtra(T.API_TYPE, T.FRIEND_REQUESTS_PENDING);
		
		getActivity().startService(apiIntent);
		return;
	}
	
	
	private class FriendRequestListReceiver extends ResultReceiver {
		
		public FriendRequestListReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == 1) {
				Log.d(tag, "APISerivce returned successful with friends");
				String result = resultData.getString("result");
				ArrayList<User> list = convertJsonToListOfUsers(result);

				adapter.setItemList(list);
				adapter.notifyDataSetChanged();
				Log.d(tag, "Pending friend requests: /n" +result);
			} else {
				Log.d(tag, "APIService get friends failed?");

			}
		}
		
	}
	
	private ArrayList<User> convertJsonToListOfUsers(String json) {
		ArrayList<User> users = new ArrayList<User>();
		
		Gson gson = new Gson();
		
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
	
	
}
