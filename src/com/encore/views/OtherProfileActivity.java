package com.encore.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.TabFriendsAdapter;
import com.encore.models.FriendRequest;
import com.encore.models.OtherProfile;
import com.encore.models.Profile;
import com.encore.models.Session;
import com.encore.util.T;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/7/14.
 */
public class OtherProfileActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "OtherProfileActivity";
    private Context context;
    private ImageView profilePicture;
    private TextView username, fullName, numRapsTv, numLikesTv, numFriendsTv;
    private Button addFriendButton, friendsButton, likesButton;
    private ListView listview;
    private ProgressBar progressOther, progressOtherTabs;

    private String myUsername;
    private HashSet<String> friendsUsernames;
    
    private ResultReceiver receiver;
    private TabFriendsAdapter friendsAdapter;
    private InboxViewAdapter likesAdapter;

    private ArrayList<Session> likedSessions;
    private ArrayList<Profile> friendsList;
    private HashSet<String> pendingThemSet;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    // TODO: Change the friends tab to raps tab + support profile pics

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile_activity);
        context = this;

        setupActionBar();
        getViews();
        setOnClickListeners();
        initData();
    }

    // Enable up navigation
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getViews() {
        profilePicture = (ImageView) findViewById(R.id.other_profile_picture);
        username = (TextView) findViewById(R.id.other_username);
        fullName = (TextView) findViewById(R.id.other_fullname);
        numRapsTv = (TextView) findViewById(R.id.otherNumRapsTv);
        numLikesTv = (TextView) findViewById(R.id.otherNumLikesTv);
        numFriendsTv = (TextView) findViewById(R.id.otherNumFriendsTv);
        addFriendButton = (Button) findViewById(R.id.other_add_friend_button);
        friendsButton = (Button) findViewById(R.id.other_friends_button);
        likesButton = (Button) findViewById(R.id.other_likes_button);
        listview = (ListView) findViewById(R.id.other_tabs_lv);
        progressOther = (ProgressBar) findViewById(R.id.progress_other);
        progressOtherTabs = (ProgressBar) findViewById(R.id.progress_other_tabs);

        // Hide everything until we get some data
        setProfileVisibility(0);
        setTabVisibility(0);
    }

    private void setOnClickListeners() {
        addFriendButton.setOnClickListener(this);
        friendsButton.setOnClickListener(this);
        likesButton.setOnClickListener(this);
    }

    // Get the user's profile information
    private void initData() {
        Bundle arguments = getIntent().getExtras();
        myUsername = arguments.getString(T.MY_USERNAME);
        pendingThemSet = (HashSet) arguments.getSerializable(T.PENDING_THEM);
        if(pendingThemSet == null) {
            // Does this really need to be made? It only executes from ProfileFragment, but
            // we always get friends from profile fragment, not pending friends...
            getPendingRequests();
        }

        // api request
        getProfileInfo(arguments.getString(T.USERNAME));
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.other_add_friend_button:
                sendFriendRequest(username.getText().toString());
                disableButton(addFriendButton, "Sent");

                break;
            case R.id.other_friends_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(1);
                break;
            case R.id.other_likes_button:
                if(v.isSelected()) {
                    break; // no need to make another request
                }
                setTabPressed(2);
                break;
            default:
                break;
        }
    }

    private void disableButton(Button button, String disabledText) {
        button.setEnabled(false);
        button.setText(disabledText);
        button.setBackgroundResource(R.drawable.disabled_state);
    }

    private void enableButton(Button button, String enabledText) {
        button.setEnabled(true);
        button.setText(enabledText);
        button.setBackgroundResource(R.drawable.button_style);
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

    // To visually simulate tabs, we "select" the given tab number
    private void setTabPressed(int tabNumber) {
        friendsButton.setSelected((tabNumber == 1));
        likesButton.setSelected((tabNumber == 2));

        // Load the appropriate data
        switch(tabNumber)
        {
            case 1:
                // Friends
                friendsAdapter = new TabFriendsAdapter(context, R.layout.tab_friends_list_row, null);
                listview.setAdapter(friendsAdapter);
                friendsAdapter.setItemList(friendsList);
                friendsAdapter.notifyDataSetChanged();

                setOnItemClickListener(1);
                break;
            case 2:
                // Likes
                likesAdapter = new InboxViewAdapter(context, R.layout.tab_likes_list_row, null);
                likesAdapter.setOtherUser(true);
                likesAdapter.setThumbnailScreenWidth(getScreenWidth());
                listview.setAdapter(likesAdapter);
                likesAdapter.setSessionsList(likedSessions);
                likesAdapter.notifyDataSetChanged();

                setOnItemClickListener(2);
                break;
        }
    }

    private void setOnItemClickListener(int tabNumber) {
        switch(tabNumber)
        {
            case 1:
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String otherUsername = friendsList.get(position).getUsername();
                        refreshData(otherUsername);
                    }
                });

                break;
            case 2:
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // TODO: Play video
                        Session likedVideo = likedSessions.get(position);

                    }
                });
                break;
        }
    }

    private void refreshData(String otherUsername) {
        setProfileVisibility(0);
        setTabVisibility(0);
        getProfileInfo(otherUsername);
    }

    private void setTabVisibility(int type) {
        // Set the visibility of a group of views
        if(type == 0) {
            progressOtherTabs.setVisibility(View.VISIBLE);
            listview.setVisibility(View.INVISIBLE);
        } else if(type == 1) {
            progressOtherTabs.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }
    }

    private void setProfileVisibility(int type) {
        // Set the visibility of a group of views
        if(type == 0) {
            progressOther.setVisibility(View.VISIBLE);
        } else if(type == 1) {
            progressOther.setVisibility(View.GONE);
        }
    }

    // Our api requests
    private void getProfileInfo(String username) {
        setProfileVisibility(0);

        // Make an api call to get the user's information
        Intent api = new Intent(this, APIService.class);
        OtherProfileReceiver receiver = new OtherProfileReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_OTHER_PROFILE);
        api.putExtra(T.USERNAME, username);
        api.putExtra(T.RECEIVER, receiver);
        startService(api);
    }

    private void getPendingRequests() {
        setProfileVisibility(0);

        Intent api = new Intent(context, APIService.class);
        ResultReceiver receiver = new RequestsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.FRIEND_REQUESTS_PENDING);
        api.putExtra(T.RECEIVER, receiver);
        context.startService(api);
    }

    private void sendFriendRequest(String username) {
        Intent api = new Intent(context, APIService.class);
        api.putExtra(T.API_TYPE, T.FRIEND_REQUEST);
        api.putExtra(T.USERNAME, username);
        context.startService(api);

        addFriendButton.setText("Sent");
    }

    private double getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public class OtherProfileReceiver extends ResultReceiver {
        public OtherProfileReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Convert the api request from JSON
                Log.d(TAG, "APIService returned successfully with other profile");
                String result = data.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                OtherProfile otherUser = (new Gson()).fromJson(result, OtherProfile.class);

                if(otherUser.getUsername() != null) {
                    username.setText(otherUser.getUsername());
                }
                if(otherUser.getFullName() != null) {
                    fullName.setText(otherUser.getFullName());
                }

                int numRaps = otherUser.getNumRaps();
                int numLikes = otherUser.getNumLikes();
                int numFriends = otherUser.getNumFriends();

                numRapsTv.setText(
                        (numRaps == 1) ? numRaps + " Rap" : numRaps + " Raps");
                numLikesTv.setText(
                        (numLikes == 1) ? numLikes + " Like" : numLikes + " Likes");
                numFriendsTv.setText(
                        (numFriends == 1) ? numFriends + " Friend" : numFriends + " Friends");

                // Save likes
                likedSessions = (ArrayList) otherUser.getLikedSessions();

                // Save and set friends by default
                friendsList = (ArrayList) otherUser.getFriendsProfilesAsList();
                setTabPressed(1);

                // Update friends button status
                friendsUsernames = (HashSet) otherUser.getFriendsUsernamesAsSet();
                if(friendsUsernames.contains(myUsername)) {
                    // If we're already friends, disable the button
                    disableButton(addFriendButton, "Friends");
                } else if(pendingThemSet.contains(otherUser.getUsername())) {
                    // If we've sent a friend request, disable the button
                    disableButton(addFriendButton, "Sent");
                } else {
                    // By default
                    enableButton(addFriendButton, "Add Friend");
                }

                URL profilePictureURL = otherUser.getProfilePictureUrl();
                if(profilePictureURL != null) {
                    imageLoader.displayImage(profilePictureURL.toString(), profilePicture);
//                    Picasso.with(context)
//                            .load(profilePictureURL.toString())
//                            .placeholder(R.drawable.background_333_transparent2)
//                            .into(profilePicture);
                } else {
                    Picasso.with(context)
                            .load(R.drawable.default_profile_picture)
                            .into(profilePicture);
                }

                // Show data
                setProfileVisibility(1);
                setTabVisibility(1);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
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
                List<String> pendingThemList = requests.getPendingThemUsernames();
                pendingThemSet = new HashSet<String>(pendingThemList);

                // Show data
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }
}
