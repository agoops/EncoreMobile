package Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.encore.EncoreApplication;
import com.encore.PickerActivity;
import com.encore.R;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: babakpourkazemi
 * Date: 9/7/13
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectionFragment extends Fragment {

    private static final String TAG = "SelectionFragment";
    private ProfilePictureView profilePictureView;
    private TextView userNameView;

    private ListView listView;
    private List<BaseListElement> listElements;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private UiLifecycleHelper uiHelper;

    private static final int REAUTH_ACTIVITY_CODE = 100;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.selection, container, false);

        // Find the user's profile picture custom view
        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);

        // Find the user's name view
        userNameView = (TextView) view.findViewById(R.id.selection_user_name);


        // -- Setting up the list view --
        // Find the list view
        listView = (ListView) view.findViewById(R.id.selection_list);

        // Set up the list view items, based on a list of
        // BaseElement items
        listElements = new ArrayList<BaseListElement>();
        // Add an item for the friend picker
        listElements.add(new PeopleListElement(0));

        if(savedInstanceState != null) {
            // Restore the state for each list element
            for(BaseListElement listElement : listElements) {
                listElement.restoreState(savedInstanceState);
            }
        }

        // Set the list view adapter
        listView.setAdapter(new ActionListAdapter(getActivity(),
                R.id.selection_list, listElements));
        // ----

        // Check for an open session
        Session session = Session.getActiveSession();
        if(session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
        return view;
    }

    // --------- Starts PickerActivity class ----------
    private void startPickerActivity(Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setData(data);
        intent.setClass(getActivity(), PickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REAUTH_ACTIVITY_CODE) {
            uiHelper.onActivityResult(requestCode, resultCode, data);
        } else if(requestCode == Activity.RESULT_OK &&
                requestCode >= 0 && requestCode < listElements.size()) {
            listElements.get(requestCode).onActivityResult(data);
        }
    }


    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if(session!= null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
    }


    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define
        // a new callback to handle the response
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if(session == Session.getActiveSession()) {
                    if(user != null) {
                        // Set the id for the profilePictureView
                        // that in turn displays the profile picture
                        profilePictureView.setProfileId(user.getId());
                        // Set the TextView's text to the user's name.
                        userNameView.setText(user.getName());
                    }
                }
                if(response.getError() != null) {
                    // Handle errors, will do so later
                }
            }
        });
        request.executeAsync();
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        for(BaseListElement listElement : listElements) {
            listElement.onSaveInstanceState(bundle);
        }
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }


    // --------- Private inner class ActionListAdapter -----------
    private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
        private List<BaseListElement> listELements;

        public ActionListAdapter(Context context, int resourceId,
                                 List<BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listELements = listElements;
            // Set up as an observer for list item changes to
            // refresh the view.
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setBaseAdapter(this);
//                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null);
            }

            BaseListElement listElement = listELements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    text2.setText(listElement.getText2());
                }
            }
            return view;
        }
    }

    // ---------------- Private inner class represents friend picker list item ---------------
    private class PeopleListElement extends BaseListElement {
        private List<GraphUser> selectedUsers;
        private static final String FRIENDS_KEY = "friends";

        public PeopleListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.action_people),
                    getActivity().getResources().getString(R.string.action_people),
                    getActivity().getResources().getString(R.string.action_people_default),
                    requestCode);
        }


        @Override
        protected void onActivityResult(Intent data) {
            selectedUsers = ((EncoreApplication) getActivity()
                    .getApplication())
                    .getSelectedUsers();
            setUsersText();
            notifyDataChanged();
        }


        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (selectedUsers != null) {
                bundle.putByteArray(FRIENDS_KEY,
                        getByteArray(selectedUsers));
            }
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPickerActivity(PickerActivity.FRIEND_PICKER, getRequestCode());
                }
            };
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            byte[] bytes = savedState.getByteArray(FRIENDS_KEY);
            if (bytes != null) {
                selectedUsers = restoreByteArray(bytes);
                setUsersText();
                return true;
            }
            return false;
        }

        private void setUsersText() {
            String text = null;
            if (selectedUsers != null) {
                if (selectedUsers.size() == 1) {
                    text = String.format(getResources().getString(R.string.single_user_selected),
                            selectedUsers.get(0).getName());
                } else if (selectedUsers.size() == 2) {
                    text = String.format(getResources().getString(R.string.two_users_selected),
                            selectedUsers.get(0).getName(), selectedUsers.get(1).getName());
                } else if (selectedUsers.size() > 2) {
                    text = String.format(getResources().getString(R.string.multiple_users_selected),
                            selectedUsers.get(0).getName(), (selectedUsers.size() - 1));
                }
            }
            if (text == null) {
                text = getResources().getString(R.string.action_people_default);
            }
            Log.d("Value of text: ", text);
            setText2(text);
        }

        private byte[] getByteArray(List <GraphUser> users) {
            // convert the list of GraphUsers to a list of String
            // where each element is the JSON representation of the
            // GraphUser so it can be stored in a Bundle
            List<String> usersAsString = new ArrayList<String>(users.size());

            for (GraphUser user : users) {
                usersAsString.add(user.getInnerJSONObject().toString());
            }
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                new ObjectOutputStream(outputStream).writeObject(usersAsString);
                return outputStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, "Unable to serialize users.", e);
            }
            return null;
        }

        private List<GraphUser> restoreByteArray(byte[] bytes) {
            try {
                @SuppressWarnings("unchecked")
                List<String> usersAsString =
                        (List<String>) (new ObjectInputStream
                                (new ByteArrayInputStream(bytes)))
                                .readObject();
                if (usersAsString != null) {
                    List<GraphUser> users = new ArrayList<GraphUser>
                            (usersAsString.size());
                    for (String user : usersAsString) {
                        GraphUser graphUser = GraphObject.Factory
                                .create(new JSONObject(user),
                                        GraphUser.class);
                        users.add(graphUser);
                    }
                    return users;
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (IOException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (JSONException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            }
            return null;
        }
    }
}