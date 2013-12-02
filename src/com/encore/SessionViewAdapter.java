package com.encore;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.encore.API.models.Session;

public class SessionViewAdapter extends BaseAdapter{
	
	private Context mContext;
    private List<Session> mSessionList;
    
	public SessionViewAdapter(Context c, ArrayList<Session> arrayList) {
		mContext = c;
		mSessionList = arrayList;
	}
	
	@Override
	public int getCount() {
		return mSessionList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mSessionList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get the selected entry
		
		//this shouldn't be an applicationInfo object
        Session entry = (Session) mSessionList.get(position);
 
        // reference to convertView
        SessionView v = (SessionView) convertView;
 
        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = (SessionView) inflater.inflate(R.layout.session_view, parent, false);
            v.setSession(entry);
        }
 
        // load controls from layout resources
        ImageView ivAppIcon = (ImageView) v.findViewById(R.id.ivIcon);
        TextView tvAppName = (TextView) v.findViewById(R.id.tvName);
 
        // set data to display
//        ivAppIcon.setImageDrawable(entry.loadIcon());
        tvAppName.setText(entry.getTitle());
 
        // return view
        return v;
	}

	public void setItemList(ArrayList<Session> list) {
		this.mSessionList = list;
	}

}
