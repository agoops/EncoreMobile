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

import com.encore.FriendsFragmentAdapter;
import com.encore.R;
import com.encore.API.APIService;
import com.encore.API.models.Profile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FriendsFragment extends Fragment {


	private static String tag = "FriendsFragment";
	private ListView listView;
	FriendsFragmentAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.friends_fragment, container,
				false);
		listView = (ListView) view.findViewById(R.id.friends_list_view);

		
		adapter = new FriendsFragmentAdapter(container.getContext(), 0, null);
		listView.setAdapter(adapter);
		FriendListReceiver mReceiver = new FriendListReceiver(new Handler());

		/* This is the receiver and method that hits the endpoint for list of friends
		 */
		getFriends(mReceiver);
		return view;
	}

	public void getFriends(ResultReceiver receiver) {
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		apiIntent.putExtra("receiver", receiver);
		apiIntent.putExtra(T.API_TYPE, T.FRIENDS);
		
		getActivity().startService(apiIntent);
		return;
	}

	private class FriendListReceiver extends ResultReceiver {
		public FriendListReceiver(Handler handler) {
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

	public ArrayList<Profile> convertJsonToListOfProfile(String json) {
		Log.d(tag, "CONVERT TO OBJECT STARTED");
		Gson gson = new Gson();
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		JsonParser jsonParser = new JsonParser();

		JsonArray profilesJson = jsonParser.parse(json).getAsJsonObject()
				.getAsJsonArray("friends");

		for (JsonElement j : profilesJson) {
			Profile profile = gson.fromJson(j, Profile.class);
			profiles.add(profile);
			Log.d(tag, profile.toString());
		}

		return profiles;
	}
	


//	public ArrayList<User> makeListOfFriends() {
//		ArrayList<User> list = new ArrayList<User>();
//		for (int i =0; i < 4; ++i) {
//			User user = new User();
//			user.setUsername("yung$$ user "+ i);
//			list.add(user); 
//		}
//		return list;
//	}
	
}
