package com.encore.views;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.encore.R;

public class IntroScreen extends FragmentActivity {
	
	Fragment[] fragments;
	private static int LOGIN = 0;
	private static int SIGNUP = 1;
	
	TextView rapchat;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_activity);
		
		rapchat = (TextView) findViewById(R.id.rapchat);
		rapchat.setTextColor(Color.WHITE);
	
	}
}
