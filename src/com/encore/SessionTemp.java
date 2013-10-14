package com.encore;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class SessionTemp {
	String desc;
	Drawable icon;
	Uri uri;
	public SessionTemp (String description, Drawable icon) {
		desc = description;
		this.icon = icon;
	}
	
	public Drawable loadIcon(){
		return icon;
	}
	
	public String loadLabel() {
		return desc;
	}
	
	public void setUri(Uri uri) {
		this.uri = uri;
	}
	
	public Uri getUri() {
		return uri;
	}
}
