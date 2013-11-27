package com.encore;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.encore.API.models.Session;

public class SessionView extends LinearLayout {
	Session data;
	String uri;
	public SessionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setSession(Session st){
		this.data = st;
	}
	
	public Session getSession() {
		return data;
	}
	
	public String getUri(){
		return "/storage/sdcard0/DCIM/Camera/20130923_224141.mp4";
	}
}
