package com.encore.views;

import Fragments.ExploreFragment;
import Fragments.NewsfeedFragment;
import Fragments.ProfileFragment;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.encore.R;

public class HomeActivity extends FragmentActivity {
	HomeActivityPagerAdapter mHomeActivityPagerAdapter;
	ViewPager vp;
	private static final int NUM_VIEWS = 4; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity_placeholder);
		
		final ActionBar actionBar = getActionBar();
		
		// Passes the fragment corresponding to the selected tab
		// to the viewpager (where each child view is a separate tab)
		mHomeActivityPagerAdapter = 
				new HomeActivityPagerAdapter(getSupportFragmentManager());
		vp = (ViewPager) findViewById(R.id.pager);
		vp.setAdapter(mHomeActivityPagerAdapter);
		
		// Specifies that tabs should be displayed in the action bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Swipe listeners
		vp.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages,
						// select the corresponding tab.
						getActionBar().setSelectedNavigationItem(position);
					}
				});
		
		// Tab listeners for when tabs change
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	    	@Override
	    	public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) { // use support library instead.
	    		// When the tab is selected, switch to the
	    		// corresponding page in the ViewPager
	    		vp.setCurrentItem(tab.getPosition());
	    	}
	    	
	    	@Override
	    	public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
	    		
	    	}
	    	
			@Override
			public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
				// Ignore
			}
	    };
	    
	    actionBar.addTab(actionBar.newTab()
	    		.setText("Feed")
	    		.setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab()
	    		.setText("Explore")
	    		.setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab()
	    		.setText("Sessions")
	    		.setTabListener(tabListener));
	    actionBar.addTab(actionBar.newTab()
	    		.setText("Profile")
	    		.setTabListener(tabListener));
	}
	
	
	public class HomeActivityPagerAdapter extends FragmentStatePagerAdapter {
		public HomeActivityPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int i) {
			switch(i) {
			case 0:
				 return new NewsfeedFragment();
			case 1:
				 return new ExploreFragment();
			case 2:
				// return new SessionsFragment()
				break;
			case 3:
				 return new ProfileFragment();
			default:
				 return new NewsfeedFragment();
			}
			return new NewsfeedFragment();
		}
		
		@Override
		public int getCount() {
			return NUM_VIEWS;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return "OBJECT " + (position + 1);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on action bar items
		switch(item.getItemId()) {
		case R.id.action_video:
			// Launch all the session stuff!
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

