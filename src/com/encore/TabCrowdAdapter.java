package com.encore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.encore.models.Crowd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabCrowdAdapter extends ArrayAdapter<Crowd> {
    private static final String TAG = "CrowdAdapter";
    private TextView crowdTitle, numMembers, membersTv;
    private Context context;
    private int layoutId;
    private List<Crowd> crowds;
    private static LayoutInflater inflater = null;

    public TabCrowdAdapter(Context context, int layoutId, List<Crowd> crowds) {
        super(context, layoutId, crowds);
        this.context = context;
        this.layoutId = layoutId;
        this.crowds = crowds;
    }

    // Returns the appropriate view to our custom listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get the crowd_list_row
        Log.d(TAG, "populating convertView for the first time");
        convertView = inflater.inflate(layoutId, null);

        crowdTitle = (TextView) convertView.findViewById(R.id.tab_crowds_title);
        numMembers = (TextView) convertView.findViewById(R.id.tab_crowds_num_members);
//        membersTv = (TextView) convertView.findViewById(R.id.tab_crowds_members);


        // And update its title (which will happen for all crowds)
        Crowd crowd = crowds.get(position);
        crowdTitle.setText(
                (crowd.getTitle() == null) ? "" : crowd.getTitle());
        numMembers.setText(Integer.toString(crowd.getNumMembers()) + " members");

//        String[] members = crowd.getMembersFirstNames();
//        StringBuilder sb = new StringBuilder();
//        for(int i=0; i < members.length; i++) {
//            sb.append(members[i]);
//            if(i < members.length-1) {
//                sb.append(", ");
//            }
//        }
//        membersTv.setText(sb.toString());

        return convertView;
    }

    @Override
    public int getCount() {
        if(crowds != null) {
            return crowds.size();
        }
        return 0;
    }

    public void setItemList(ArrayList<Crowd> crowds) {
        this.crowds = crowds;
    }
}
