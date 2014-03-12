package com.encore.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.util.T;

/**
 * Created by babakpourkazemi on 3/12/14.
 */
public class InviteActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "InviteActivity";
    private Context context;

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
                sendInvite(T.EMAIL);
                break;
            case R.id.invite_text_button:
                sendInvite(T.TEXT);
                break;
        }
    }

    private void sendInvite(String inviteType) {
        if(inviteType.equals(T.EMAIL)) {

        } else if(inviteType.equals(T.TEXT)) {

        }
        ResultReceiver receiver = new InviteReceiver(new Handler());
        Intent sendInvite = new Intent(context, APIService.class);
        sendInvite.putExtra(T.API_TYPE, T.SEND_INVITE);
        sendInvite.putExtra(T.RECEIVER, receiver);
        startService(sendInvite);
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

    public class InviteReceiver extends ResultReceiver {
        public InviteReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                Log.d(TAG, "invite returned successfully");
                Log.d(TAG, "Returned invite data: " + data.toString());
            } else {
                Log.d(TAG, "invite unsuccessful!");
                Toast.makeText(context, "Unable to send invitation. Try again.", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }
}
