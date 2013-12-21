package Fragments;

import java.util.ArrayList;
import java.util.Arrays;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.encore.R;
import com.encore.UsersFragmentAdapter;
import com.encore.API.APIService;
import com.encore.API.models.Profile;
import com.google.gson.Gson;

public class ProfileFragment extends Fragment {
	private static final String TAG = "ProfileFragment";
	private TextView fullName, username, email;
	private ListView friendsLv;
	private UsersFragmentAdapter adapter;
	private ProfileReceiver receiver;
	private ProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.profile_fragment, container, false);
		
		// Show progress bar
		progressBar = (ProgressBar) v.findViewById(R.id.progress_profile);
		progressBar.setVisibility(View.VISIBLE);
		
		// Get views
		fullName = (TextView) v.findViewById(R.id.profile_name);
		username = (TextView) v.findViewById(R.id.profile_username);
		email = (TextView) v.findViewById(R.id.profile_email);
		friendsLv = (ListView) v.findViewById(R.id.profile_friends_lv);
		
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
				
				fullName.setText(userMe.getFullName());
				username.setText(userMe.getUsername());
				email.setText(userMe.getEmail());
				
				// Hide progress bar
				progressBar.setVisibility(View.GONE);
			} else {
				Log.d(TAG, "Profile api call unsuccessfull");
			}
		}
	}
}
