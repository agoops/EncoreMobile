package com.encore;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.encore.API.models.User;

public class FriendView extends LinearLayout {

	User user;

	
	public FriendView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setUser(User user){
		this.user = user;
	}
}