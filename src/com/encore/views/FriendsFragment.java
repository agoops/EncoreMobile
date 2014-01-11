package com.encore.views;

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

import com.encore.API.APIService;
import com.encore.FriendsFragmentAdapter;
import com.encore.R;
import com.encore.models.Friends;
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendsFragment extends Fragment {


	private static final String TAG = "FriendsFragment";
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
		apiIntent.putExtra(T.API_TYPE, T.GET_FRIENDS);
		
		getActivity().startService(apiIntent);
		return;
	}

    private class FriendListReceiver extends ResultReceiver {
        public FriendListReceiver(Handler handler) {
            super(handler);
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
                Profile[] temp = (new Gson()).fromJson(result, Friends.class).getFriends();

                ArrayList<Profile> friends = new ArrayList<Profile>(Arrays.asList(temp));

                adapter.setItemList(friends);
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "APIService get friends failed?");

            }
        }
    }

//	private class FriendListReceiver extends ResultReceiver {
//		public FriendListReceiver(Handler handler) {
//			super(handler);
//		}
//
//		@Override
//		protected void onReceiveResult(int resultCode, Bundle resultData) {
//			if (resultCode == 1) {
//				Log.d(tag, "APIService returned successful with friends");
//				/*
//				 * TODO:WARNING This conversion of json to List<Profile> might
//				 * be running on UI thread and that might be bad. EDIT:
//				 * http://stackoverflow
//				 * .com/questions/13178075/broadcast-receiver
//				 * -and-resultreceiver-in-android according to that ^ link, it
//				 * seems that the code will run on the service's thread (given
//				 * that it is seperate), and update UI with Handler given.
//				 */
//				String result = resultData.getString("result");
//				ArrayList<Profile> profiles = convertJsonToListOfUser(result);
//				adapter.setItemList(profiles);
//				adapter.notifyDataSetChanged();
//			} else {
//				Log.d(tag, "APIService get friends failed?");
//
//			}
//		}
//	}

//	public ArrayList<Profile> convertJsonToListOfUser(String json) {
//		Log.d(TAG, "CONVERT TO OBJECT STARTED");
//		Gson gson = new Gson();
//
//		ArrayList<Profile> profiles = new ArrayList<Profile>();
//		JsonParser jsonParser = new JsonParser();
//
//		JsonArray profilesJson = jsonParser.parse(json).getAsJsonObject()
//				.getAsJsonArray("friends");
//
//		for (JsonElement j : profilesJson) {
//			Profile profile = gson.fromJson(j, Profile.class);
//			profiles.add(profile);
//			Log.d(TAG, profile.toString());
//		}
//
//		return profiles;
//	}
}
