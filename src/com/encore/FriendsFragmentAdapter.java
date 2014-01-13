package com.encore;

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

import com.encore.models.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendsFragmentAdapter extends ArrayAdapter<Profile> implements OnCheckedChangeListener {
	private static final String TAG = "FriendsFragment";
	
	private Context mContext;
	private List<Profile> mFriendList;
	private String tag = "FriendsFragmentAdapter";
	private static LayoutInflater inflater = null;
    private Set<String> selectedUsernamesSet;

	public FriendsFragmentAdapter(Context context, int resource,
			List<Profile> objects) {
		super(context, resource, objects);
		mContext = context;
		mFriendList = objects;
        selectedUsernamesSet = new HashSet<String>();
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
	public Profile getItem(int arg0) {
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
		Profile user = mFriendList.get(position);
		viewHolder.username.setText(user.getUsername());
		viewHolder.checkBox.setTag(position);
		viewHolder.checkBox.setChecked(false);
		viewHolder.checkBox.setOnCheckedChangeListener(this);
		
    	return rowView;
	}

	public void setItemList(ArrayList<Profile> users) {
		this.mFriendList = users;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, 
			boolean isChecked) {

		int position = (Integer) buttonView.getTag();

        // Get the checked friend's uid
        String selectedUsername = mFriendList.get(position).getUser().getUsername();

        if(isChecked) {
            selectedUsernamesSet.add(selectedUsername);
		} else {
            selectedUsernamesSet.remove(selectedUsername);
		}
	}
	
	public Set<String> getSelectedFriends() {
		Log.d(TAG, "getting selected friends: " + selectedUsernamesSet.toString());
		return selectedUsernamesSet;
	}

    public List<String> getSelectedUsernames() {
        List<String> usernames = new ArrayList<String>(selectedUsernamesSet);
        return usernames;
    }

	static class FriendsHolder {
		TextView username;
		CheckBox checkBox;
	}
}
