package com.encore;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SessionViewAdapter extends BaseAdapter{
	
	private Context mContext;
    private List mSessionList;
    private PackageManager mPackManager;
    
	public SessionViewAdapter(Context c, List list, PackageManager pm) {
		mContext = c;
		mSessionList = list;
		mPackManager = pm;
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
        ApplicationInfo entry = (ApplicationInfo) mSessionList.get(position);
 
        // reference to convertView
        SessionView v = (SessionView) convertView;
 
        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = (SessionView) inflater.inflate(R.layout.session_view, null);
        }
 
        // load controls from layout resources
        ImageView ivAppIcon = (ImageView)v.findViewById(R.id.ivIcon);
        TextView tvAppName = (TextView)v.findViewById(R.id.tvName);
 
        // set data to display
        ivAppIcon.setImageDrawable(entry.loadIcon(mPackManager));
        tvAppName.setText(entry.loadLabel(mPackManager));
 
        // return view
        return v;
	}

}