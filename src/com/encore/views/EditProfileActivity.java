package com.encore.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.TokenHelper;
import com.encore.util.T;

/**
 * Created by babakpourkazemi on 1/5/14.
 */
public class EditProfileActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EditProfileActivity";
    private EditText first, last, email, phone;
    private Button makeChangesBtn, logoutButton;
    private String currentFirst, currentLast, currentEmail, currentPhone;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        getViews();
        setCurrentInfo();
        setOnClickListeners();
    }

    private void getViews() {
        first = (EditText) findViewById(R.id.edit_profile_firstname);
        last = (EditText) findViewById(R.id.edit_profile_lastname);
        email = (EditText) findViewById(R.id.edit_profile_email);
        phone = (EditText) findViewById(R.id.edit_profile_phonenumber);
        makeChangesBtn = (Button) findViewById(R.id.edit_profile_submit_button);
        logoutButton = (Button) findViewById(R.id.edit_profile_logout_button);
    }

    private void setCurrentInfo() {
        // Pre-fill the edittexts with the user's current information
        Intent intent = getIntent();
        currentFirst = intent.getStringExtra(T.FIRST_NAME);
        currentLast = intent.getStringExtra(T.LAST_NAME);
        currentEmail = intent.getStringExtra(T.EMAIL);
        currentPhone = intent.getStringExtra(T.PHONE_NUMBER);

        first.setText(currentFirst);
        last.setText(currentLast);
        email.setText(currentEmail);
        phone.setText(currentPhone);
    }

    private void setOnClickListeners() {
        makeChangesBtn.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                // Respond to the action bar's up navigation
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.edit_profile_submit_button:
                String firstName = first.getText().toString();
                String lastName = last.getText().toString();
                String emailAddress = email.getText().toString().trim();
                String phoneNumber = PhoneNumberUtils.formatNumber(
                        phone.getText().toString());

                boolean isValid =
                        validateFields(firstName, lastName, emailAddress, phoneNumber);

                if(isValid) {
                    updateUsersInfo(firstName, lastName, emailAddress, phoneNumber);
                    Intent homeActivity = new Intent(this, HomeActivity.class);
                    startActivity(homeActivity);
                }
                break;
            case R.id.edit_profile_logout_button:
                TokenHelper.updateToken(this, null);
                Intent introScreen = new Intent(this, WelcomeActivity.class);
                startActivity(introScreen);
                break;
            default:
                break;
        }
    }

    public void updateUsersInfo(String first, String last, String email, String phone) {
        // Make API call
        Intent api = new Intent(this, APIService.class);
        api.putExtra(T.API_TYPE, T.UPDATE_USER);
        api.putExtra(T.FIRST_NAME, first);
        api.putExtra(T.LAST_NAME, last);
        api.putExtra(T.EMAIL, email);
        api.putExtra(T.PHONE_NUMBER, phone);
        startService(api);
    }

    // Basic checking of fields
    public boolean validateFields(String firstName, String lastName, String email, String phone) {
        // First, we validate all of our fields
        String invalidMessage = "";
        if(firstName.length() <= 0) {
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
            Toast.makeText(this, invalidMessage, Toast.LENGTH_LONG).show();
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
}
