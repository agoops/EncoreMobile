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
import android.widget.EditText;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.util.T;

/**
 * Created by babakpourkazemi on 3/12/14.
 */
public class FeedbackActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "FeedbackActivity";
    private Context context;

    private EditText feedbackText;
    private Button submitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        context = this;

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getViews();
        setOnClickListeners();
    }

    private void getViews() {
        feedbackText = (EditText) findViewById(R.id.feedback_edittext);
        submitButton = (Button) findViewById(R.id.feedback_submit);
    }

    private void setOnClickListeners() {
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.feedback_submit:
                String feedbackString = feedbackText.getText().toString().trim();

                if(feedbackString.length() < 5) {
                    Toast.makeText(context, "Your feedback is too short!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    ResultReceiver receiver = new FeedbackReceiver(new Handler());

                    Intent api = new Intent(context, APIService.class);
                    api.putExtra(T.API_TYPE, T.FEEDBACK);
                    api.putExtra(T.FEEDBACK_KEY, feedbackString);
                    api.putExtra(T.RECEIVER, receiver);
                }
                break;
        }
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

    public class FeedbackReceiver extends ResultReceiver {
        public FeedbackReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                Log.d(TAG, "feedback returned successfully");
                Toast.makeText(context, "We got your feedback, thanks!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Log.d(TAG, "POST feedback unsuccessful!");
                Toast.makeText(context, "Feedback unable to send", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }
}
