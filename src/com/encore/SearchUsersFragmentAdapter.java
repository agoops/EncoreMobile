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
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/7/14.
 */
public class SearchUsersFragmentAdapter extends ArrayAdapter<Profile> implements View.OnClickListener {
    private static final String TAG = "SearchprofilesFragmentAdapter";
    private Context context;
    private List<Profile> profiles;
    private HashSet<String> pendingThemSet;
    private HashSet<String> friendsUsernamesSet;

//    private ImageView profilePic;
//    private TextView usernameTv, fullNameTv;
//    private Button addFriendButton;

    // Note: this adapter is only used after the search has completed
    // from FindFriendsActivity. This means we make our request for
    // pending requests AFTER the user searches for a username
    public SearchUsersFragmentAdapter(Context context, int layoutId, List<Profile> profiles) {
        super(context, layoutId, profiles);
        this.context = context;
        this.profiles = profiles;

        getPendingRequests();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.search_list_row, parent, false);

            holder = new ViewHolder();
            initViews(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Profile profile = profiles.get(position);

        setTags(holder);
        setOnClickListeners(holder);
        populateViews(holder, profile);

        return convertView;
    }

    private void initViews(ViewHolder holder, View convertView) {
        holder.profilePic = (ImageView) convertView.findViewById(R.id.search_profile_pic);
        holder.usernameTv = (TextView) convertView.findViewById(R.id.search_username);
        holder.fullNameTv = (TextView) convertView.findViewById(R.id.search_fullname);
        holder.addFriendButton = (Button) convertView.findViewById(R.id.search_add_friend);
        holder.profilePic = (ImageView) convertView.findViewById(R.id.search_profile_pic);
    }

    private void setTags(ViewHolder holder) {
        // necessary for adding a friend
        holder.addFriendButton.setTag(R.string.first_key, holder.usernameTv);
        holder.addFriendButton.setTag(R.string.second_key, holder.addFriendButton);
    }

    private void setOnClickListeners(ViewHolder holder) {
        holder.addFriendButton.setOnClickListener(this);
    }

    private void populateViews(ViewHolder holder, Profile profile) {
        holder.usernameTv.setText(profile.getUsername());
        holder.fullNameTv.setText(profile.getFullName());

        URL url = profile.getProfilePictureUrl();
        if(url == null) {
            Picasso.with(context)
                    .load(R.drawable.default_profile_picture)
                    .into(holder.profilePic);
        } else {
            Picasso.with(context)
                    .load(url.toString())
                    .placeholder(R.drawable.background_333_transparent2)
                    .into(holder.profilePic);
        }

        if(friendsUsernamesSet != null && friendsUsernamesSet.contains(profile.getUsername())) {
            // If you're friends with someone, disable the button
            disableButton(holder.addFriendButton, "Friends");
        } else if(pendingThemSet != null && pendingThemSet.contains(profile.getUsername())) {
            // If you've already sent a request, don't send another
            disableButton(holder.addFriendButton, "Sent");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.search_add_friend:
                TextView usernameTv = (TextView) v.getTag(R.string.first_key);
                Button addFriendButton = (Button) v.getTag(R.string.second_key);
                sendFriendRequest(addFriendButton, usernameTv.getText().toString());
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

    private void sendFriendRequest(Button addFriendButton, String username) {
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
        if (profiles != null) {
            return profiles.size();
        }
        return 0;
    }

    @Override
    public Profile getItem(int position) {
        if (profiles != null) {
            return profiles.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setItemList(ArrayList<Profile> profiles) {
        this.profiles = profiles;
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

    static class ViewHolder {
        private ImageView profilePic;
        private TextView usernameTv, fullNameTv;
        private Button addFriendButton;
    }
}
