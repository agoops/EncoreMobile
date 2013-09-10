package Fragments;//package Fragments;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import com.bpkazemi.Encore.R;
//import com.facebook.*;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
//import com.facebook.widget.ProfilePictureView;
//
//import java.util.Arrays;
//
///**
// * Created with IntelliJ IDEA.
// * User: babakpourkazemi
// * Date: 9/7/13
// * Time: 12:10 PM
// * To change this template use File | Settings | File Templates.
// */
//public class MainFragment extends Fragment {
//
//    /* ----------- Private Vars ----------- */
//    /* ------------------------------------ */
//
//    // Callback to listen for session changes
//    private Session.StatusCallback callback = new Session.StatusCallback() {
//        @Override
//        public void call(Session session, SessionState state, Exception exception) {
//            onSessionStateChange(session, state, exception);
//        }
//    };
//    // Manages auth flow and used for session management
//    private UiLifecycleHelper uiHelper;
//
//    private ProfilePictureView profilePictureView;
//    private TextView userNameView;
//
//    // Decides whether to update onActivityResult()
//    private static final int REAUTH_ACTIVITY_CODE = 100;
//
//    /* ------------------------------------ */
//    /* ------------------------------------ */
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater,
//                             ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.main, container, false);
//
//        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
//        authButton.setFragment(this); // To allow fragment to receive onActivityResult
//        authButton.setReadPermissions(Arrays.asList("email", "read_friendlists"));
//
//        // Find the user's profile picture custom view
//        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
//        profilePictureView.setCropped(true);
//
//        // Find the user's name view
//        userNameView = (TextView) view.findViewById(R.id.selection_user_name);
//
//        // Check for an open session
//        Session session = Session.getActiveSession();
//        if(session != null && session.isOpened()) {
//            // Get the user's data
//            makeMeRequest(session);
//        }
//
//        return view;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        uiHelper = new UiLifecycleHelper(getActivity(), callback);
//        uiHelper.onCreate(savedInstanceState);
//    }
//
//    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
////        if(state.isOpened()) {
////            Log.i("MainFragment", "Logged in...");
////            // User is logged in, change UI accordingly
////        } else {
////            Log.i("MainFragment", "Logged out...");
////        }
//
//        if (session != null && session.isOpened()) {
//            // Get the user's data.
//            makeMeRequest(session);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        uiHelper.onResume();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REAUTH_ACTIVITY_CODE) {
//            uiHelper.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        uiHelper.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        uiHelper.onDestroy();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        uiHelper.onSaveInstanceState(outState);
//    }
//
//    // ------------- Helpers -------------
//    private void makeMeRequest(final Session session) {
//        // Make an API call to get user data and define a
//        // new callback to handle the response.
//        Request request = Request.newMeRequest(session,
//                new Request.GraphUserCallback() {
//                    @Override
//                    public void onCompleted(GraphUser user, Response response) {
//                        // If the response is successful
//                        if (session == Session.getActiveSession()) {
//                            if (user != null) {
//                                // Set the id for the ProfilePictureView
//                                // view that in turn displays the profile picture.
//                                profilePictureView.setProfileId(user.getId());
//                                // Set the Textview's text to the user's name.
//                                userNameView.setText(user.getName());
//                            }
//                        }
//                        if (response.getError() != null) {
//                            // Handle errors, will do so later.
//                        }
//                    }
//                });
//        request.executeAsync();
//    }
//}