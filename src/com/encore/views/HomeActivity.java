package com.encore.views;

import java.util.ArrayList;

import util.T;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.SessionViewAdapter;
import com.encore.StartSession2;
import com.encore.API.APIService;
import com.encore.API.models.Session;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class HomeActivity extends FragmentActivity {
	private static final String TAG = "HomeActivity";
	ViewPager viewPager;
	ListView listView;
	ActionBar actionBar;
	Fragment[] fragments;
	private static int NEWSFEED = 0;
	private static int INBOX = 1;
	private static int PENDING_REQUESTS = 2;
	private static int USERS = 3;

	// Change this to take out tabs from HomeActivity
	private static int NUM_TABS = 4;
	boolean customTitleSupported;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// check if custom title is supported BEFORE setting the content view!
		// TODO doesn't work. do later;
		// customTitleSupported =
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.home_activity);
		// customTitleBar();

		fragments = new Fragment[NUM_TABS];
		actionBar = getActionBar();

		Log.d(TAG, "onCreate() reached");
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		setupActionBar(actionBar);
		Log.d(TAG, "maybe got here?");
		viewPager.setOffscreenPageLimit(NUM_TABS - 1);
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});
		getAllSessions();
	}

	public void customTitleBar() {
		// set up custom title
		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.customtitlebar);
			TextView title = (TextView) findViewById(R.id.title);
			title.setText(getText(R.string.app_name).toString());

		}
	}

	private void startNewSession() {
		Log.d(TAG, "Button clicked!");
	}

	private void setupActionBar(ActionBar actionBar) {
		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO
			}

			@Override
			public void onTabSelected(Tab tab,
					android.app.FragmentTransaction ft) {
				Log.d(TAG, "onTabSelected() called");
				viewPager.setCurrentItem(tab.getPosition(), true);
			}

			@Override
			public void onTabUnselected(Tab tab,
					android.app.FragmentTransaction ft) {
				// TODO
			}
		};
		// Add title to tabs
		for (int i = 0; i < NUM_TABS; i++) {
			switch (i) {
			case 0:
				actionBar.addTab(actionBar.newTab().setText("Newsfeed")
						.setTabListener(tabListener));
				break;
			case 1:
				actionBar.addTab(actionBar.newTab().setText("Inbox")
						.setTabListener(tabListener));
				break;
			case 2:
				actionBar.addTab(actionBar.newTab().setText("Requests")
						.setTabListener(tabListener));
				break;
			case 3:
				actionBar.addTab(actionBar.newTab().setText("Users")
						.setTabListener(tabListener));

			}
		}
	}

	public class PagerAdapter extends FragmentStatePagerAdapter {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
			Log.d(TAG, "PagerAdapter ctor");
		}

		@Override
		public Fragment getItem(int i) {
			Log.d(TAG, "getItem() called with " + i);
			
			switch (i) {
			case 0:
				Fragment fragment1 = new VideoListViewFragment();
				Bundle args = new Bundle();
				fragment1.setArguments(args);
				fragments[NEWSFEED] = fragment1;
				return fragment1;
			case 1:
				Fragment fragment2 = new InboxListViewFragment();
				fragments[INBOX] = fragment2;
				return fragment2;
			case 2:
				Fragment fragment3 = new FriendRequestsFragment();
				fragments[PENDING_REQUESTS] = fragment3;
				return fragment3;
			case 3:
				Fragment fragment4 = new UsersFragment();
				fragments[USERS] = fragment4;
				return fragment4;
			}

			return null;
		}

		@Override
		public int getCount() {
			return NUM_TABS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Home page";
			case 1:
				return "Inbox";
			case 2:
				return "Requests";
			case 3:
				return "Users";
			default:
				return null;
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Action Item selected");
		// Handle presses on action bar items
		switch(item.getItemId()) {
		case R.id.action_video:
			// Launch a new session
			Log.d(TAG, "Launching StartSession2");
			Intent intent = new Intent(this, StartSession2.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	 
    private void getAllSessions() {
            Intent apiIntent = new Intent(this, APIService.class);
            apiIntent.putExtra(T.API_TYPE, T.GET_SESSIONS);
            SessionListReceiver mReceiver = new SessionListReceiver(new Handler());
            apiIntent.putExtra(T.RECEIVER, mReceiver);
            startService(apiIntent);
    }
    
    private class SessionListReceiver extends ResultReceiver {
            public SessionListReceiver(Handler handler) {
                    super(handler);
                    // TODO Auto-generated constructor stub
            }

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == 1) {
                            Log.d(TAG, "APISerivce returned successful with sessions");
                            
                            String result = resultData.getString("result");
                            ArrayList<ArrayList<Session>> sessions = convertJsonToListOfSession(result);
                            
                            SessionViewAdapter nAdapter = ((VideoListViewFragment) fragments[NEWSFEED])
                                            .getAdapter();
                            nAdapter.setItemList(sessions.get(NEWSFEED));
                            nAdapter.notifyDataSetChanged();
                            
                            InboxViewAdapter iAdapter = ((InboxListViewFragment) fragments[INBOX]).getAdapter();
                            iAdapter.setItemList(sessions.get(INBOX));
                            iAdapter.notifyDataSetChanged();
                            
                    } else {
                            Log.d(TAG, "APIService get session failed?");

                    }
            }
    }
    
    private ArrayList<ArrayList<Session>> convertJsonToListOfSession(String json) {
            ArrayList<ArrayList<Session>> sessions = new ArrayList<ArrayList<Session>>();
            
            Gson gson = new Gson();
            ArrayList<Session> inbox = new ArrayList<Session>();
            ArrayList<Session> newsfeed = new ArrayList<Session>();
            sessions.add(NEWSFEED, newsfeed);
            sessions.add(INBOX, inbox);
            JsonParser jsonParser = new JsonParser();
            JsonArray sessionsJson = new JsonArray();
            
            sessionsJson = jsonParser.parse(json).getAsJsonObject()
                            .getAsJsonArray("sessions");
            for (JsonElement j : sessionsJson) {
                    Session session = gson.fromJson(j, Session.class);
                    if (session.isComplete()) {
                            newsfeed.add(session);
                    } else{
                            inbox.add(session);
                    }
            }
            
            return sessions;
            
    }
}
