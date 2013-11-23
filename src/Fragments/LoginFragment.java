package Fragments;

import util.T;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class LoginFragment extends Fragment implements OnClickListener {
	private static final String TAG = "LoginActivity";
	Button login;
	EditText usernameInput;
	EditText passwordInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d(TAG, "onCreateView called");
		View v = inflater.inflate(R.layout.login_fragment, container, false);
		
        login = (Button) v.findViewById(R.id.login);
        usernameInput = (EditText) v.findViewById(R.id.username_login);
        passwordInput = (EditText) v.findViewById(R.id.password_login);
        
        login.setOnClickListener((OnClickListener) this);
        
        return v;
    }

    // Click listeners!
    @Override
    public void onClick(View v) {
    	Log.d(TAG, "Assigning click listeners");
    	switch(v.getId()) {
    	case R.id.login:
    		login(v);
    		break;
    	default:
    		break;
    	}
    }
    
    public void login(View view) {
    	Log.d(TAG, "login called");
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        Intent apiIntent = new Intent(getActivity(), APIService.class);

        apiIntent.putExtra(T.API_TYPE, T.SIGN_IN);
        
        apiIntent.putExtra(T.USERNAME, username);
        apiIntent.putExtra(T.PASSWORD,password);
        LoginReceiver mReceiver = new LoginReceiver(new Handler());
        apiIntent.putExtra("receiver", mReceiver);
        
        getActivity().startService(apiIntent);
        Toast.makeText(getActivity(), "service started", Toast.LENGTH_SHORT).show();
    }

    public void skip(View view) {
        startActivity(new Intent(getActivity(), HomeActivity.class));
    }
    
    private void goToHomeScreen() {
        Intent i =  new Intent(getActivity(), HomeActivity.class);
        startActivity(i);
    }
    private void showLoginFailed() {
        Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
    }
    
    private class LoginReceiver extends ResultReceiver {
        public LoginReceiver(Handler handler) {
                super(handler);
                // TODO Auto-generated constructor stub
        }
        
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 1) {
                        Log.d(TAG, "APIService returned successful");
                        goToHomeScreen();
                }
                else {
                        showLoginFailed();
                }
        }
    }
	
}
