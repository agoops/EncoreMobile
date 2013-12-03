package Fragments;

import util.T;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.encore.R;
import com.encore.API.APIService;
import com.encore.views.HomeActivity;

public class SignupFragment extends Fragment implements OnClickListener {
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
		nextStep.setOnClickListener((OnClickListener) this);
		
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
		
		// This should be moved into APIService, and an APIService instance should be instantiated on top
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		SignUpReceiver receiver = new SignUpReceiver(new Handler()); 
		apiIntent.putExtra(T.API_TYPE, T.SIGN_UP);
		apiIntent.putExtra(T.RECEIVER, receiver);
		apiIntent.putExtra(T.USERNAME, username.getText().toString());
		apiIntent.putExtra(T.PASSWORD, password.getText().toString());
		apiIntent.putExtra(T.FIRST_NAME, first_name.getText().toString());
		apiIntent.putExtra(T.LAST_NAME, last_name.getText().toString());
		apiIntent.putExtra(T.EMAIL, email.getText().toString());
		apiIntent.putExtra(T.PHONE_NUMBER, phone_number.getText().toString());
		
		getActivity().startService(apiIntent);
		
		
	}
	
	private class SignUpReceiver extends ResultReceiver{

		public SignUpReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
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
