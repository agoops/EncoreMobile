package com.encore.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.encore.Fragments.IntroFragment;
import com.encore.R;
import com.encore.TokenHelper;

public class WelcomeActivity extends FragmentActivity {
	private static final String TAG = "WelcomeActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //set no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.placeholder);
        String token = TokenHelper.getToken(this);

		if(token == null) {
			// Create a new transaction and welcome screen fragment
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			IntroFragment introFragment = new IntroFragment();
			ft.add(R.id.fragment_placeholder, introFragment);
			ft.commit();
		} else {
			// Token is set! Goto home.
//            Log.d(TAG, "Token is: " + token);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Intent homeActivity = new Intent(this, HomeActivity.class);
			startActivity(homeActivity);
		}
	}
}
