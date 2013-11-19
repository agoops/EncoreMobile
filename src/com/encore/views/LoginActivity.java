package com.encore.views;

import util.T;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.encore.R;
import com.encore.API.APIService;

public class LoginActivity extends Activity {
	private String tag = "LoginActivity";
	Button login;
	EditText usernameInput;
	EditText passwordInput;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		login = (Button) findViewById(R.id.login);
		usernameInput = (EditText) findViewById(R.id.username_login);
		passwordInput = (EditText) findViewById(R.id.password_login);

	}

	public void signIn(View view) {
		String username = usernameInput.getText().toString();
		String password = passwordInput.getText().toString();

		Intent apiIntent = new Intent(this, APIService.class);

		apiIntent.putExtra(T.API_TYPE, T.SIGN_IN);
		
		apiIntent.putExtra(T.USERNAME, username);
		apiIntent.putExtra(T.PASSWORD,password);
		LoginReceiver mReceiver = new LoginReceiver(new Handler());
		apiIntent.putExtra("receiver", mReceiver);
		
		startService(apiIntent);
		Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show();
		
		
	}

	public void skip(View view) {
		startActivity(new Intent(this, HomeActivity.class));
	}
	
	private void goToHomeScreen() {
		Intent i =  new Intent(this, HomeActivity.class);
		startActivity(i);
	}
	private void showLoginFailed() {
		Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
	}
	
	private class LoginReceiver extends ResultReceiver {

		public LoginReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == 1) {
				Log.d(tag, "APISerivce returned successful");
				
				goToHomeScreen();

			}
			else {
				showLoginFailed();
			}
		}
		
	}

}
