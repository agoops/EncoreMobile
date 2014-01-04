package com.encore.views;

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
import android.widget.ListView;
import android.widget.TextView;

import com.encore.Fragments.ProfileFragment;
import com.encore.R;
import com.encore.StartSession;

public class HomeActivity extends FragmentActivity {
	private static final String TAG = "HomeActivity";
	ViewPager viewPager;
	ListView listView;
	ActionBar actionBar;
	Fragment[] fragments;
//	private static int NEWSFEED = 0;
	private static int INBOX = 0;
//	private static int PENDING_REQUESTS = 1;
//	private static int USERS = 2;
	private static int PROFILE = 1;

	// Change this to take out tabs from HomeActivity
	private static int NUM_TABS = 2;
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

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		setupActionBar(actionBar);
		viewPager.setOffscreenPageLimit(0);
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});
		
//		getAllSessions();
	}

	public void customTitleBar() {
		// set up custom title
		if (customTitleSupported) {
//			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//					R.layout.customtitlebar);
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
			}
		};
		// Add title to tabs
		for (int i = 0; i < NUM_TABS; i++) {
			switch (i) {
//			case 0:
//				actionBar.addTab(actionBar.newTab().setText("Newsfeed")
//						.setTabListener(tabListener));
//				break;
			case 0:
				actionBar.addTab(actionBar.newTab().setText("Inbox")
						.setTabListener(tabListener));
				break;
//			case 1:
//				actionBar.addTab(actionBar.newTab().setText("Users")
//						.setTabListener(tabListener));
//				break;
			case 1:
				actionBar.addTab(actionBar.newTab().setText("Profile")
						.setTabListener(tabListener));
				break;
//			case 2:
//				actionBar.addTab(actionBar.newTab().setText("Users")
//						.setTabListener(tabListener));

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
//			case 0:
//				Fragment fragment1 = new VideoListViewFragment();
//				Bundle args = new Bundle();
//				fragment1.setArguments(args);
//				fragments[NEWSFEED] = fragment1;
//				return fragment1;
			case 0:
				Fragment fragment2 = new InboxListViewFragment();
				fragments[INBOX] = fragment2;
				return fragment2;
//			case 1:
//				Fragment fragment3 = new FriendRequestsFragment();
//				fragments[PENDING_REQUESTS] = fragment3;
//				return fragment3;
//			case 2:
//				Fragment fragment4 = new UsersFragment();
//				fragments[USERS] = fragment4;
//				return fragment4;
			case 1:
				Fragment profileFragment = new ProfileFragment();
				fragments[PROFILE] = profileFragment;
				return profileFragment;
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
//			case 0:
//				return "Home page";
			case 0:
				return "Inbox";
//			case 1:
//				return "Requests";
//			case 2:
//				return "Users";
            case 1:
                return "Profile";
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
//			Intent intent = new Intent(this, StartSession2.class);

//			Intent intent = new Intent(this, CameraActivity2.class);
            Intent intent = new Intent(this, StartSession.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
