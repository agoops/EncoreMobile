package com.encore.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.encore.R;

public class IntroFragment extends Fragment implements OnClickListener {
	private static int LOGIN = 0;
	private static int SIGNUP = 1;
	
	Button login;
	Button signup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.intro_fragment, container, false);
		
		login = (Button) v.findViewById(R.id.login_intro);
		signup = (Button) v.findViewById(R.id.signup_intro);
		
		login.setOnClickListener(this);
		signup.setOnClickListener(this);
		return v;
	}
	
	// Click listeners!
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.login_intro:
			launchLogin();
			break;
		case R.id.signup_intro:
			launchSignup();
			break;
		default:
			break;
		}
	}
	
	public void launchLogin() {
		// Create new transaction and login fragment
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		LoginFragment loginFragment = new LoginFragment();
		
		// Replace the intro fragment with the login fragment
		ft.replace(R.id.fragment_placeholder, loginFragment);
		ft.addToBackStack(null);
		
		ft.commit();
	}
	
	public void launchSignup() {
		// Create new fragment and transaction
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		SignupFragment signupFragment = new SignupFragment();
		
		// Replace the intro fragment with the signup fragment 
		ft.replace(R.id.fragment_placeholder, signupFragment);
		ft.addToBackStack(null);
		
		ft.commit();
	}
}
