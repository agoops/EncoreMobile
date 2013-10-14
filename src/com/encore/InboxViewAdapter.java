package com.encore;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class InboxViewAdapter extends BaseAdapter{
	
	private Context mContext;
    private List<SessionTemp> mSessionList;
    
	public InboxViewAdapter(Context c, List<SessionTemp> list) {
		mContext = c;
		mSessionList = list;
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
        SessionTemp entry = (SessionTemp) mSessionList.get(position);
 
        // reference to convertView
        SessionView v = (SessionView) convertView;
 
        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = (SessionView) inflater.inflate(R.layout.inbox_view, parent, false);
            Button reply = (Button) v.findViewById(R.id.reply);
            reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					((Activity)mContext).startActivityForResult(new Intent(mContext, AndroidVideoCapture.class), 0);

				}
            	
            
            });
            v.setData(entry);
        }
 
        // load controls from layout resources
        ImageView ivAppIcon = (ImageView)v.findViewById(R.id.ivIcon);
        TextView tvAppName = (TextView)v.findViewById(R.id.tvName);
 
        // set data to display
        ivAppIcon.setImageDrawable(entry.loadIcon());
        tvAppName.setText(entry.loadLabel());
 
        // return view
        return v;
	}
	

}
