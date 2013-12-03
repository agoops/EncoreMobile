package com.encore;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.encore.API.models.Profile;
import com.encore.API.models.User;

public class FriendsFragmentAdapter extends ArrayAdapter<User> implements OnCheckedChangeListener {
	private static final String TAG = "FriendsFragment";
	
	private Context mContext;
	private List<User> mFriendList;
	private List<Integer> selectedFriendsList;
	private String tag = "FriendsFragmentAdapter";
	private static LayoutInflater inflater = null;

	public FriendsFragmentAdapter(Context context, int resource,
			List<User> objects) {
		super(context, resource, objects);
		mContext = context;
		mFriendList = objects;
		selectedFriendsList = new ArrayList<Integer>();
	}

	// public FriendsFragmentAdapter(Context context, List<Profile> list) {
	//
	// this.mContext=context;
	// this.mFriendList=list;
	// }

	@Override
	public int getCount() {
		if (mFriendList != null) {
			return mFriendList.size();
		}
		return 0;
	}

	@Override
	public User getItem(int arg0) {
		if (mFriendList != null) {
			return mFriendList.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = convertView;
		final FriendsHolder viewHolder;
		
		// Inflate the friends_view if null
		if(convertView == null) {
			rowView = inflater.inflate(R.layout.friend_view, null);
			viewHolder = new FriendsHolder();
			
			viewHolder.username = (TextView) rowView.findViewById(R.id.username_friend_view);
			viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.checkbox_friend_view);
			
			rowView.setTag(viewHolder);
		} else {
			// Avoids calling findViewById() on resource every time.
			viewHolder = (FriendsHolder) rowView.getTag();
		}
		
		// And update its title (which will happen for all crowds)
		User user = mFriendList.get(position);
		viewHolder.username.setText(user.getUsername());
		viewHolder.checkBox.setTag(position);
		viewHolder.checkBox.setChecked(false);
		viewHolder.checkBox.setOnCheckedChangeListener(this);
		
    	return rowView;
	}

	public void setItemList(ArrayList<User> users) {
		this.mFriendList = users;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, 
			boolean isChecked) {
		int position = (Integer) buttonView.getTag();
		if(isChecked) {
			selectedFriendsList.add(mFriendList.get(position).getUserId());
		} else {
			selectedFriendsList.remove(mFriendList.get(position).getUserId());
		}
	}
	
	public List<Integer> getSelectedFriends() {
		Log.d(TAG, "getting selected friends: " + selectedFriendsList.toString());
		return selectedFriendsList;
	}
	
	static class FriendsHolder {
		TextView username;
		CheckBox checkBox;
	}
}
