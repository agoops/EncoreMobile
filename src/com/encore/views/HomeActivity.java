package com.encore.views;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.encore.Fragments.ProfileFragment;
import com.encore.R;

public class HomeActivity extends FragmentActivity {
    private static final String TAG = "HomeActivity";

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private String[] drawerTitles;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Show inbox by default
        Fragment inboxFragment = new InboxListViewFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.home_content_frame, inboxFragment)
                .commit();

        // Get views
        drawerTitles = getResources().getStringArray(R.array.drawer_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        drawerList = (ListView) findViewById(R.id.home_left_drawer);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_navigation_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Hide overlay
        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }

        /* Swaps fragments in the main content view */
        private void selectItem(int position) {
            FragmentManager fm = getSupportFragmentManager();

            switch(position)
            {
                case 0:
                    Fragment inboxFragment = new InboxListViewFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, inboxFragment)
                            .commit();
                    break;
                case 1:
                    Fragment profileFragment = new ProfileFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, profileFragment)
                            .commit();
                    break;
                default:
                    Fragment defaultFragment = new InboxListViewFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, defaultFragment)
                            .commit();
                    break;
            }

            drawerList.setItemChecked(position, true);
            setTitle(drawerTitles[position]);
            drawerLayout.closeDrawer(drawerList);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            // If drawerToggle returne true, it has handled the touch event
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
