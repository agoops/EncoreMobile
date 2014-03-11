package com.encore.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.TabFriendsAdapter;
import com.encore.TabLikesAdapter;
import com.encore.TabRapsAdapter;
import com.encore.models.Clip;
import com.encore.models.Clips;
import com.encore.models.Friends;
import com.encore.models.Likes;
import com.encore.models.Profile;
import com.encore.models.Session;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by babakpourkazemi on 3/10/14.
 */
public class ProfileDetailsFragment extends Fragment {
    private static final String TAG = "ProfileDetailsFragment";
    private View view;
    private Context context;

    private ListView listview;
    private ProgressBar progressBar;

    private TabFriendsAdapter friendsAdapter;
    private TabLikesAdapter likesAdapter;
    private TabRapsAdapter rapsAdapter;

    private ArrayList<Profile> friendsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_details_fragment, container, false);
        context = getActivity();

        String infoType = getArguments().getString(T.PROFILE_INFO_TYPE);

        getViews();
        initListview(infoType);

        return view;
    }

    private void getViews() {
        listview = (ListView) view.findViewById(R.id.profile_info_listview);
        progressBar = (ProgressBar) view.findViewById(R.id.profile_details_progress_bar);
    }

    private void initListview(String infoType) {
        if(infoType.equals(T.PROFILE_INFO_RAPS)) {
            getRaps();
        } else if(infoType.equals(T.PROFILE_INFO_LIKES)) {
            getLikes();
        } else if(infoType.equals(T.PROFILE_INFO_FRIENDS)) {
            // TODO: Change to requests
            getFriends();
        }
    }

    private void getRaps() {
        Log.d(TAG, "getLikes");

        Intent api = new Intent(getActivity(), APIService.class);
        RapsReceiver receiver = new RapsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_CLIPS);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    private void getLikes() {
        Log.d(TAG, "getLikes");

        Intent api = new Intent(getActivity(), APIService.class);
        LikesReceiver receiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    private void getFriends() {
        Log.d(TAG, "getFriends");

        Intent api = new Intent(getActivity(), APIService.class);
        ResultReceiver receiver = new FriendsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_FRIENDS);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    public double getScreenWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public class RapsReceiver extends ResultReceiver {
        public RapsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Convert the api request from JSON
                Log.d(TAG, "APIService returned with raps");
                String result = data.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                Clips clips = (new Gson()).fromJson(result, Clips.class);
                ArrayList<Clip> clipsList = clips.getClips();

                // Update the data on our listview
                rapsAdapter = new TabRapsAdapter(getActivity(), R.layout.tab_raps_list_row, null);
                listview.setAdapter(rapsAdapter);
                rapsAdapter.setThumbnailWidth(getScreenWidth());
                rapsAdapter.setItemList(clipsList);
                rapsAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "GET raps unsuccessful");
            }
        }
    }

    public class FriendsReceiver extends ResultReceiver {
        public FriendsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Convert the api request from JSON
                Log.d(TAG, "APIService returned successful with friends");
                String result = data.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                Friends friends = (new Gson()).fromJson(result, Friends.class);
                Profile[] friendsArray = friends.getFriends();

                // Update the data on our listview
                friendsAdapter = new TabFriendsAdapter(getActivity(), R.layout.tab_friends_list_row, null);
                listview.setAdapter(friendsAdapter);
                friendsList = new ArrayList<Profile>(Arrays.asList(friendsArray));
                friendsAdapter.setItemList(friendsList);
                friendsAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }

    private class LikesReceiver extends ResultReceiver {
        public LikesReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 1) {
                Log.d(TAG, "APIService returned successful with likes");
                String result = resultData.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                ArrayList<Session> likedSessions = new Gson().fromJson(result, Likes.class)
                        .getLikedSessionsList();

                // Update the data on our listview
                likesAdapter = new TabLikesAdapter(getActivity(), R.layout.tab_likes_list_row, null);
                listview.setAdapter(likesAdapter);
                likesAdapter.setItemList(likedSessions);
                likesAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "APIService get session clip url failed?");
            }
        }
    }
}
