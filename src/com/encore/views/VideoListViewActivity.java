package com.encore.views;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ListView;

import com.encore.R;

public class VideoListViewActivity extends FragmentActivity {
	private static String tag = "VideoListViewActivity";
	ViewPager viewPager;
	ListView listView;
	ActionBar actionBar;
	Fragment[] fragments;
	private static int HOME = 0;
	private static int INBOX = 1;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_list_activity);
		fragments = new Fragment[2];
		actionBar = getActionBar();
		

		Log.d(tag, "onCreate() reached");
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
		setupActionBar(actionBar);
		viewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
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
			public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
				//TODO
			}
			@Override
			public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
				Log.d(tag, "onTabSelected() called");
				viewPager.setCurrentItem(tab.getPosition(),true);
			}
			@Override
			public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
				//TODO
			}
		};
		//Add title to tabs
		for (int i = 0; i < 2; i++) {
			switch (i) {
			case 0:
				actionBar.addTab(actionBar.newTab().setText("Home")
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
				Fragment fragment1 = new VideoListViewFragment();
				Bundle args = new Bundle();
				fragment1.setArguments(args);
				fragments[HOME] = fragment1;
				return fragment1;
			case 1:
				Fragment fragment2 = new InboxListViewFragment();
				fragments[INBOX] = fragment2;
				return fragment2;
			}

			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Home page";
			case 1:
				return "Inbox";
			}
			System.out.println("Got here for some reason");
			return null;
		}
	}

}
