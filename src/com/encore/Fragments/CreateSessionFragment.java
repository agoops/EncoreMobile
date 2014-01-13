package com.encore.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.FriendsFragmentAdapter;
import com.encore.R;
import com.encore.models.Crowd;
import com.encore.models.Crowds;
import com.encore.models.Friends;
import com.encore.models.Profile;
import com.encore.util.T;
import com.encore.views.HomeActivity;
import com.encore.widget.CrowdAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by babakpourkazemi on 12/25/13.
 */
public class CreateSessionFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "CreateSessionFragment";

    private View view;
    private EditText newSessionTitle, newCrowdTitle;
    private Switch crowdToggleSwitch;
    private ListView crowdsAndFriendsLv;
    private Button sendButton;
    private ProgressBar progressBar;
    private boolean isChecked;
    private String videoFilepath, thumbnailFilepath;

    private CrowdAdapter crowdAdapter;
    private FriendsFragmentAdapter friendsAdapter;

    private ArrayList<Crowd> crowds;
    private ArrayList<Profile> friends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        view = inflater.inflate(R.layout.new_session, container, false);

        // Get our views
        newSessionTitle = (EditText) view.findViewById(R.id.new_session_title);
        newCrowdTitle = (EditText) view.findViewById(R.id.new_crowd_title);
        crowdToggleSwitch = (Switch) view.findViewById(R.id.crowd_toggle_switch);
        crowdsAndFriendsLv = (ListView) view.findViewById(R.id.crowds_and_friends_lv);
        sendButton = (Button) view.findViewById(R.id.send_button);
        progressBar = (ProgressBar) view.findViewById(R.id.new_session_pb);

        // Get filepaths from the CameraFragment
        Bundle args = getArguments();
        videoFilepath = args.getString(T.FILEPATH);
        thumbnailFilepath = args.getString(T.THUMBNAIL_FILEPATH);

        // Hide everything until we get some data
        setVisibility(0);

        // Set onClick and onCheck listeners
        sendButton.setOnClickListener(this);
        crowdToggleSwitch.setOnCheckedChangeListener(this);
        
        // By default, show existing crowds
        getCrowds();

        return view;
    }

    // API requests
    private void getCrowds() {
        Intent apiIntent = new Intent(getActivity(), APIService.class);
        CrowdReceiver receiver = new CrowdReceiver(new Handler());
        apiIntent.putExtra(T.API_TYPE, T.GET_CROWDS);
        apiIntent.putExtra("receiver", receiver);
        getActivity().startService(apiIntent);
    }

    private void getFriends() {
        Intent apiIntent = new Intent(getActivity(), APIService.class);
        FriendListReceiver receiver = new FriendListReceiver(new Handler());
        apiIntent.putExtra(T.API_TYPE, T.GET_FRIENDS);
        apiIntent.putExtra("receiver", receiver);
        getActivity().startService(apiIntent);
    }

    @Override
    public void onClick(View v) {
        boolean isValid = true;
        Log.d(TAG, "Send button clicked");
        String invalidMessage = "";
        switch(v.getId())
        {
            case R.id.send_button:
                String sessionTitle, crowdId, crowdTitle;
                List<String> crowdUsernames;
                boolean useExistingCrowd = !isChecked;

                Intent api = new Intent(getActivity(), APIService.class);
                api.putExtra(T.API_TYPE, T.CREATE_SESSION);

                if(useExistingCrowd) {
                    // sessionTitle, useExistingCrowd, Filepath, and crowdId
                    sessionTitle = newSessionTitle.getText().toString();
                    crowdId = Integer.toString(crowdAdapter.getSelectedCrowdId());

                    // Validate fields
                    if(sessionTitle.length() <= 0) {
                        invalidMessage = "Forgetting a session title?";
                    } else if(crowdId.equals("-1")) {
                        invalidMessage = "Forgetting to pick a crowd?";
                    }

                    // Create session if fields are valid
                    if (invalidMessage.length() == 0) {
                        api.putExtra(T.SESSION_TITLE, sessionTitle);
                        api.putExtra(T.SESSION_USE_EXISTING_CROWD, useExistingCrowd);
                        api.putExtra(T.FILEPATH, videoFilepath);
                        api.putExtra(T.SESSION_CROWD_ID, crowdId);
                        api.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);

                        getActivity().startService(api);
                    }
                } else {
                    // sessionTitle, useExistingCrowd, Filepath, crowdTitle, crowdMembers
                    sessionTitle = newSessionTitle.getText().toString();
                    crowdTitle = newCrowdTitle.getText().toString();
                    crowdUsernames = friendsAdapter.getSelectedUsernames();
                    String crowdMembers = new Gson().toJson(crowdUsernames);

                    // Validate fields
                    if(sessionTitle.length() <= 0) {
                        invalidMessage = "Forgetting a session title?";
                    }
                    if(crowdTitle.length() <= 0) {
                        invalidMessage = "Forgetting a crowd title?";
                    }
                    if(crowdUsernames.size() <= 0) {
                        invalidMessage = "Add some friends to your crowd";
                    }

                    // Create session if fields are valid
                    if(invalidMessage.length() == 0) {
                        api.putExtra(T.SESSION_TITLE, sessionTitle);
                        api.putExtra(T.SESSION_USE_EXISTING_CROWD, useExistingCrowd);
                        api.putExtra(T.FILEPATH, videoFilepath);
                        api.putExtra(T.SESSION_CROWD_TITLE, crowdTitle);
                        api.putExtra(T.SESSION_CROWD_MEMBERS, crowdMembers);
                        api.putExtra(T.THUMBNAIL_FILEPATH, thumbnailFilepath);

                        getActivity().startService(api);
                    }
                }

                if(invalidMessage.length() > 0) {
                    // If fields were invalid, display toast warning
                    Toast.makeText(getActivity(), invalidMessage, Toast.LENGTH_SHORT).show();
                } else {
                    // Else go home
                    Intent homeActivity = new Intent(getActivity(), HomeActivity.class);
                    getActivity().startActivity(homeActivity);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        setVisibility(0);

        // Switch button listener
        this.isChecked = isChecked;
        Log.d(TAG, "switch checked: " + isChecked);

        if(isChecked) {
            // Creating a new crowd, so we'll GET friends/
            friendsAdapter = new FriendsFragmentAdapter(getActivity(), 0, null);
            crowdsAndFriendsLv.setAdapter(friendsAdapter);

            // Get the user's friends
            getFriends();
        } else {
            // Picking from an existing crowd, so we'll GET crowds/
            getCrowds();
        }
    }

    private void setVisibility(int type) {
        // Set the visibility of a group of views

        if(type == 0) {
            progressBar.setVisibility(View.VISIBLE);
            newCrowdTitle.setVisibility(View.GONE);
            crowdsAndFriendsLv.setVisibility(View.GONE);
            sendButton.setVisibility(View.GONE);
        } else if(type == 1) {
            // The crowd title textview should only be visible if isChecked
            int crowdTitleVisibility = (isChecked) ? View.VISIBLE : View.GONE;

            progressBar.setVisibility(View.GONE);
            newCrowdTitle.setVisibility(crowdTitleVisibility);
            crowdsAndFriendsLv.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        }
    }

    // Our result receivers
    private class CrowdReceiver extends ResultReceiver {
        public CrowdReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == T.GET_CROWDS) {

                Log.d(TAG, "APIService returned successful");
                Gson mGson = new Gson();

                // Convert json results to ArrayList<Crowd>
                String jsonResult = resultData.getString("crowdsJson");
                Crowd[] temp = mGson.fromJson(jsonResult, Crowds.class).getCrowds();
                crowds = new ArrayList<Crowd>(Arrays.asList(temp));

                // Create and assign adapter
                crowdAdapter = new CrowdAdapter(getActivity(), 0, crowds);
                crowdsAndFriendsLv.setAdapter(crowdAdapter);

                // Update adapter
                crowdAdapter.setItemList(crowds);
                crowdAdapter.notifyDataSetChanged();

                setVisibility(1);
            }
            else {
                Log.d(TAG, "getCrowds() failed");
            }
        }
    }

    private class FriendListReceiver extends ResultReceiver {
        public FriendListReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 1) {
                Log.d(TAG, "APIService returned successful with friends");
                String result = resultData.getString("result");
                Profile[] temp = (new Gson()).fromJson(result, Friends.class).getFriends();

                friends = new ArrayList<Profile>(Arrays.asList(temp));

                friendsAdapter.setItemList(friends);
                friendsAdapter.notifyDataSetChanged();

                setVisibility(1);
            } else {
                Log.d(TAG, "APIService get friends failed?");

            }
        }
    }
}
