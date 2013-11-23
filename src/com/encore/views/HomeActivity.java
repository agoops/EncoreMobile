package com.encore.views;

import util.T;
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
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.encore.R;
import com.encore.StartSession2;

public class HomeActivity extends FragmentActivity {
	private static String tag = "VideoListViewActivity";
	ViewPager viewPager;
	ListView listView;
	ActionBar actionBar;
	Fragment[] fragments;
	private static int HOME = 0;
	private static int INBOX = 1;
	private static int FRIENDS = 2;
	private static int NUM_VIEWS = T.NUM_VIEWS;
	boolean customTitleSupported;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// check if custom title is supported BEFORE setting the content view!
		//TODO doesn't work. do later;
		//customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.home_activity);
		//customTitleBar();

		fragments = new Fragment[3];
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
		for (int i = 0; i < NUM_VIEWS; i++) {
			switch (i) {
			case 0:
				actionBar.addTab(actionBar.newTab().setText("Home")
						.setTabListener(tabListener));
				break;
			case 1:
				actionBar.addTab(actionBar.newTab().setText("Inbox")
						.setTabListener(tabListener));
				break;
			case 2:
				actionBar.addTab(actionBar.newTab().setText("Friends")
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
				Fragment fragment1 = new VideoListViewFragment();
				Bundle args = new Bundle();
				fragment1.setArguments(args);
				fragments[HOME] = fragment1;
				return fragment1;
			case 1:
				Fragment fragment2 = new InboxListViewFragment();
				fragments[INBOX] = fragment2;
				return fragment2;
			case 2:
				Fragment fragment3 = new FriendsFragment();
				fragments[FRIENDS] = fragment3;
				return fragment3;
			}

			return null;
		}

		@Override
		public int getCount() {
			return NUM_VIEWS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Home page";
			case 1:
				return "Inbox";
			case 2:
				return "Friends";
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
