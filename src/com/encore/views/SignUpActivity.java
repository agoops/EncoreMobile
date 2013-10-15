package com.encore.views;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.encore.R;
import com.encore.API.APIService;

public class SignUpActivity extends Activity {
	Context mContext;
	EditText fullname;
	EditText email;
	EditText username;
	EditText password;
	EditText retypePassword;
	Button signup;
	
	APIService apiService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_activity);
		mContext = this;
		apiService = new APIService();
		fullname = (EditText) findViewById(R.id.fullname);
		email = (EditText) findViewById(R.id.email);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		retypePassword = (EditText) findViewById(R.id.retypePassword);

		signup = (Button) findViewById(R.id.signup_signup);

		setUpSignupButton();
	}

	public void setUpSignupButton() {
		signup.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String name, emailText, usernameText, passwordText, retypePasswordText;
				name = fullname.getText().toString();
				emailText = email.getText().toString();
				usernameText = username.getText().toString();
				passwordText = password.getText().toString();
				retypePasswordText = retypePassword.getText().toString();
				
				if (name.equals("") || emailText.equals("")|| 
						usernameText.equals("")|| passwordText.equals("")||
						retypePasswordText.equals("")) {
					Toast.makeText(mContext,"Please fill out all fields", 
			                Toast.LENGTH_SHORT).show();
					return;
				}
				if (!passwordText.equals(retypePasswordText)){
					Toast.makeText(mContext,"Passwords do not match", 
			                Toast.LENGTH_SHORT).show();
					return;
				}
				String phoneNumber = "poop";
				try{
				TelephonyManager tMgr =(TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
				phoneNumber = tMgr.getLine1Number();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				String[] names = name.split(" ");
				String firstName = names[0];
				String lastName = "";
				if (names.length ==2){
					lastName = names[1];
				}
				
				
				Map<String, String> map = new HashMap<String,String>();
				map.put("first_name", firstName);
				map.put("last_name", lastName);
				map.put("email", emailText);
				map.put("username", usernameText);
				map.put("password", passwordText);
				map.put("phone_number", phoneNumber);
				
				String jsonData = new JSONObject(map).toString();
				Toast.makeText(mContext,jsonData, 
		                Toast.LENGTH_LONG).show();
				//apiService.connect("post_users", json_data, null);
				return;
				
			}
			
		});
	}

}
