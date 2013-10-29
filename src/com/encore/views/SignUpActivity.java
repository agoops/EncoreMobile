package com.encore.views;


import util.T;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.encore.R;
import com.encore.API.APIService;

public class SignUpActivity extends Activity {
	EditText username, password, first_name, 
		last_name, email, phone_number;
	Button next_step;
	APIService api;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_activity);
		
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		first_name = (EditText) findViewById(R.id.first_name);
		last_name = (EditText) findViewById(R.id.last_name);
		email = (EditText) findViewById(R.id.email);
		phone_number = (EditText) findViewById(R.id.phone_number);
		
		next_step = (Button) findViewById(R.id.next_step);
		
		api = new APIService();
	}
	
	public void signUp(View view) {
		Log.d("SIGNUP", "Button clicked");
		
		Intent apiIntent = new Intent(this, APIService.class);
		
		apiIntent.putExtra(T.API_TYPE, T.SIGN_UP);
		
		apiIntent.putExtra(T.USERNAME, username.getText().toString());
		apiIntent.putExtra(T.PASSWORD, password.getText().toString());
		apiIntent.putExtra(T.FIRST_NAME, first_name.getText().toString());
		apiIntent.putExtra(T.LAST_NAME, last_name.getText().toString());
		apiIntent.putExtra(T.EMAIL, email.getText().toString());
		apiIntent.putExtra(T.PHONE_NUMBER, phone_number.getText().toString());
		
		startService(apiIntent);
	}
}
