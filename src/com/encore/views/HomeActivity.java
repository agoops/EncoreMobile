package com.encore.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.encore.Fragments.ProfileFragment;
import com.encore.R;
import com.encore.StartSession;

public class HomeActivity extends FragmentActivity {
    private static final String TAG = "HomeActivity";

    private DrawerLayout drawerLayout;
    private ListView leftDrawerList, rightDrawerList;
    private String[] leftDrawerTitles, rightDrawerTitles;
    private ActionBarDrawerToggle leftDrawerToggle;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Show live sessions by default
        Fragment liveFragment = new LiveFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.home_content_frame, liveFragment)
                .commit();

        this.context = this;

        getViews();
        setupNavDrawer();
    }

    private void getViews() {
        leftDrawerTitles = getResources().getStringArray(R.array.left_drawer_titles);
        rightDrawerTitles = getResources().getStringArray(R.array.right_drawer_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.home_left_drawer);
        rightDrawerList = (ListView) findViewById(R.id.home_right_drawer);
    }

    private void setupNavDrawer() {
        leftDrawerToggle = new ActionBarDrawerToggle(
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
                if(drawerLayout.isDrawerOpen(rightDrawerList) &&
                        drawerView != rightDrawerList) {
                    // Close right drawer if left is open
                    drawerLayout.closeDrawer(rightDrawerList);
                }
            }
        };
        drawerLayout.setDrawerListener(leftDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Set the adapter for the list view
        leftDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, leftDrawerTitles));
        leftDrawerList.setOnItemClickListener(new LeftDrawerItemClickListener());

        rightDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, rightDrawerTitles));
        rightDrawerList.setOnItemClickListener(new RightDrawerClickListener());

        // Hide overlay
        drawerLayout.setScrimColor(Color.TRANSPARENT);
    }

    private class LeftDrawerItemClickListener implements ListView.OnItemClickListener {
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
                    Fragment liveFragment = new LiveFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, liveFragment)
                            .commit();
                    break;
                case 1:
                    Fragment completeFragment = new CompleteFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, completeFragment)
                            .commit();
                    break;
                case 2:
                    Fragment profileFragment = new ProfileFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, profileFragment)
                            .commit();
                    break;
                default:
                    Fragment defaultFragment = new LiveFragment();
                    fm.beginTransaction()
                            .replace(R.id.home_content_frame, defaultFragment)
                            .commit();
                    break;
            }

            leftDrawerList.setItemChecked(position, true);
            setTitle(leftDrawerTitles[position]);
            drawerLayout.closeDrawer(leftDrawerList);
        }
    }

    private class RightDrawerClickListener implements ListView.OnItemClickListener {
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
                    Toast.makeText(context, "Rap Battle!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(context, "Freestyle!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "Rap Battle!", Toast.LENGTH_SHORT).show();
                    break;
            }

            // TODO: Prevent the item from remaining selected
//            rightDrawerList.setItemChecked(position, true);
            setTitle(rightDrawerTitles[position]);
            drawerLayout.closeDrawer(rightDrawerList);
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        leftDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        leftDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (leftDrawerToggle.onOptionsItemSelected(item)) {
            // If leftDrawerToggle returned true, it has handled the touch event
            return true;
        }

        switch(item.getItemId())
        {
            case R.id.action_video:
                // Launch a new session
                Log.d(TAG, "Launching StartSession");
                Intent intent = new Intent(this, StartSession.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
