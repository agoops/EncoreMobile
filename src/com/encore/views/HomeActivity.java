package com.encore.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.Fragments.ProfileFragment;
import com.encore.R;
import com.encore.StartSession;
import com.encore.TabFriendsAdapter;
import com.encore.models.Friends;
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class HomeActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "HomeActivity";

    private DrawerLayout drawerLayout;
    private ListView leftDrawerList, rightDrawerList;
    private String[] leftDrawerTitles;
    private ActionBarDrawerToggle leftDrawerToggle;
    private RelativeLayout leftDrawerContainer, rightDrawerContainer;
    private ImageView profilePictureIv;
    private TextView usernameTv, fullnameTv, numRapsTv, numLikesTv, numFriendsTv;
    private Button editProfile, findFriends;

    private Profile userMe;

    private File profilePictureFile;
    private Bitmap profileBitmap;
    private String myUsername;

    private TabFriendsAdapter friendsAdapter;
    private ArrayList<Profile> friendsList;
    private HashSet<String> friendsUsernames;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Show live sessions by default
        Fragment liveFragment = new LiveFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.home_content_frame, liveFragment)
                .commit();

        this.context = this;

        getViews();
        assignClickListeners();
        initData();
        setupNavDrawer();
    }

    private void getViews() {
        leftDrawerTitles = getResources().getStringArray(R.array.left_drawer_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.home_left_drawer);
        rightDrawerList = (ListView) findViewById(R.id.home_right_drawer);
        leftDrawerContainer = (RelativeLayout) findViewById(R.id.home_left_drawer_container);
        rightDrawerContainer = (RelativeLayout) findViewById(R.id.home_right_drawer_container);

        profilePictureIv = (ImageView) findViewById(R.id.left_drawer_profile_picture);
        usernameTv = (TextView) findViewById(R.id.left_drawer_username);
        fullnameTv = (TextView) findViewById(R.id.left_drawer_fullname);
        numRapsTv = (TextView) findViewById(R.id.left_drawer_NumRapsTv);
        numLikesTv = (TextView) findViewById(R.id.left_drawer_NumLikesTv);
        numFriendsTv = (TextView) findViewById(R.id.left_drawer_NumFriendsTv);
        editProfile = (Button) findViewById(R.id.left_drawer_EditButton);
        findFriends = (Button) findViewById(R.id.home_right_drawer_find_friends_button);
    }

    private void assignClickListeners() {
        numRapsTv.setOnClickListener(this);
        numLikesTv.setOnClickListener(this);
        numFriendsTv.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        leftDrawerContainer.setOnClickListener(this);
        findFriends.setOnClickListener(this);
    }

    private void initData() {
        // Init the drawers' data
        friendsUsernames = new HashSet<String>();
        getMe();
        getFriends();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.left_drawer_NumRapsTv:
                launchProfileDetailsFragment(T.PROFILE_INFO_RAPS);
                break;
            case R.id.left_drawer_NumLikesTv:
                launchProfileDetailsFragment(T.PROFILE_INFO_LIKES);
                break;
            case R.id.left_drawer_NumFriendsTv:
                launchProfileDetailsFragment(T.PROFILE_INFO_FRIENDS);
                break;
            case R.id.left_drawer_EditButton:
                Intent launchEditProfileActivity = new Intent(context, EditProfileActivity.class);
                launchEditProfileActivity.putExtra(T.FIRST_NAME, userMe.getFirstName());
                launchEditProfileActivity.putExtra(T.LAST_NAME, userMe.getLastName());
                launchEditProfileActivity.putExtra(T.EMAIL, userMe.getEmail());
                launchEditProfileActivity.putExtra(T.PHONE_NUMBER, userMe.getPhoneNumber());
                launchEditProfileActivity.putExtra(T.PROFILE_PICTURE, profilePictureFile);
                startActivity(launchEditProfileActivity);
                break;
            case R.id.home_right_drawer_find_friends_button:
                Intent launchFriendFinder = new Intent(context, FindFriendsActivity.class);
                launchFriendFinder.putExtra(T.MY_USERNAME, myUsername);
                launchFriendFinder.putExtra("friendsUsernames", friendsUsernames);
                launchFriendFinder.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(launchFriendFinder);
                break;
        }
    }

    private void launchProfileDetailsFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(T.PROFILE_INFO_TYPE, type);
        Fragment profileDetailsFragment = new com.encore.Fragments.ProfileDetailsFragment();
        profileDetailsFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.home_content_frame, profileDetailsFragment)
                .commit();
        drawerLayout.closeDrawer(leftDrawerContainer);
    }

    private void setupNavDrawer() {
        leftDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(drawerLayout.isDrawerOpen(rightDrawerContainer) &&
                        drawerView != rightDrawerContainer) {
                    // Close right drawer if left is open
                    drawerLayout.closeDrawer(rightDrawerContainer);
                }
            }
        };
        drawerLayout.setDrawerListener(leftDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        leftDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, leftDrawerTitles));
        leftDrawerList.setOnItemClickListener(new LeftDrawerItemClickListener());

        rightDrawerList.setOnItemClickListener(new RightDrawerClickListener());

        // Hide overlay
        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    private class LeftDrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        /* Swaps fragments in the main content view */
        private void selectItem(int position) {
            FragmentManager fm = getSupportFragmentManager();

            switch(position)
            {
                case 0:
                    Fragment liveFragment = new LiveFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, liveFragment)
                            .commit();
                    break;
                case 1:
                    Fragment completeFragment = new CompleteFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, completeFragment)
                            .commit();
                    break;
                case 2:
                    Fragment profileFragment = new ProfileFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, profileFragment)
                            .commit();
                    break;
                default:
                    Fragment defaultFragment = new LiveFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, defaultFragment)
                            .commit();
                    break;
            }

            leftDrawerList.setItemChecked(position, true);
            setTitle(leftDrawerTitles[position]);
            drawerLayout.closeDrawer(leftDrawerContainer);
        }
    }

    private class RightDrawerClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        /* Opens the friends' profile */
        private void selectItem(int position) {
            Intent otherProfile = new Intent(context, OtherProfileActivity.class);
            otherProfile.putExtra(T.USERNAME, friendsList.get(position).getUsername());
            otherProfile.putExtra(T.MY_USERNAME, myUsername);

            // TODO: Prevent the item from remaining selected
            rightDrawerList.setItemChecked(position, true);
            setTitle(friendsList.get(position).getFullName());
            drawerLayout.closeDrawer(rightDrawerContainer);

            startActivity(otherProfile);
        }
    }

    private void getMe() {
        Log.d(TAG, "Making request to users/me");

        Intent api = new Intent(context, APIService.class);
        ResultReceiver receiver = new MeReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_ME);
        api.putExtra(T.RECEIVER, receiver);
        startService(api);
    }

    private void getFriends() {
        Log.d(TAG, "Making request to get friends");

        Intent api = new Intent(context, APIService.class);
        ResultReceiver receiver = new FriendsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_FRIENDS);
        api.putExtra(T.RECEIVER, receiver);
        startService(api);
    }

    // Our receivers
    public class MeReceiver extends ResultReceiver {
        public MeReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                Log.d(TAG, "APIService returned successful with users/me");
                String result = data.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                userMe = new Gson().fromJson(result, Profile.class);

                int numRaps = userMe.getNumRaps();
                int numLikes = userMe.getNumLikes();
                int numFriends = userMe.getNumFriends();

                numRapsTv.setText(
                        (numRaps == 1) ? numRaps + " \nRap" : numRaps + " \nRaps");
                numLikesTv.setText(
                        (numLikes == 1) ? numLikes + " \nLike" : numLikes + " \nLikes");
                numFriendsTv.setText(
                        (numFriends == 1) ? numFriends + " \nFriend" : numFriends + " \nFriends");

                myUsername = userMe.getUsername();

                usernameTv.setText(myUsername);
                fullnameTv.setText(userMe.getFullName());

                // Download our profile picture
                profilePictureIv.setTag(userMe.getProfilePictureUrl());
                new DownloadImagesTask().execute(profilePictureIv);

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

                // Update the data on our listview
                friendsAdapter = new TabFriendsAdapter(context, R.layout.tab_friends_list_row, null);
                rightDrawerList.setAdapter(friendsAdapter);
                friendsList = new ArrayList<Profile>(Arrays.asList(friendsArray));
                friendsAdapter.setItemList(friendsList);
                friendsAdapter.notifyDataSetChanged();

                for(Profile profile : friendsList) {
                    friendsUsernames.add(profile.getUsername());
                }
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, File> {
        ImageView imageView = null;

        @Override
        protected File doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return downloadImage((URL) imageView.getTag());
        }

        @Override
        protected void onPostExecute(File file) {
            profilePictureIv.setImageBitmap(profileBitmap);
        }

        public File downloadImage(URL url) {
            // TODO: Find a better way to encapsulate all the data for profile pictures (e.g., URIs, files, bitmaps)
            try {
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                profileBitmap = BitmapFactory.decodeStream(input);

                File f = T.bitmapToFile(profileBitmap, 100,
                        context.getCacheDir(), "Rapback_downsampled_profile");

                profilePictureFile = f;
                return f;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        leftDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (leftDrawerToggle.onOptionsItemSelected(item)) {
            // If leftDrawerToggle returned true, it has handled the touch event
            return true;
        }

        switch(item.getItemId())
        {
            case R.id.action_video:
                // Launch a new session
                Log.d(TAG, "Launching StartSession");
                Intent intent = new Intent(this, StartSession.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
