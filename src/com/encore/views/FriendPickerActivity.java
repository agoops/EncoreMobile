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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.SessionFlowManager;
import com.encore.TabFriendsAdapter;
import com.encore.models.Friends;
import com.encore.models.Profile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by babakpourkazemi on 3/14/14.
 */
public class FriendPickerActivity extends Activity {
    private static final String TAG = "FriendPickerActivity";
    private List<Profile> friends;
    private Context context;

    private ListView friendsLv;
    private TabFriendsAdapter adapter;
    private ArrayList<Profile> friendsList;
    private ProgressBar progressBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_picker);
        context = this;

        getViews();
        setupActionBar();
        setupListView();
    }

    // Grab our views
    private void getViews() {
        friendsLv = (ListView) findViewById(R.id.friend_picker_listview);
        progressBar = (ProgressBar) findViewById(R.id.friend_picker_progress);
    }

    private void setupActionBar() {
        // Enable up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupListView() {
        adapter = new TabFriendsAdapter(context, R.layout.tab_friends_list_row, null);
        friendsLv.setAdapter(adapter);
        getFriends();

        friendsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Rap battle!
                String username = friendsList.get(position).getUsername();
                Intent createRapBattle = new Intent(context, SessionFlowManager.class);
                createRapBattle.putExtra(T.IS_BATTLE, true);
                createRapBattle.putExtra(T.BATTLE_RECEIVER, username);
                startActivity(createRapBattle);
            }
        });
    }

    private void getFriends() {
        Intent api = new Intent(context, APIService.class);
        ResultReceiver receiver = new FriendsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_FRIENDS);
        api.putExtra(T.RECEIVER, receiver);
        startService(api);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                // Needed for proper UP navigation
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FriendsReceiver extends ResultReceiver {
        public FriendsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Convert the api request from JSON
                String result = data.getString("result");
                Log.d(TAG, "result from apiservice is: " + result);

                Friends friends = (new Gson()).fromJson(result, Friends.class);
                Profile[] friendsArray = friends.getFriends();
                friendsList = new ArrayList<Profile>(Arrays.asList(friendsArray));
                adapter.setItemList(friendsList);
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }
}
