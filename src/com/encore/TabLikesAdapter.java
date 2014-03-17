package com.encore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.models.Likes;
import com.encore.models.Session;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabLikesAdapter extends ArrayAdapter<Session> {
    private static final String TAG = "TabLikesAdapter";

    private Context context;
    private int layoutId;
    private List<Session> likedSessions;
    private LayoutInflater inflater;

    public TabLikesAdapter(Context context, int layoutId, List<Session> likedSessions) {
        super(context, layoutId, likedSessions);
        Log.d(TAG, "Creating new TabLikesAdapter");

        this.context = context;
        this.layoutId = layoutId;
        this.likedSessions = likedSessions;

//        getLikes();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tab_likes_list_row, parent, false);

            holder = new ViewHolder();
            holder.sessionTitle = (TextView) convertView.findViewById(R.id.tab_likes_title);
            holder.numLikes = (TextView) convertView.findViewById(R.id.tab_likes_num_likes);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // And set the sessionTitle and numLikes
        Session session = likedSessions.get(position);
        holder.sessionTitle.setText(session.getTitle());
        holder.numLikes.setText(session.getLikes() + " likes");

        return convertView;
    }

    @Override
    public int getCount() {
        if(likedSessions != null) {
            return likedSessions.size();
        }

        return 0;
    }

    public void getLikes() {
        Log.d(TAG, "Making request to get likes");
        Intent api = new Intent(context, APIService.class);
        LikesReceiver receiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, receiver);
        context.startService(api);
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
                likedSessions = new Gson().fromJson(result, Likes.class)
                        .getLikedSessionsList();
            } else {
                Log.d(TAG, "APIService get session clip url failed?");
            }
        }
    }

    public void setItemList(ArrayList<Session> sessions) {
        this.likedSessions = sessions;
    }

    static class ViewHolder {
        private TextView sessionTitle, numLikes;
    }
}
