package com.encore.views;

import com.encore.Fragments.IntroFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.encore.R;
import com.encore.TokenHelper;

public class WelcomeActivity extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeholder);
		String token = TokenHelper.getToken(this);
		if(token == null) {
			// Create a new transaction and welcome screen fragment
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			IntroFragment introFragment = new IntroFragment();
			// Add the fragment to and commit the transaction
			ft.add(R.id.fragment_placeholder, introFragment);
			ft.commit();
		} else {
			// Token is set! Go straight home.
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Intent homeActivity = new Intent(this, HomeActivity.class);
			startActivity(homeActivity);
		}
	}
}
