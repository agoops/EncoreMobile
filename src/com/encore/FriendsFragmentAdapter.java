package com.encore;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.encore.API.models.User;

public class FriendsFragmentAdapter extends BaseAdapter {
	private Context mContext;
	private List<User> mFriendList;
	private String tag = "FriendsFragmentAdapter";
	
	public FriendsFragmentAdapter(Context context, List<User> list) {

		this.mContext=context;
		this.mFriendList=list;
	}

	@Override
	public int getCount() {
		Log.d(tag, "size of friends list: " + mFriendList.size());
		return mFriendList.size();
	}
	
	@Override
	public Object getItem(int arg0){
		Log.d(tag,"Asking for item: " + arg0);
		return mFriendList.get(arg0);
	}
	
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(tag, "Populating position: " + position);
        // reference to convertView
        FriendView v = (FriendView) convertView;
        User user = mFriendList.get(position);
        Log.d(tag, "User for this view is: "+user.getUsername());
        
        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = (FriendView) inflater.inflate(R.layout.friend_view, parent, false);
            
            
        }
 
        // load controls from layout resources
        TextView username = (TextView)v.findViewById(R.id.username_friend_view);
        CheckBox checkbox = (CheckBox)v.findViewById(R.id.checkbox_friend_view);
 
        // set data to display
        username.setText(user.getUsername());
        checkbox.setChecked(false);
 
        // return view
        return v;
	}
}
