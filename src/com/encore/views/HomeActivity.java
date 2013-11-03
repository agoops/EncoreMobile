package com.encore.views;

import Fragments.NewsfeedFragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.encore.R;

public class HomeActivity extends FragmentActivity {
	NewsfeedCollectionPagerAdapter mNewsfeedPagerAdapter;
	ViewPager vp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity_placeholder);
		
		final ActionBar actionBar = getActionBar();
		
		mNewsfeedPagerAdapter = new NewsfeedCollectionPagerAdapter(getSupportFragmentManager());
		vp = (ViewPager) findViewById(R.id.pager);
		vp.setAdapter(mNewsfeedPagerAdapter);
		
		// Specifies that tabs should be displayed in the action bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Swipe listeners for touch gestures
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
	    	public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
	    		// When the tab is selected, switch to the
	    		// corresponding page in the ViewPager
	    		vp.setCurrentItem(tab.getPosition());
	    	}
	    	
	    	@Override
	    	public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
	    		
	    	}
	    	
			@Override
			public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
				
			}
	    };
	    
	    // Add 3 tabs
	    for(int i=0; i<3; i++) {
	    	actionBar.addTab(
	    			actionBar.newTab()
	    				.setText("Tab " + (i+1))
	    				.setTabListener(tabListener));
	    }
	}
	
	public class NewsfeedCollectionPagerAdapter extends FragmentStatePagerAdapter {
		public NewsfeedCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int i) {
			return new NewsfeedFragment();
		}
		
		@Override
		public int getCount() {
			return 100; // ???
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return "OBJECT " + (position + 1);
		}
	}
}

