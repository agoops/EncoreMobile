package com.encore.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.TabRequestsAdapter;
import com.encore.models.FriendRequest;
import com.encore.models.FriendRequestProfile;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabRequestsFragment extends Fragment {
    private static final String TAG = "TabRequestsFragment";
    private ListView myRequestsLv;
    private TabRequestsAdapter adapter;
    private RequestsReceiver receiver;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_requests, null, false);
        Log.d(TAG, "onCreateView");

        getViews();
        setupAdapter();
        getRequests();

        return view;
    }

    public void getViews() {
        myRequestsLv = (ListView) view.findViewById(R.id.tab_requests_lv);
    }

    public void setupAdapter() {
        adapter = new TabRequestsAdapter(getActivity(), R.layout.tab_requests_list_row, null);
        myRequestsLv.setAdapter(adapter);
    }

    public void getRequests() {
        Log.d(TAG, "Making request to get friend requests");
        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new RequestsReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.FRIEND_REQUESTS_PENDING);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    public class RequestsReceiver extends ResultReceiver {
        public RequestsReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle data) {
            if(resultCode == 1) {
                // Populate views with our data
                Log.d(TAG, "Friends received successfully");
                String result = data.getString("result");

                FriendRequest requests = (new Gson()).fromJson(result, FriendRequest.class);
                FriendRequestProfile[] pendingMe  = requests.getPendingMe();

//                adapter = new TabRequestsAdapter(getActivity(), 0, Arrays.asList(pendingMe));
//                myRequestsLv.setAdapter(adapter);
                adapter.setItemList(new ArrayList<FriendRequestProfile>(Arrays.asList(pendingMe)));
                adapter.notifyDataSetChanged();

                // Hide progress bar
//				progressBar.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "GET friends unsuccessful");
            }
        }
    }
}
