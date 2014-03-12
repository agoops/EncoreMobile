package com.encore.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.encore.API.API;
import com.encore.R;
import com.encore.util.T;

/**
 * Created by babakpourkazemi on 3/12/14.
 */
public class InviteActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "InviteActivity";
    private Context context;
    private static String inviteURL = API.INVITE;
    private String inviteNumber;

    private Button emailButton, textButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);
        context = this;

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getViews();
        setOnClickListeners();
    }

    private void getViews() {
        emailButton = (Button) findViewById(R.id.invite_email_button);
        textButton = (Button) findViewById(R.id.invite_text_button);
    }

    private void setOnClickListeners() {
        emailButton.setOnClickListener(this);
        textButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.invite_email_button:
                Log.d(TAG, "Sending email");
                launchEmailPickerIntent();
                break;
            case R.id.invite_text_button:
                Log.d(TAG, "Sending SMS");
                launchContactPickerIntent();
                break;
        }
    }

    private void launchEmailPickerIntent() {
        String message = "Yo, you've been invited to Rapback!\nGoto " + inviteURL + " to get the app.";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_SUBJECT, "You've been invited to try Rapback!");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email provider :"));
    }

    private void launchContactPickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        // Show user only contacts w/ phone numbers
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, T.CONTACT_NUMBER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == T.CONTACT_NUMBER_REQUEST_CODE)
            {
                Uri contactUri = data.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                inviteNumber = cursor.getString(column);
                Log.d(TAG, "selected number: " + inviteNumber);
                sendText(inviteNumber);
            }
        }
    }

    private void sendText(String phoneNum) {
        String message = "Don't have Rapback? Goto " + inviteURL;

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
