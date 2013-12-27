package com.encore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.FriendsFragmentAdapter;
import com.encore.R;
import com.encore.models.Friends;
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CreateCrowdFragment extends Fragment implements OnClickListener {
	private static final String TAG = "CreateCrowdFragment";
	private EditText crowdTitleET;
	private ListView listview;
	private FriendsFragmentAdapter adapter;
    private ArrayList<Profile> friends;
	private int[] selectedFriends;
    private APIService api;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.create_crowd_fragment, container, false);
		crowdTitleET = (EditText) v.findViewById(R.id.crowdTitleET);
		listview = (ListView) v.findViewById(R.id.chooseFriendsLV);
		
		// Set up the listview
		adapter = new FriendsFragmentAdapter(getActivity(), 0, null);
		listview.setAdapter(adapter);
		
		// Get the user's friends
		FriendListReceiver receiver = new FriendListReceiver(new Handler());
		
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		apiIntent.putExtra("receiver", receiver);
		apiIntent.putExtra(T.API_TYPE, T.FRIENDS);
		getActivity().startService(apiIntent);
		
		// Click listeners
		((Button) v.findViewById(R.id.createCrowdBtn))
			.setOnClickListener((OnClickListener) this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.createCrowdBtn:
			// Get the String array of selected usernames to add as crowd members
			Set<Integer> selectedFriendsSet = adapter.getSelectedFriends();
            List<Integer> selectedFriendsList = new ArrayList<Integer>(selectedFriendsSet);

			selectedFriends = new int[selectedFriendsList.size()];
			for(int i=0; i<selectedFriendsList.size(); i++) {
				selectedFriends[i] = selectedFriendsList.get(i);
			}
			
			String crowdTitle = crowdTitleET.getText().toString();
			if(crowdTitle == null || crowdTitle.equals("")) {
				Toast.makeText(getActivity(), "Forgetting a title?", Toast.LENGTH_LONG).show();
			} else if(selectedFriendsList.size() <= 0) { 
				Toast.makeText(getActivity(), "Yo! Pick some friends.", Toast.LENGTH_LONG).show();
			} else {
				Intent apiIntent = new Intent(getActivity(), APIService.class);
				apiIntent.putExtra(T.API_TYPE, T.CREATE_CROWD);
				apiIntent.putExtra(T.CROWD_TITLE, crowdTitle);
				apiIntent.putExtra(T.CROWD_MEMBERS, selectedFriends);
				getActivity().startService(apiIntent);
				
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				PickCrowdFragment pickCrowdFragment = new PickCrowdFragment();
				
				// Note: we're not adding this fragment to the backstack!
				ft.replace(R.id.fragment_placeholder, pickCrowdFragment);
				ft.commit();
			}
			break;
		default:
			break;
		}
	}
	
	private class FriendListReceiver extends ResultReceiver {
		public FriendListReceiver(Handler handler) {
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
                Profile[] temp = (new Gson()).fromJson(result, Friends.class).getFriends();

                friends = new ArrayList<Profile>(Arrays.asList(temp));

				adapter.setItemList(friends);
				adapter.notifyDataSetChanged();
			} else {
				Log.d(TAG, "APIService get friends failed?");

			}
		}
	}
	
//	// TODO: Use GSON instead?
//	public ArrayList<User> convertJsonToListOfUser(String json) {
//		Log.d(TAG, "CONVERT TO OBJECT STARTED");
//		Gson gson = new Gson();
//		ArrayList<User> profiles = new ArrayList<User>();
//		JsonParser jsonParser = new JsonParser();
//
//		JsonArray profilesJson = jsonParser.parse(json).getAsJsonObject()
//				.getAsJsonArray("friends");
//
//		for (JsonElement j : profilesJson) {
//			User profile = gson.fromJson(j, User.class);
//			profiles.add(profile);
//			Log.d(TAG, profile.toString());
//		}
//
//		return profiles;
//	}

}
