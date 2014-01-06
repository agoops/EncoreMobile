package com.encore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.encore.models.Session;

import java.util.ArrayList;

public class SessionView extends LinearLayout {
	Session data;
	String uri;
	ArrayList<View> commentViews;
	boolean commentsVisible;
	Button commentButton;
	
	public SessionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commentViews = new ArrayList<View>();
		commentsVisible = false;
	}
	
	public boolean areCommentsVisible(){
		return commentsVisible;
	}
	
	public void setCommentsVisible(boolean visible){
		this.commentsVisible = visible;
	}
	
	public void toggleCommentsVisible(){
		// Toggle numLikes TVs and Buttons
		for (View cv: commentViews){
			cv.setVisibility(this.areCommentsVisible() ? TextView.GONE : TextView.VISIBLE);
		}
		
		this.setCommentsVisible(!this.areCommentsVisible());
	}
	
	public void addCommentView(View commentView) {
		commentViews.add(commentView);
	}
	
	public ArrayList<View> getCommentViews(){
		return commentViews;
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
