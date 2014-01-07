package com.encore.views;

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
import android.view.View;

import com.encore.Fragments.ProfileFragment;
import com.encore.R;
import com.encore.StartSession;

public class HomeActivity extends FragmentActivity {
    private static final String TAG = "HomeActivity";
    ViewPager viewPager;
    Fragment[] fragments;
    private static final int INBOX = 0;
    private static final int PROFILE = 1;

    private static int NUM_TABS = 2;

    // TODO: By default, set the inbox action item to be selected

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Init our fragments holder
        fragments = new Fragment[NUM_TABS];

        // Init our viewpager
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        viewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        switch (position) {
                            case INBOX:
                                // Set actionbar menu icon
                                setActionItemSelected(R.id.action_inbox);
                                break;
                            case PROFILE:
                                // Set actionbar menu icon
                                setActionItemSelected(R.id.action_profile);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
            Log.d(TAG, "PagerAdapter constructor");
        }

        @Override
        public Fragment getItem(int i) {
            Log.d(TAG, "getItem() called with " + i);

            switch (i) {
                case 0:
                    Fragment fragment2 = new InboxListViewFragment();
                    // This is needed to set the default menu item state
                    fragments[INBOX] = fragment2;
                    return fragment2;
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
                case 0:
                    return "Inbox";
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
        switch(item.getItemId())
        {
            case R.id.action_video:
                // Launch a new session
                Log.d(TAG, "Launching StartSession");
                Intent intent = new Intent(this, StartSession.class);
                startActivity(intent);

                return true;
            case R.id.action_inbox:
                viewPager.setCurrentItem(INBOX);

                return true;
            case R.id.action_profile:
                viewPager.setCurrentItem(PROFILE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setActionItemSelected(int selectedId) {
        View inboxItem = findViewById(R.id.action_inbox);
        View profileItem = findViewById(R.id.action_profile);

        switch(selectedId)
        {
            case R.id.action_inbox:
                // Mark the inbox menu item
                inboxItem.setSelected(true);
                profileItem.setSelected(false);

                break;
            case R.id.action_profile:
                // Mark the profile menu item
                inboxItem.setSelected(false);
                profileItem.setSelected(true);

                break;
            default:
                // Else deselect everything
                inboxItem.setSelected(false);
                profileItem.setSelected(false);

                break;
        }
    }
}
