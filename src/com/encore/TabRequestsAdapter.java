package com.encore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.encore.models.FriendRequestProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabRequestsAdapter extends ArrayAdapter<FriendRequestProfile> {
    private static final String TAG = "CrowdAdapter";
    private Context context;
    private int layoutId;
    private List<FriendRequestProfile> friendRequestProfiles;
    private static LayoutInflater inflater = null;
//    private TextView username, fullName;
//    private Button accept, decline;

    public TabRequestsAdapter(Context context, int layoutId, List<FriendRequestProfile> friendRequestProfiles) {
        super(context, layoutId, friendRequestProfiles);
        this.context = context;
        this.layoutId = layoutId;
        this.friendRequestProfiles = friendRequestProfiles;
    }

    // Returns the appropriate view to our custom listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.tab_requests_username);
            holder.fullName= (TextView) convertView.findViewById(R.id.tab_requests_fullname);
            holder.accept = (Button) convertView.findViewById(R.id.tab_requests_accept);
            holder.decline = (Button) convertView.findViewById(R.id.tab_requests_decline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        FriendRequestProfile request = friendRequestProfiles.get(position);
        holder.username.setText(request.getSender().getUsername());
        holder.fullName.setText(request.getSender().getFullName());
        // TODO: set button click listeners
        return convertView;
    }

    @Override
    public int getCount() {
        if(friendRequestProfiles != null) {
            return friendRequestProfiles.size();
        }
        return 0;
    }

    public void setItemList(ArrayList<FriendRequestProfile> requests) {
        this.friendRequestProfiles = requests;
    }

    static class ViewHolder {
        private TextView username, fullName;
        private Button accept, decline;
    }
}
