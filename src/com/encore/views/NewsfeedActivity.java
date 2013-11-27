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

import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.SessionTemp;
import com.encore.SessionViewAdapter;
import com.encore.StartSession2;
import com.encore.API.APIService;
import com.encore.API.models.Session;
import com.encore.API.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class NewsfeedActivity extends FragmentActivity {
	
	private static String tag = "NewsfeedActivity";
	ViewPager viewPager;
	ActionBar actionBar;
	Fragment[] fragments;
	private static int NEWSFEED = 0;
	private static int INBOX = 1;
	private int NUM_TABS = 2;
	
	private ArrayList<SessionTemp> completedSessions = new ArrayList<SessionTemp>();
	private ArrayList<SessionTemp> inboxSessions = new ArrayList<SessionTemp>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.home_activity);
		// customTitleBar();

		fragments = new Fragment[NUM_TABS];
		actionBar = getActionBar();

		Log.d(tag, "onCreate() reached");
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		setupActionBar(actionBar);
		Log.d(tag, "maybe got here?");
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
				Log.d(tag, "APISerivce returned successful with sessions");
				
				
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
				Log.d(tag, "APIService get session failed?");

			}
		}
	}
	
	private ArrayList<ArrayList<Session>> convertJsonToListOfSession(String json) {
		ArrayList<ArrayList<Session>> sessions = new ArrayList<ArrayList<Session>>();
		
		Gson gson = new Gson();
		ArrayList<Session> inbox = new ArrayList<Session>();
		ArrayList<Session> newsfeed = new ArrayList<Session>();
		sessions.add(newsfeed);
		sessions.add(inbox);
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
				Log.d(tag, "onTabSelected() called");
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
			

			}
		}
	}

	
	public class PagerAdapter extends FragmentStatePagerAdapter {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
			Log.d(tag, "PagerAdapter ctor");
		}

		@Override
		public Fragment getItem(int i) {
			Log.d(tag, "getItem() called with " + i);
			
			switch (i) {
			case 0:
				Fragment fragment3 = new VideoListViewFragment();
				fragments[NEWSFEED] = fragment3;
				return fragment3;
			case 1:
				Fragment fragment4 = new InboxListViewFragment();
				fragments[INBOX] = fragment4;
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
				return "Pending";
			case 1:
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
		Log.d("HOMEACTIVITY.JAVA", "Action Item selected");
		// Handle presses on action bar items
		switch(item.getItemId()) {
		case R.id.action_video:
			// Launch a new session
			Log.d("HOMEACTIVITY.JAVA", "Launching StartSession2");
			Intent intent = new Intent(this, StartSession2.class);
			startActivity(intent);
			return true;
		case R.id.newsfeed_activity:
//			Intent newsfeedIntent = new Intent(this, NewsfeedActivity.class);
//			startActivity(newsfeedIntent);
			return true;
		case R.id.friends_activity:
			Intent friendsIntent = new Intent(this, FriendsActivity.class);
			startActivity(friendsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
