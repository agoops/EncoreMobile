package com.encore.views;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.encore.R;
import com.encore.StartSession2;

public class HomeActivity extends FragmentActivity {
	private static final String TAG = "HomeActivity";
	ViewPager viewPager;
	ListView listView;
	ActionBar actionBar;
	Fragment[] fragments;
//	private static int NEWSFEED = 0;
	private static int INBOX = 0;
	private static int PENDING_REQUESTS = 1;
	private static int USERS = 2;

	// Change this to take out tabs from HomeActivity
	private static int NUM_TABS = 3;
	boolean customTitleSupported;

	// Navbar logic
	private CharSequence mTitle;
	private String[] navbarTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle drawerToggle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.home_activity_navbar);
		
		// Initially set the view to your inbox
		Fragment fragment = new InboxListViewFragment();
		FragmentManager fm = getSupportFragmentManager();
		fm.beginTransaction().replace(R.id.fragment_frame, fragment)
			.commit();
		
		// ==== New Nav Drawer ====
		fragments = new Fragment[NUM_TABS];
		mTitle = getTitle();
		navbarTitles = getResources().getStringArray(R.array.navbar_titles);
		mDrawerList = (ListView) findViewById(R.id.nav_drawer); 
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		// Set the adapter for the listview
		mDrawerList.setAdapter(new ArrayAdapter<String> (this, R.layout.drawer_list_item, 
				R.id.drawer_title, navbarTitles));
		
		// Clicking a navbar title should launch the respective fragment
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		// Set a drawer toggle listener
		drawerToggle = new ActionBarDrawerToggle(
				this,								// host activity 
				mDrawerLayout, 						// DrawerLayout object 
				R.drawable.ic_navigation_drawer, 	// nav drawer icon
				R.string.drawer_open, 				// 'open drawer' description
				R.string.drawer_close) {			// 'close drawer' description
			
			// Called when a drawer is in a completely closed state			 
			public void onDrawerClosed(View v) {
				getActionBar().setTitle(mTitle);
			}
			
			// Called when a drawer is in a completely open state
			public void onDrawerOpened(View v) {
				getActionBar().setTitle(mTitle);
			}
		};
		
		// Enable the ActionBar to toggle the nav drawer 
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		mDrawerLayout.setDrawerListener(drawerToggle);
		
		
		// ============== OLD TABS =================		
		
		// check if custom title is supported BEFORE setting the content view!
		// TODO doesn't work. do later;
		// customTitleSupported =
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		// customTitleBar();

//		fragments = new Fragment[NUM_TABS];
//		actionBar = getActionBar();
//
//		Log.d(TAG, "onCreate() reached");
//		viewPager = (ViewPager) findViewById(R.id.pager);
//		viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
//		setupActionBar(actionBar);
//		Log.d(TAG, "maybe got here?");
//		viewPager.setOffscreenPageLimit(0);
//		viewPager
//				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//					@Override
//					public void onPageSelected(int position) {
//						// When swiping between pages, select the
//						// corresponding tab.
//						getActionBar().setSelectedNavigationItem(position);
//					}
//				});
//		getAllSessions();
	}
	
	// OnItemClick, load the appropriate fragment
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectItem(position);
		}
	}
		
	// Swaps the selected fragment into view
	private void selectItem(int position) { // would the title be a better switch? This requires synch'ing between the titles array.
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new InboxListViewFragment();
			fragments[INBOX] = fragment;
			break;
		case 1:
			fragment = new FriendRequestsFragment();
			fragments[PENDING_REQUESTS] = fragment;
			break;
		case 2:
			fragment = new UsersFragment();
			fragments[USERS] = fragment;
			break;
		default:
			Log.d(TAG, "Uh-oh, shouldn't have gotten here.");
			break;
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
			.replace(R.id.fragment_frame, fragment)
			.commit();
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
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
		// Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        
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
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
	
	
// ====== OLD TABS =====
	
//	public void customTitleBar() {
//		// set up custom title
//		if (customTitleSupported) {
//			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//					R.layout.customtitlebar);
//			TextView title = (TextView) findViewById(R.id.title);
//			title.setText(getText(R.string.app_name).toString());
//
//		}
//	}
//
//	private void startNewSession() {
//		Log.d(TAG, "Button clicked!");
//	}
//
//	private void setupActionBar(ActionBar actionBar) {
//		// Specify that tabs should be displayed in the action bar.
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//		// Create a tab listener that is called when the user changes tabs.
//		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
//
//			@Override
//			public void onTabReselected(Tab tab,
//					android.app.FragmentTransaction ft) {
//				// TODO
//			}
//
//			@Override
//			public void onTabSelected(Tab tab,
//					android.app.FragmentTransaction ft) {
//				Log.d(TAG, "onTabSelected() called");
//				viewPager.setCurrentItem(tab.getPosition(), true);
//			}
//
//			@Override
//			public void onTabUnselected(Tab tab,
//					android.app.FragmentTransaction ft) {
//				// TODO
//			}
//		};
//		// Add title to tabs
//		for (int i = 0; i < NUM_TABS; i++) {
//			switch (i) {
////			case 0:
////				actionBar.addTab(actionBar.newTab().setText("Newsfeed")
////						.setTabListener(tabListener));
////				break;
//			case 0:
//				actionBar.addTab(actionBar.newTab().setText("Inbox")
//						.setTabListener(tabListener));
//				break;
//			case 1:
//				actionBar.addTab(actionBar.newTab().setText("Requests")
//						.setTabListener(tabListener));
//				break;
//			case 2:
//				actionBar.addTab(actionBar.newTab().setText("Users")
//						.setTabListener(tabListener));
//
//			}
//		}
//	}
//
//	public class PagerAdapter extends FragmentStatePagerAdapter {
//		public PagerAdapter(FragmentManager fm) {
//			super(fm);
//			Log.d(TAG, "PagerAdapter ctor");
//		}
//
//		@Override
//		public Fragment getItem(int i) {
//			Log.d(TAG, "getItem() called with " + i);
//			
//			switch (i) {
////			case 0:
////				Fragment fragment1 = new VideoListViewFragment();
////				Bundle args = new Bundle();
////				fragment1.setArguments(args);
////				fragments[NEWSFEED] = fragment1;
////				return fragment1;
//			case 0:
//				Fragment fragment2 = new InboxListViewFragment();
//				fragments[INBOX] = fragment2;
//				return fragment2;
//			case 1:
//				Fragment fragment3 = new FriendRequestsFragment();
//				fragments[PENDING_REQUESTS] = fragment3;
//				return fragment3;
//			case 2:
//				Fragment fragment4 = new UsersFragment();
//				fragments[USERS] = fragment4;
//				return fragment4;
//			}
//
//			return null;
//		}
//
//		@Override
//		public int getCount() {
//			return NUM_TABS;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position) {
//			switch (position) {
////			case 0:
////				return "Home page";
//			case 0:
//				return "Inbox";
//			case 1:
//				return "Requests";
//			case 2:
//				return "Users";
//			default:
//				return null;
//			}
//		}
//	}
	
}
