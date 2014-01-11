package com.encore.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.util.T;
import com.encore.views.HomeActivity;

public class SignupFragment extends Fragment implements OnClickListener {
    private static final String TAG = "SignupFragment";
	EditText username, password, first_name, 
		last_name, email, phone_number;
	Button next_step;
	APIService api;
	Context mContext;
	
	// Provides a layout for the fragment when it's first drawn.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.signup_fragment, container, false);
		mContext = getActivity();
		username = (EditText) view.findViewById(R.id.username);
		password = (EditText) view.findViewById(R.id.password);
		first_name = (EditText) view.findViewById(R.id.first_name);
		last_name = (EditText) view.findViewById(R.id.last_name);
		email = (EditText) view.findViewById(R.id.email);
		phone_number = (EditText) view.findViewById(R.id.phone_number);
		next_step = (Button) view.findViewById(R.id.next_step);
		
		api = new APIService();
		
		Button nextStep = (Button) view.findViewById(R.id.next_step);
		nextStep.setOnClickListener(this);
		
		return view;
	}
	
	// Click listeners
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.next_step:
			signUp(v);
			break;
		default:
			break;
		}
	}
	
	// Signup using our APIService
	public void signUp(View view) {
        String usernameField = username.getText().toString();
        String passwordField = password.getText().toString();
        String firstNameField = first_name.getText().toString();
        String lastNameField = last_name.getText().toString();
        String emailField = email.getText().toString();
        String phoneNumberField = phone_number.getText().toString();

        if(validateFields(usernameField, passwordField, firstNameField,
                lastNameField, emailField, phoneNumberField)) {
            // This should be moved into APIService, and an APIService instance should be instantiated on top
            Intent apiIntent = new Intent(getActivity(), APIService.class);
            SignUpReceiver receiver = new SignUpReceiver(new Handler());
            apiIntent.putExtra(T.API_TYPE, T.SIGN_UP);
            apiIntent.putExtra(T.RECEIVER, receiver);
            apiIntent.putExtra(T.USERNAME, usernameField);
            apiIntent.putExtra(T.PASSWORD, passwordField);
            apiIntent.putExtra(T.FIRST_NAME, firstNameField);
            apiIntent.putExtra(T.LAST_NAME, lastNameField);
            apiIntent.putExtra(T.EMAIL, emailField);
            apiIntent.putExtra(T.PHONE_NUMBER, phoneNumberField);

            getActivity().startService(apiIntent);
        }
	}

    // Basic checking of fields
    public boolean validateFields(String username, String password, String firstName,
                                  String lastName, String email, String phone) {
        // First, we validate all of our fields
        String invalidMessage = "";
        if(username.length() < 3) {
            invalidMessage = "Username must be at least 3 characters";
        } else if (password.length() < 5) {
            invalidMessage = "Password must be at least 5 characters";
        } else if (firstName.length() <= 0) {
            invalidMessage = "Forgetting a first name?";
        } else if (lastName.length() <= 0) {
            invalidMessage = "Forgetting a last name?";
        } else if (!isValidEmail(email)) {
            invalidMessage = "Invalid email";
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            invalidMessage = "Invalid phone number";
        }

        if(invalidMessage.equals("")) {
            Log.d(TAG, "All fields are valid");
            return true;
        } else {
            Toast.makeText(getActivity(), invalidMessage, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Thank you, stackoverflow
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
	
	private class SignUpReceiver extends ResultReceiver{

		public SignUpReceiver(Handler handler) {
			super(handler);
		}
		
		@Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 1) {
                	Intent launchHome = new Intent(getActivity(), HomeActivity.class);
            		startActivity(launchHome);
                }
                else{
                	Toast.makeText(mContext, "Sign up didn't work. Try again.", Toast.LENGTH_SHORT).show();
                	return;
                }
		
		}
		
	}
}
