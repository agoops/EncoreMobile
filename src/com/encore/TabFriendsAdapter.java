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
    private TextView username, fullName;
    private ImageView profilePicture;

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
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layoutId, null);
        Profile friend = friends.get(position);

        // Get the views
        username = (TextView) convertView.findViewById(R.id.tab_friends_username);
        fullName = (TextView) convertView.findViewById(R.id.tab_friends_fullname);
        profilePicture = (ImageView) convertView.findViewById(R.id.profile_details_profile_picture);

        // Set the sessionTitle and numLikes
        username.setText(friend.getUsername());
        fullName.setText(friend.getFullName());

        // Set the profile picture
        if(friend.getProfilePictureUrl() != null) {
            String url = friend.getProfilePictureUrl().toString();

            imageLoaderWrapper.uriToImageView.put(url, profilePicture);
            imageLoaderWrapper.loadImage(url);

//            imageLoader.loadImage(url, new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                    ImageView thumbnail = uriToImageView.get(imageUri);
//                    thumbnail.setImageDrawable(
//                            context.getResources().getDrawable(R.drawable.background_333_transparent2));
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
//                    // TODO: What is the friend limit before the cache explodes?
//                    String filename = "Rapback_friend_" + count;
//                    count += 1;
//                    f = T.bitmapToFile(loadedBitmap, 90,
//                            context.getCacheDir(), filename);
//
//                    ImageView profile = uriToImageView.get(imageUri);
//                    profile.setImageURI(null);
//                    profile.setImageURI(Uri.fromFile(f));
////                    loadedBitmap.recycle(); // TODO implement viewholder pattern to overcome not being able to recycle
//                }
//            });
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
}
