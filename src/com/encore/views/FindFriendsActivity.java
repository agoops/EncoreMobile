package com.encore.views;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.SearchUsersFragmentAdapter;
import com.encore.models.SearchProfile;
import com.encore.models.User;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by babakpourkazemi on 1/7/14.
 */
public class FindFriendsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "FindFriendsActivity";
    private Context context;
    private ListView listview;
    private SearchUsersFragmentAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        context = this;

        getViews();
        setupActionBar();
        setupListView();

        handleIntent(getIntent());
    }

    private void getViews() {
        listview = (ListView) findViewById(R.id.search_results_lv);
    }

    private void setupActionBar() {
        // Enable up navigation
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupListView() {
        adapter = new SearchUsersFragmentAdapter(context, R.layout.search_list_row, null);
        listview.setAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "querying data: " + query);
            if(query.length() == 0) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT)
                        .show();
            } else {
                searchUsername(query);
            }
        }
    }

    public void searchUsername(String username) {
        Intent api = new Intent(this, APIService.class);
        SearchReceiver receiver = new SearchReceiver(new Handler());

        api.putExtra(T.API_TYPE, T.SEARCH_USERNAME);
        api.putExtra(T.RECEIVER, receiver);
        api.putExtra(T.USERNAME, username);
        startService(api);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.find_users_menu, menu);

        // Associate our searchable configuration with the widget
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_widget).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Set the searchview hint color
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setHintTextColor(Color.WHITE);

        // Expand the searchview
        searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                // Needed for proper UP navigation
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SearchReceiver extends ResultReceiver {
        public SearchReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                Log.d(TAG, "Search results received successfully");
                String result = data.getString("result");

                // Assign the adapter
                ArrayList<User> searchResults =
                        (ArrayList) (new Gson().fromJson(result, SearchProfile.class).getAsList());
                adapter.setItemList(searchResults);
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "GET /users/search/ unsuccessful");
            }
        }
    }
}
