package com.encore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.models.FriendRequest;
import com.encore.models.User;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/7/14.
 */
public class SearchUsersFragmentAdapter extends ArrayAdapter<User> implements View.OnClickListener {
    private static final String TAG = "SearchUsersFragmentAdapter";
    private Context context;
    private List<User> users;
    private HashSet<String> pendingThemSet;
    private HashSet<String> friendsUsernamesSet;

    private ImageView profilePic;
    private TextView usernameTv, fullNameTv;
    private Button addFriendButton;

    // Note: this adapter is only used after the search has completed
    // from FindFriendsActivity. This means we make our request for
    // pending requests AFTER the user searches for a username
    public SearchUsersFragmentAdapter(Context context, int layoutId, List<User> users) {
        super(context, layoutId, users);
        this.context = context;
        this.users = users;

        getPendingRequests();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.search_list_row, parent, false);

        User user = users.get(position);

        initViews(convertView);
        setTags();
        setOnClickListeners();
        populateViews(user);

        return convertView;
    }

    private void initViews(View convertView) {
        profilePic = (ImageView) convertView.findViewById(R.id.search_profile_pic);
        usernameTv = (TextView) convertView.findViewById(R.id.search_username);
        fullNameTv = (TextView) convertView.findViewById(R.id.search_fullname);
        addFriendButton = (Button) convertView.findViewById(R.id.search_add_friend);
    }

    private void setTags() {
        // necessary for adding a friend
        addFriendButton.setTag(R.string.first_key, usernameTv);
        addFriendButton.setTag(R.string.second_key, addFriendButton);
    }

    private void setOnClickListeners() {
        addFriendButton.setOnClickListener(this);
    }

    private void populateViews(User user) {
        // TODO: Set profile picture
        usernameTv.setText(user.getUsername());
        fullNameTv.setText(user.getFullName());

        if(friendsUsernamesSet != null && friendsUsernamesSet.contains(user.getUsername())) {
            // If you're friends with someone, disable the button
            disableButton(addFriendButton, "Friends");
        } else if(pendingThemSet != null && pendingThemSet.contains(user.getUsername())) {
            // If you've already sent a request, don't send another!
            disableButton(addFriendButton, "Sent");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.search_add_friend:
                TextView usernameTv = (TextView) v.getTag(R.string.first_key);
                Button addFriendButton = (Button) v.getTag(R.string.second_key);
                sendFriendRequest(usernameTv.getText().toString());
                disableButton(addFriendButton, "Sent");
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

    private void sendFriendRequest(String username) {
        Intent api = new Intent(context, APIService.class);
        api.putExtra(T.API_TYPE, T.FRIEND_REQUEST);
        api.putExtra(T.USERNAME, username);
        context.startService(api);

        addFriendButton.setText("Sent");
    }

    private void getPendingRequests() {
        Intent api = new Intent(context, APIService.class);
        ResultReceiver receiver = new RequestsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.FRIEND_REQUESTS_PENDING);
        api.putExtra(T.RECEIVER, receiver);
        context.startService(api);
    }

    // Used for passing the pending them data to OtherProfile
    public HashSet<String> getPendingThemSet() {
        return this.pendingThemSet;
    }

    public void setFriendsUsernamesSet(HashSet<String> usernames) {
        this.friendsUsernamesSet = usernames;
    }

    @Override
    public int getCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    @Override
    public User getItem(int position) {
        if (users != null) {
            return users.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setItemList(ArrayList<User> users) {
        this.users = users;
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
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }
}
