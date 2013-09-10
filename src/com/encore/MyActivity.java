package com.encore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.bpkazemi.Encore.API.APIService;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;


public class MyActivity extends FragmentActivity {

    /* ------------ Private Vars ------------- */
    /* --------------------------------------- */
//    private MainFragment mainFragment;

    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS+1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    // Monitor activity state
    private boolean isResumed = false;

    // Callback to listen for session changes by
    // overriding call method and invoking onSessionStateChange
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    // Manages auth flow and used for session management
    private UiLifecycleHelper uiHelper;

    // For the menu
    private MenuItem settings;

    private APIService apiService;
    private static final String POST_URI = "http://158.130.153.103:8000/";

    /* --------------------------------------- */
    /* --------------------------------------- */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        hideFragments();

//        if (savedInstanceState == null) {
//            // Add the fragment on initial activity setup
//            mainFragment = new MainFragment();
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(android.R.id.content, mainFragment)
//                    .commit();
//        } else {
//            // Or set the fragment from restored state info
//            mainFragment = (MainFragment) getSupportFragmentManager()
//                    .findFragmentById(android.R.id.content);
//        }

    }

    // Handle authenticated vs non-authenticated for newly instantiated fragments
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            showFragment(SELECTION, false);

            PostRequestAsync postRequest = new PostRequestAsync();
            postRequest.execute(null, null, null);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            showFragment(SPLASH, false);
        }
    }

    // Listens for session state changes - invoked by Session.StatusCallback
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if(isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            // Get num entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for(int i=0; i<backStackSize; i++) {
                manager.popBackStack();
            }

            if(state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment
                showFragment(SELECTION, false);
            } else if(state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                showFragment(SPLASH, false);
            }
        }
    }

    /* ------------- Asynctask request ------------  */
    /* --------------------------------------------  */
    public class PostRequestAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Session session = Session.getActiveSession();

            // Send data to server
            Map<String, String> postData = new HashMap<String, String>();
            postData.put("access_token", session.getAccessToken());

            apiService = new APIService();
            HttpResponse response = apiService.post(POST_URI, postData);
            Log.d("+=+=+=+=Response: ", response.toString());
            return null;
        }
    }


    // Prepare the options menu display
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Only add the menu when the selection fragment is showing
        if(fragments[SELECTION].isVisible()) {
            if(menu.size() == 0) {
                settings = menu.add(R.string.settings);
            }
            return true;
        } else {
            menu.clear();
            settings = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(settings)) {
            showFragment(SETTINGS, true);
            return true;
        }
        return false;
    }


    /* ----------- Obligatory jazz ----------- */
    /* --------------------------------------- */

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    /* ----------- Helper Methods ------------  */
    /* ---------------------------------------  */

    // Hide all fragments - used onCreate
    private void hideFragments() {
        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
    }

    // Show target fragment, hide all others
    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i=0; i<fragments.length; i++) {
            if(i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if(addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }


}


// ~~~~~~~~~~~~ First facebook tutorial
// start Facebook Login
//        Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//            // callback when session changes state
//            @Override
//            public void call(Session session, SessionState state, Exception exception) {
//                if (session.isOpened()) {
//
//                    // make request to the /me API
//                    Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//                        // callback after Graph API response with user object
//                        @Override
//                        public void onCompleted(GraphUser user, Response response) {
//                            if (user != null) {
//                                TextView welcome = (TextView) findViewById(R.id.welcome);
//                                welcome.setText("Hello " + user.getName() + "!");
//                            }
//                        }
//                    }).executeAsync();
//                }
//            }
//        });

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
// From the first fb login tutorial
//        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//    }
