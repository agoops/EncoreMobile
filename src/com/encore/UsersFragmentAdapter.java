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
import com.encore.models.Profile;
import com.encore.util.T;

import java.util.ArrayList;
import java.util.List;

public class UsersFragmentAdapter extends ArrayAdapter<Profile> {
	private Context mContext;
	private List<Profile> mProfileList;
	private String tag = "UsersFragmentAdapter";

	public UsersFragmentAdapter(Context context, int resource,
			List<Profile> profiles) {
		super(context, resource, profiles);
		mContext = context;
		mProfileList = profiles;
	}

	@Override
	public int getCount() {
		if (mProfileList != null) {
			return mProfileList.size();
		}
		return 0;
	}

	@Override
	public Profile getItem(int arg0) {
		if (mProfileList != null) {
			return mProfileList.get(arg0);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// reference to convertView
		FriendView v = (FriendView) convertView;
		Profile profile = mProfileList.get(position);
		Log.d(tag, "User for this view is: " + profile.getUsername());

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
		sr.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FriendView fv = (FriendView) v.getParent();
				Button button = (Button) v;
				String username = fv.getUser().getUsername();
				Log.d(tag, "sessionTitle in adapter: " + username);
				Intent apiIntent = new Intent(mContext, APIService.class);
				apiIntent.putExtra(T.API_TYPE, T.FRIEND_REQUEST);
				apiIntent.putExtra(T.USERNAME, username);
				//TODO: maybe put a receiver in apiIntent to change text to pending when it's acutally pending
				mContext.startService(apiIntent);
				button.setText("Pending");
				
			}
			
		});
//		CheckBox checkbox = (CheckBox) v
//				.findViewById(R.id.checkbox_user_view);

		// set data to display and store
		username.setText(profile.getUsername());
		v.setProfile(profile);
		// return view
		return v;
	}
	
	
	public void setItemList(ArrayList<Profile> Profiles) {
		this.mProfileList = Profiles;
	}


}
