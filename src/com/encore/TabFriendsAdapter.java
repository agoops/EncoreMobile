package com.encore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.encore.models.Profile;
import com.encore.widget.ImageLoaderWrapper;

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

    private ImageLoaderWrapper imageLoaderWrapper;

    public TabFriendsAdapter(Context context, int layoutId, List<Profile> friends) {
        super(context, layoutId, friends);
        this.context = context;
        this.layoutId = layoutId;
        this.friends = friends;
        this.imageLoaderWrapper = new ImageLoaderWrapper(this.context);
        imageLoaderWrapper.init();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.tab_friends_username);
            holder.fullName = (TextView) convertView.findViewById(R.id.tab_friends_fullname);
            holder.profilePicture = (ImageView) convertView.findViewById(R.id.profile_details_profile_picture);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Profile friend = friends.get(position);
        holder.username.setText(friend.getUsername());
        holder.fullName.setText(friend.getFullName());
        // Set the profile picture
        if(friend.getProfilePictureUrl() != null) {
            String url = friend.getProfilePictureUrl().toString();

            imageLoaderWrapper.uriToImageView.put(url, holder.profilePicture);
            imageLoaderWrapper.loadImage(url);
        } else {
            holder.profilePicture.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.default_profile_picture));
        }
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

    static class ViewHolder {
        TextView username;
        TextView fullName;
        ImageView profilePicture;
    }
}
