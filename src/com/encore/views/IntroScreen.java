package com.encore.views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.encore.R;

public class IntroScreen extends Activity {
	
	private static int LOGIN = 0;
	private static int SIGNUP = 1;
	
	Button login;
	Button signup;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_activity);
		
		login = (Button) findViewById(R.id.login_intro);
		signup = (Button) findViewById(R.id.signup_intro);
		assignButtonsToActivities();
		
		
	}
	
	public void assignButtonsToActivities() {
		
	}
	
	
}
