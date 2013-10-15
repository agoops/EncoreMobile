package com.encore.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.encore.R;

public class IntroScreen extends Activity {
	
	private static int LOGIN = 0;
	private static int SIGNUP = 1;
	
	Button login;
	Button signup;
	Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_activity);
		
		mContext = this;
		login = (Button) findViewById(R.id.login_intro);
		signup = (Button) findViewById(R.id.signup_intro);
		assignButtonsToActivities();
		
		
	}
	
	public void assignButtonsToActivities() {
		signup.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, SignUpActivity.class));
			}
		});
	}
	
	
}
