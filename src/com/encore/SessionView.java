package com.encore;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.encore.API.models.Comment;
import com.encore.API.models.Session;

public class SessionView extends LinearLayout {
	Session data;
	String uri;
	ArrayList<View> commentViews;
	boolean commentsVisible;
	public SessionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		commentViews = new ArrayList<View>();
		commentsVisible = false;
		// TODO Auto-generated constructor stub
	}
	
	public boolean areCommentsVisible(){
		return commentsVisible;
	}
	
	public void setCommentsVisible(boolean visible){
		this.commentsVisible = visible;
	}
	
	public void toggleCommentsVisible(){
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
