package com.encore.views;

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
import android.widget.ListView;

import com.encore.FriendsFragmentAdapter;
import com.encore.R;
import com.encore.API.APIService;
import com.encore.API.models.User;

public class FriendsFragment extends Fragment{

	
	/*
	 * Things that need to run in the background.
	 *       API call to get list of friends
	 *       Updating the UI with the friend xml
	 *       
	 *       TODO:
	 *       Maybe change this to a dialog box
	 *       make StartSession2 a FragmentActivity so it can launch FriendFragment easily
	 *       ping endpoint to get list of friends
	 *       Use gson correctly to turn this list of friends into list of User
	 */
	
	private static String tag = "FriendsFragment";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.friends_fragment, container, false);
		
	    ListView lv = (ListView) view.findViewById(R.id.friends_list_view);
	    
	    //List<SessionTemp> list = getFriends();
	    List<User> list = makeListOfFriends();
	    
	    List<User> friendList = getFriends();
	    
	    lv.setAdapter(new FriendsFragmentAdapter(container.getContext(), list));
	    return view;
    }
	
	public ArrayList<User> getFriends() {
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		FriendListReceiver mReceiver = new FriendListReceiver(new Handler());
		apiIntent.putExtra("receiver", mReceiver);
		apiIntent.putExtra(T.API_TYPE, T.FRIENDS);
		getActivity().startService(apiIntent);
		return null;
	}
	
	
	public ArrayList<User> makeListOfFriends() {
		ArrayList<User> list = new ArrayList<User>();
		for (int i =0; i < 4; ++i) {
			User user = new User();
			user.setUsername("yung$$ user "+ i);
			list.add(user); 
		}
		return list;
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
				

			}
			else {
				Log.d(tag, "APIService get friends failed?");
			
			}
		}
	}
}
