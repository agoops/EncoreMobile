package com.encore.Fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.TabCrowdAdapter;
import com.encore.TabFriendsAdapter;
import com.encore.TabLikesAdapter;
import com.encore.TabRequestsAdapter;
import com.encore.models.Crowd;
import com.encore.models.Crowds;
import com.encore.models.FriendRequest;
import com.encore.models.FriendRequestProfile;
import com.encore.models.Friends;
import com.encore.models.Likes;
import com.encore.models.Profile;
import com.encore.models.Session;
import com.encore.util.T;
import com.encore.views.EditProfileActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileFragment extends Fragment implements View.OnClickListener{
	private static final String TAG = "ProfileFragment";
    private View view;
    private Button rapsButton, crowdsButton, likesButton, requestsButton, editProfileButton;
    private ListView listview;
    private TextView numRapsTv, numLikesTv, numFriendsTv;
    private ProgressBar progressProfile, progressTabs;
    private TabFriendsAdapter friendsAdapter;
    private TabCrowdAdapter crowdsAdapter;
    private TabLikesAdapter likesAdapter;
    private TabRequestsAdapter requestsAdapter;
    private ResultReceiver receiver;

    private Profile userMe;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.profile_fragment, container, false);

        getViews();
        setOnClickListeners();
        initData();

		return view;
	}

    private void getViews() {
        rapsButton = (Button) view.findViewById(R.id.profile_raps_button);
        crowdsButton = (Button) view.findViewById(R.id.profile_crowds_button);
        likesButton = (Button) view.findViewById(R.id.profile_likes_button);
        requestsButton = (Button) view.findViewById(R.id.profile_requests_button);
        editProfileButton = (Button) view.findViewById(R.id.profileEditButton);
        listview = (ListView) view.findViewById(R.id.tabsLv);
        numRapsTv = (TextView) view.findViewById(R.id.profileNumRapsTv);
        numLikesTv = (TextView) view.findViewById(R.id.profileNumLikesTv);
        numFriendsTv = (TextView) view.findViewById(R.id.profileNumFriendsTv);
        progressProfile = (ProgressBar) view.findViewById(R.id.progress_profile);
        progressTabs = (ProgressBar) view.findViewById(R.id.progress_profile_tabs);

        // Hide everything until we get some data
        setProfileVisibility(0);
    }

    private void setOnClickListeners() {
        rapsButton.setOnClickListener(this);
        crowdsButton.setOnClickListener(this);
        likesButton.setOnClickListener(this);
        requestsButton.setOnClickListener(this);
        editProfileButton.setOnClickListener(this);
    }

    public void initData() {
        // Populate the profile page's basic data
        getMe();

        // By default, the first tab will be selected
        setTabPressed(1);

        // The default first tab means we want to pull in our friends
        getFriends();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.profile_raps_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(1);
                getFriends();

                break;
            case R.id.profile_crowds_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(2);
                getCrowds();

                break;
            case R.id.profile_likes_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(3);
                getLikes();

                break;
            case R.id.profile_requests_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(4);
                getRequests();

                break;
            case R.id.profileEditButton:
                Intent launchEditProfileActivity = new Intent(getActivity(), EditProfileActivity.class);
                launchEditProfileActivity.putExtra(T.FIRST_NAME, userMe.getFirstName());
                launchEditProfileActivity.putExtra(T.LAST_NAME, userMe.getLastName());
                launchEditProfileActivity.putExtra(T.EMAIL, userMe.getEmail());
                launchEditProfileActivity.putExtra(T.PHONE_NUMBER, userMe.getPhoneNumber());
                startActivity(launchEditProfileActivity);

                break;
            default:
                break;
        }
    }

    // API requests
    private void getMe() {
        Log.d(TAG, "Making request to users/me");

        // Hide everything until we get some data
        setProfileVisibility(0);

        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new MeReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_ME);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    private void getFriends() {
        Log.d(TAG, "Making request to get friends");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new FriendsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_FRIENDS);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    private void getCrowds() {
        Log.d(TAG, "Making request to get crowds");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new CrowdReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_CROWDS);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    private void getLikes() {
        Log.d(TAG, "Making request to get likes");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(getActivity(), APIService.class);
        LikesReceiver receiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    private void getRequests() {
        Log.d(TAG, "Making request to get friend requests");

        // Hide the tabs until we get some data
        setTabVisibility(0);

        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new RequestsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.FRIEND_REQUESTS_PENDING);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    // To visually simulate tabs, we "select" the given tab number
    public void setTabPressed(int tabNumber) {
        rapsButton.setSelected((tabNumber == 1));
        crowdsButton.setSelected((tabNumber == 2));
        likesButton.setSelected((tabNumber == 3));
        requestsButton.setSelected((tabNumber == 4));
    }

    private void setProfileVisibility(int type) {
        // Set the visibility of a group of views
        if(type == 0) {
            progressProfile.setVisibility(View.VISIBLE);
        } else if(type == 1) {
            progressProfile.setVisibility(View.GONE);
        }
    }

    private void setTabVisibility(int type) {
        // Set the visibility of a group of views
        if(type == 0) {
            progressTabs.setVisibility(View.VISIBLE);
            listview.setVisibility(View.INVISIBLE);
        } else if(type == 1) {
            progressTabs.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }
    }

    private void simulateTouch(View v) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 200f;
        float y = 200f;
        Toast.makeText(getActivity(), "(x, y): " + x + ", " + y, Toast.LENGTH_SHORT).show();
        // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;

        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );


        // Dispatch touch event to view
        v.dispatchTouchEvent(motionEvent);
    }

    // Our receivers
    public class MeReceiver extends ResultReceiver {
        public MeReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Convert the api request from JSON
                Log.d(TAG, "APIService returned successful with users/me");
                String result = data.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                userMe = new Gson().fromJson(result, Profile.class);

                int numRaps = userMe.getNumRaps();
                int numLikes = userMe.getNumLikes();
                int numFriends = userMe.getNumFriends();

                numRapsTv.setText(
                        (numRaps == 1) ? numRaps + " Rap" : numRaps + " Raps");
                numLikesTv.setText(
                        (numLikes == 1) ? numLikes + " Like" : numLikes + " Likes");
                numFriendsTv.setText(
                        (numFriends == 1) ? numFriends + " Friend" : numFriends + " Friends");

                // Show data
                setProfileVisibility(1);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
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
//                friendsAdapter.notifyDataSetChanged();


                // Update the data on our listview
                friendsAdapter = new TabFriendsAdapter(getActivity(), R.layout.tab_friends_list_row, null);
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
                crowdsAdapter = new TabCrowdAdapter(getActivity(), R.layout.tab_crowd_list_row, null);
                listview.setAdapter(crowdsAdapter);
                crowdsAdapter.setItemList(crowds);
                crowdsAdapter.notifyDataSetChanged();
                listview.performClick();

//                listview.invalidateViews();
//                simulateTouch(listview);

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
                likesAdapter = new TabLikesAdapter(getActivity(), R.layout.tab_likes_list_row, null);
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

    public class RequestsReceiver extends ResultReceiver {
        public RequestsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                Log.d(TAG, "APIService returned successful with friend requests");
                String result = data.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                FriendRequest requests = (new Gson()).fromJson(result, FriendRequest.class);
                FriendRequestProfile[] pendingMe  = requests.getPendingMe();

                // Update the data on our listview
                requestsAdapter = new TabRequestsAdapter(getActivity(), R.layout.tab_requests_list_row, null);
                listview.setAdapter(requestsAdapter);
                requestsAdapter.setItemList(new ArrayList<FriendRequestProfile>(Arrays.asList(pendingMe)));
                requestsAdapter.notifyDataSetChanged();
                listview.performClick();


                // For whatever reason, the listview won't update unless touched, so we simulate one
//                listview.invalidateViews();
//                simulateTouch(listview);

                // Show data
                setTabVisibility(1);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }
	
}
