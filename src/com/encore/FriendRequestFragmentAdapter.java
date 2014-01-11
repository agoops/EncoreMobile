package com.encore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.models.User;
import com.encore.util.T;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestFragmentAdapter extends ArrayAdapter<User> {
	
	
	private Context mContext;
	private List<User> mUserList;
	private String tag = "FriendReauestFragmentAdapter";

	public FriendRequestFragmentAdapter(Context context, int resource,
			List<User> objects) {
		super(context, resource, objects);
		mContext = context;
		mUserList = objects;
	}
	
	@Override
	public int getCount() {
		if (mUserList != null) {
			return mUserList.size();
		}
		return 0;
	}

	@Override
	public User getItem(int arg0) {
		if (mUserList != null) {
			return mUserList.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void setItemList(ArrayList<User> users) {
		this.mUserList = users;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// reference to convertView
		FriendView v = (FriendView) convertView;
		User user = mUserList.get(position);
		Log.d(tag, "User for this view is: " + user.getUsername());

		// inflate new layout if null
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = (FriendView) inflater.inflate(R.layout.users_view, parent,
					false);
		}

		// load controls from layout resources
		TextView username = (TextView) v
				.findViewById(R.id.username_user_view);
		
		Button sr = (Button) v.findViewById(R.id.send_request);
		sr.setText("Confirm");
		sr.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FriendView fv = (FriendView) v.getParent();
				Button button = (Button) v;
				String username = fv.getUser().getUsername();
				Log.d(tag, "sessionTitle in adapter: " + username);
				Intent apiIntent = new Intent(mContext, APIService.class);
				apiIntent.putExtra(T.API_TYPE, T.ACCEPT_FRIEND_REQUEST);
				apiIntent.putExtra(T.ACCEPTED, true);
				apiIntent.putExtra(T.USERNAME, username);
				//TODO: maybe put a receiver in apiIntent to change text to pending when it's acutally pending
				mContext.startService(apiIntent);
				button.setText("Friends!");
				button.setEnabled(false);
			}
			
		});
		
		// set data to display and store
		username.setText(user.getUsername());
		v.setUser(user);
		// return view
		return v;
	}
}
