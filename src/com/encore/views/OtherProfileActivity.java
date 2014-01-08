package com.encore.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.TabCrowdAdapter;
import com.encore.TabFriendsAdapter;
import com.encore.TabLikesAdapter;
import com.encore.models.Crowd;
import com.encore.models.Crowds;
import com.encore.models.Friends;
import com.encore.models.Likes;
import com.encore.models.Profile;
import com.encore.models.Session;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by babakpourkazemi on 1/7/14.
 */
public class OtherProfileActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "OtherProfileActivity";
    private Context context;
    private ImageView profilePicture;
    private TextView username, fullName, numRaps, numLikes, numFriends;
    private Button addFriendButton, friendsButton, crowdsButton, likesButton;
    private ListView listview;
    private ProgressBar otherProgress, otherTabsProgress;
    
    private ResultReceiver receiver;
    private TabFriendsAdapter friendsAdapter;
    private TabCrowdAdapter crowdsAdapter;
    private TabLikesAdapter likesAdapter;

    // TODO: Change the friends tab to raps tab

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile_activity);
        context = this;

        setupActionBar();
        getViews();
        setOnClickListeners();
        initData();
    }
    
    private void getViews() {
        profilePicture = (ImageView) findViewById(R.id.other_profile_picture);
        username = (TextView) findViewById(R.id.other_username);
        fullName = (TextView) findViewById(R.id.other_fullname);
        numRaps = (TextView) findViewById(R.id.otherNumRapsTv);
        numLikes = (TextView) findViewById(R.id.otherNumLikesTv);
        numFriends = (TextView) findViewById(R.id.otherNumFriendsTv);
        addFriendButton = (Button) findViewById(R.id.other_add_friend_button);
        friendsButton = (Button) findViewById(R.id.other_friends_button);
        crowdsButton = (Button) findViewById(R.id.other_crowds_button);
        likesButton = (Button) findViewById(R.id.other_likes_button);
        listview = (ListView) findViewById(R.id.other_tabs_lv);
        otherProgress = (ProgressBar) findViewById(R.id.progress_other);
        otherTabsProgress = (ProgressBar) findViewById(R.id.progress_other_tabs);
    }
    
    private void setOnClickListeners() {
        addFriendButton.setOnClickListener(this);
        friendsButton.setOnClickListener(this);
        crowdsButton.setOnClickListener(this);
        likesButton.setOnClickListener(this);
    }
    
    // Get the user's profile information
    private void initData() {
        getProfileInfo();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.other_add_friend_button:
                break;
            case R.id.other_friends_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(1);
                break;
            case R.id.other_crowds_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(2);
                break;
            case R.id.other_likes_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(3);
                break;
            default:
                break;
        }
    }

    // Enable up navigation
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                // Needed for proper UP navigation
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Our api requests
    private void getProfileInfo() {
        // Make an api call to get the user's information
    }

    private void getFriends() {
        Log.d(TAG, "Making request to get friends");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(this, APIService.class);
//        receiver = new ProfileFragment.FriendsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_FRIENDS);
        api.putExtra(T.RECEIVER, receiver);
        this.startService(api);
    }

    private void getCrowds() {
        Log.d(TAG, "Making request to get crowds");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(this, APIService.class);
//        receiver = new ProfileFragment.CrowdReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_CROWDS);
        api.putExtra(T.RECEIVER, receiver);
        this.startService(api);
    }

    private void getLikes() {
        Log.d(TAG, "Making request to get likes");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(this, APIService.class);
//        LikesReceiver receiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, receiver);
        this.startService(api);
    }

    // To visually simulate tabs, we "select" the given tab number
    public void setTabPressed(int tabNumber) {
        friendsButton.setSelected((tabNumber == 1));
        crowdsButton.setSelected((tabNumber == 2));
        likesButton.setSelected((tabNumber == 3));
    }

    private void setTabVisibility(int type) {
        // Set the visibility of a group of views
        if(type == 0) {
            otherTabsProgress.setVisibility(View.VISIBLE);
            listview.setVisibility(View.INVISIBLE);
        } else if(type == 1) {
            otherTabsProgress.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
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
//                friendsAdapter.notifyDataSetChanged();


                // Update the data on our listview
                friendsAdapter = new TabFriendsAdapter(context, R.layout.tab_friends_list_row, null);
                listview.setAdapter(friendsAdapter);
                friendsAdapter.setItemList(new ArrayList<Profile>(Arrays.asList(friendsArray)));
                friendsAdapter.notifyDataSetChanged();
                listview.performClick();

//                listview.invalidateViews();
//                simulateTouch(listview);

                // Show data
                setTabVisibility(1);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }

    public class CrowdReceiver extends ResultReceiver {
        public CrowdReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == T.GET_CROWDS) {
                // Convert the api request from JSON
                Log.d(TAG, "APIService returned successful with crowds");
                String result = resultData.getString("crowdsJson");

                Log.d(TAG, "result from apiservice is: " + result);
                Crowd[] temp = new Gson().fromJson(result, Crowds.class).getCrowds();
                ArrayList<Crowd> crowds = new ArrayList<Crowd>(Arrays.asList(temp));

                // Update the data on our listview
                crowdsAdapter = new TabCrowdAdapter(context, R.layout.tab_crowd_list_row, null);
                listview.setAdapter(crowdsAdapter);
                crowdsAdapter.setItemList(crowds);
                crowdsAdapter.notifyDataSetChanged();
                listview.performClick();

                // Show data
                setTabVisibility(1);
            }
            else {
                Log.d(TAG, "getCrowds() failed");
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
                // Convert the api request from JSON
                String result = resultData.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                ArrayList<Session> likedSessions = new Gson().fromJson(result, Likes.class)
                        .getLikedSessionsList();

                // Update the data on our listview
                likesAdapter = new TabLikesAdapter(context, R.layout.tab_likes_list_row, null);
                listview.setAdapter(likesAdapter);
                likesAdapter.setItemList(likedSessions);
                likesAdapter.notifyDataSetChanged();
                listview.performClick();

                // For whatever reason, the listview won't update unless touched, so we simulate one
//                listview.invalidateViews();
//                simulateTouch(listview);

                // Show data
                setTabVisibility(1);
            } else {
                Log.d(TAG, "APIService get session clip url failed?");
            }
        }
    }
}
