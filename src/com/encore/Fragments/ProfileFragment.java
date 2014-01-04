package com.encore.Fragments;

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
import android.widget.TabHost;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.UsersFragmentAdapter;
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileFragment extends Fragment {
	private static final String TAG = "ProfileFragment";
	private ListView friendsLv;
	private UsersFragmentAdapter adapter;
	private ProfileReceiver receiver;
//	private ProgressBar progressBar;
    private TextView numRapsTv, numCrowdsTv, numFriendsTv;
    private TabHost tabHost;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.profile_fragment, container, false);
		
		// Show progress bar
//		progressBar = (ProgressBar) v.findViewById(R.id.progress_profile);
//		progressBar.setVisibility(View.VISIBLE);
		
		// Get views
		friendsLv = (ListView) v.findViewById(R.id.profile_friends_lv);
        numRapsTv = (TextView) v.findViewById(R.id.profileNumRapsTv);
        numCrowdsTv = (TextView) v.findViewById(R.id.profileNumCrowdsTv);
        numFriendsTv = (TextView) v.findViewById(R.id.profileNumFriendsTv);
        tabHost = (TabHost) v.findViewById(R.id.profileTabHost);

        // Setup tabs
        tabHost.setup();
        TabHost.TabSpec spec1 = tabHost.newTabSpec("tag1");
        spec1.setIndicator("Friends");
        spec1.setContent(R.id.tab1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("tag2");
        spec2.setIndicator("Likes");
        spec2.setContent(R.id.tab2);

        TabHost.TabSpec spec3 = tabHost.newTabSpec("tag3");
        spec3.setIndicator("Crowds");
        spec3.setContent(R.id.tab3);

        TabHost.TabSpec spec4 = tabHost.newTabSpec("tag4");
        spec4.setIndicator("Requests");
        spec4.setContent(R.id.tab4);

        // Add TabSpec to TabHost to display
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);

		adapter = new UsersFragmentAdapter(getActivity(), 0, null);
		friendsLv.setAdapter(adapter);
		
		// Make GET request
		receiver = new ProfileReceiver(new Handler());
		getProfile(receiver);
		
		return v;
	}
	
	public void getProfile(ProfileReceiver receiver) {
		Intent api = new Intent(getActivity(), APIService.class);
		api.putExtra(T.API_TYPE, T.GET_ME);
		api.putExtra(T.RECEIVER, receiver);
		getActivity().startService(api);
	}
	
	public class ProfileReceiver extends ResultReceiver {
		public ProfileReceiver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onReceiveResult(int resultCode, Bundle data) {
			if(resultCode == 1) {
				// Populate views with our data
				Log.d(TAG, "Profile results received successfully");
				String result = data.getString("result");
				
				Profile userMe = (new Gson()).fromJson(result, Profile.class);
				Profile[] userMeFriends = userMe.getFriends();
				
				adapter.setItemList(new ArrayList<Profile>(Arrays.asList(userMeFriends)));
				adapter.notifyDataSetChanged();
				
				// Hide progress bar
//				progressBar.setVisibility(View.GONE);
			} else {
				Log.d(TAG, "Profile api call unsuccessfull");
			}
		}
	}
}
