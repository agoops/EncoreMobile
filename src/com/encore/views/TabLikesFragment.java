package com.encore.views;

import android.app.Activity;
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
import com.encore.TabLikesAdapter;
import com.encore.models.LikeArray;
import com.encore.models.Session;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabLikesFragment extends Fragment {
    private static final String TAG = "TabLikesFragment";
    private ListView myLikesLv;
    private TabLikesAdapter adapter;
    private LikesReceiver receiver;
    private View view;
    private ArrayList<Session> likedSessions = null;
    private Activity activity;

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_likes, null);

        getViews();
        setupAdapter();
        getLikes();

        return view;
    }

    public void getViews() {
        myLikesLv = (ListView) view.findViewById(R.id.tab_likes_lv);
    }

    public void setupAdapter() {
        adapter = new TabLikesAdapter(getParentFragment().getActivity(), R.layout.tab_likes_list_row, null);
        myLikesLv.setAdapter(adapter);
    }

    public void getLikes() {
        Log.d(TAG, "Making request to get likes");
        Intent api = new Intent(getParentFragment().getActivity(), APIService.class);
        receiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, receiver);
        getParentFragment().getActivity().startService(api);
    }

    private class LikesReceiver extends ResultReceiver {
        public LikesReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 1) {
                Log.d(TAG, "APIService returned successful with likes");
                String result = resultData.getString("result");

                Log.d(TAG, "result from apiservice is: " + result);
                likedSessions = new Gson().fromJson(result, LikeArray.class)
                        .getLikedSessionsList();
                adapter.setItemList(likedSessions);
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "APIService get session clip url failed?");
            }
        }
    }
}
