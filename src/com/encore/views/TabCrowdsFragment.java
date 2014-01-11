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
import com.encore.TabCrowdAdapter;
import com.encore.models.Crowd;
import com.encore.models.Crowds;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabCrowdsFragment extends Fragment {
    private static final String TAG = "TabCrowdsFragment";
    private ListView myCrowdsLv;
    private TabCrowdAdapter adapter;
    private CrowdReceiver receiver;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_crowds, null, false);

        getViews();
        setupAdapter();
        getCrowds();

        return view;
    }

    public void getViews() {
        myCrowdsLv = (ListView) view.findViewById(R.id.tab_crowds_lv);
    }

    public void setupAdapter() {
        adapter = new TabCrowdAdapter(getActivity(), R.layout.tab_crowd_list_row, null);
        myCrowdsLv.setAdapter(adapter);
    }

    public void getCrowds() {
        Log.d(TAG, "Making request to get crowds");
        Intent api = new Intent(getActivity(), APIService.class);
        receiver = new CrowdReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_CROWDS);
        api.putExtra(T.RECEIVER, receiver);
        getActivity().startService(api);
    }

    public class CrowdReceiver extends ResultReceiver {
        public CrowdReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == T.GET_CROWDS) {

                Log.d(TAG, "APIService returned successful");
                Gson mGson = new Gson();

                // Convert json results to ArrayList<Crowd>
                String jsonResult = resultData.getString("crowdsJson");
                Crowd[] temp = mGson.fromJson(jsonResult, Crowds.class).getCrowds();
                ArrayList<Crowd> crowds = new ArrayList<Crowd>(Arrays.asList(temp));

                // Update adapter
//                adapter = new TabCrowdAdapter(getActivity(), 0, crowds);
//                myCrowdsLv.setAdapter(adapter);
                adapter.setItemList(crowds);
                adapter.notifyDataSetChanged();
            }
            else {
                Log.d(TAG, "getCrowds() failed");
            }
        }
    }
}