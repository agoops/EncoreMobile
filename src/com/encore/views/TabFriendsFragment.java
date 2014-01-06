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
import com.encore.R;
import com.encore.TabFriendsAdapter;
import com.encore.models.Friends;
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabFriendsFragment extends Fragment {
    private static final String TAG = "TabFriendsFragment";
    private ListView myFriendsLv;
    private TabFriendsAdapter adapter;
    private FriendsReceiver receiver;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_friends, null, false);

        getViews();
        setupAdapter();
        getFriends();

        return view;
    }

    public void getViews() {
        myFriendsLv = (ListView) view.findViewById(R.id.tab_friends_lv);
    }

    public void setupAdapter() {
        adapter = new TabFriendsAdapter(getActivity(), R.layout.tab_friends_list_row, new ArrayList<Profile>());
        myFriendsLv.setAdapter(adapter);
    }

    public void getFriends() {
        Log.d(TAG, "Making request to get friends");
        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new FriendsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_FRIENDS);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    public class FriendsReceiver extends ResultReceiver {
        public FriendsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Populate views with our data
                Log.d(TAG, "Friends received successfully");
                String result = data.getString("result");

                Friends friends = (new Gson()).fromJson(result, Friends.class);
                Profile[] friendsArray = friends.getFriends();

                Log.d(TAG, "Assigning adapter now");
//                myFriendsLv.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
                adapter.setItemList(new ArrayList<Profile>(Arrays.asList(friendsArray)));
                adapter.notifyDataSetChanged();

                // Hide progress bar
//				progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }
}
