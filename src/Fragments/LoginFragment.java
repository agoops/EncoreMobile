package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.encore.R;
import com.encore.API.APIService;

public class LoginFragment extends Fragment implements OnClickListener {
	Button login;
	APIService api;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.login_fragment, container, false);
		login = (Button) v.findViewById(R.id.login);
		login.setOnClickListener(this);
		
		api = new APIService();
		
		return v;
	}
	
	// Click listeners!
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.login:
			login();
			break;
		default:
			break;
		}
	}
	
	public void login() {
		// Do API mumbo-jumbo
	}
}
