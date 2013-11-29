package com.encore.views;

import com.encore.R;
import com.encore.StartSession2;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FriendsActivity extends FragmentActivity{
	private static final String TAG = "FriendsActivity";
	ViewPager viewPager;
	ActionBar actionBar;
	Fragment[] fragments;
	private static int FRIENDS = 0;
	private static int USERS = 1;
	private int NUM_TABS = 2;
	
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
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});
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
				actionBar.addTab(actionBar.newTab().setText("Pending")
						.setTabListener(tabListener));
				break;
			case 1:
				actionBar.addTab(actionBar.newTab().setText("Users")
						.setTabListener(tabListener));
				break;
			

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
				Fragment fragment3 = new FriendRequestsFragment();
				fragments[FRIENDS] = fragment3;
				return fragment3;
			case 1:
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
		Log.d(TAG, "Action Item selected");
		// Handle presses on action bar items
		switch(item.getItemId()) {
		case R.id.action_video:
			// Launch a new session
			Log.d(TAG, "Launching StartSession2");
			Intent intent = new Intent(this, StartSession2.class);
			startActivity(intent);
			return true;
		case R.id.newsfeed_activity:
			Intent newsfeedIntent = new Intent(this, NewsfeedActivity.class);
			startActivity(newsfeedIntent);
			return true;
		case R.id.friends_activity:
//			Intent friendsIntent = new Intent(this, FriendsActivity.class);
//			startActivity(friendsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
