package com.encore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.encore.models.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabFriendsAdapter extends ArrayAdapter<Profile> {
    private static final String TAG = "TabFriendsAdapter";
    private Context context;
    private List<Profile> friends;
    private LayoutInflater inflater = null;
    private int layoutId;
    private TextView username, fullName;

    public TabFriendsAdapter(Context context, int layoutId, List<Profile> friends) {
        super(context, layoutId, friends);
        this.context = context;
        this.layoutId = layoutId;
        this.friends = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the view elements
        convertView = inflater.inflate(layoutId, null);

        username = (TextView) convertView.findViewById(R.id.tab_friends_username);
        fullName = (TextView) convertView.findViewById(R.id.tab_friends_fullname);

        // And set the sessionTitle and numLikes
        Profile friend = friends.get(position);
        username.setText(friend.getUsername());
        fullName.setText(friend.getFullName());

        return convertView;
    }

    @Override
    public int getCount() {
        if(friends != null) {
            return friends.size();
        }
        return 0;
    }

    public void setItemList(ArrayList<Profile> friends) {
        this.friends = friends;
    }
}
