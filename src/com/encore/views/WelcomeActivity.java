package com.encore.views;

import Fragments.IntroFragment;
import Fragments.SignupFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.encore.R;

public class WelcomeActivity extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeholder);
		
		// Create new transaction and welcome screen fragment
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		IntroFragment introFragment = new IntroFragment();
		
		// Add fragment and commit transaction
		ft.add(R.id.welcome_fragment_placeholder, introFragment);
		ft.commit();
	}
	
}
// Sessions & Comments accurately reflect Django models. createSession() endpoint works. LoginActivity converted to fragment. SignupActivity still needs to get converted to fragments
