package com.encore;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SessionView extends LinearLayout {
	SessionTemp data;
	public SessionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setData(SessionTemp st){
		this.data = st;
	}
	
	public SessionTemp getData() {
		return data;
	}
}
