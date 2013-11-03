package Fragments;

import util.T;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.encore.R;
import com.encore.API.APIService;

public class SignupFragment extends Fragment implements OnClickListener {
	EditText username, password, first_name, 
		last_name, email, phone_number;
	Button next_step;
	APIService api;
	
	// Provides a layout for the fragment when it's first drawn.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.signup_fragment, container, false);
		
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
	
	// Click listeners!
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
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		
		apiIntent.putExtra(T.API_TYPE, T.SIGN_UP);
		
		apiIntent.putExtra(T.USERNAME, username.getText().toString());
		apiIntent.putExtra(T.PASSWORD, password.getText().toString());
		apiIntent.putExtra(T.FIRST_NAME, first_name.getText().toString());
		apiIntent.putExtra(T.LAST_NAME, last_name.getText().toString());
		apiIntent.putExtra(T.EMAIL, email.getText().toString());
		apiIntent.putExtra(T.PHONE_NUMBER, phone_number.getText().toString());
		
		getActivity().startService(apiIntent);
	}
}
