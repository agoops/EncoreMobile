package com.encore.views;

import java.util.ArrayList;
import java.util.Arrays;

import com.encore.util.T;
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

import com.encore.FriendRequestFragmentAdapter;
import com.encore.R;
import com.encore.API.APIService;
import com.encore.models.FriendRequest;
import com.encore.models.User;
import com.google.gson.Gson;

public class FriendRequestsFragment extends Fragment {

	private static final String TAG = "FriendRequestsFragment";
	private ListView listView;
	private FriendRequestFragmentAdapter adapter;
	private String JSON_REQUESTS_KEY = "pending_me";
	private ProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.users_fragment, container,
				false);
		
		progressBar = (ProgressBar) view.findViewById(R.id.progress_users);
		progressBar.setVisibility(View.VISIBLE);
		
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
				Log.d(TAG, "APIService returned successful with friends");
				String result = resultData.getString("result");
				
				progressBar.setVisibility(View.GONE);
				
				FriendRequest fq = new Gson().fromJson(result, FriendRequest.class);
				ArrayList<User> pendingMe = new ArrayList(Arrays.asList(fq.getPendingMe())); 
				Log.d(TAG, "FRIENDREQUEST TOSTRING" + fq.toString());

				adapter.setItemList(pendingMe);
				adapter.notifyDataSetChanged();
			} else {
				Log.d(TAG, "APIService get friends failed?");
			}
		}
		
	}
}
