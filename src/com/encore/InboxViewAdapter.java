package com.encore;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.encore.API.models.Session;

public class InboxViewAdapter extends BaseAdapter implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
    private List<Session> mSessionList;
    
	public InboxViewAdapter(Context c, ArrayList<Session> list) {
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
	
	public void setItemList(ArrayList<Session> list) {
		this.mSessionList = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get the selected entry
		Log.d(TAG, "populating position: " + position);
		//this shouldn't be an applicationInfo object
        Session entry = (Session) mSessionList.get(position);
 
        // reference to convertView
        SessionView v = (SessionView) convertView;
 
        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = (SessionView) inflater.inflate(R.layout.inbox_view, parent, false);
            Button reply = (Button) v.findViewById(R.id.reply);
            reply.setTag(entry);
            reply.setOnClickListener((OnClickListener) this);
            v.setSession(entry);
        }
 
        // load controls from layout resources
        ImageView ivAppIcon = (ImageView)v.findViewById(R.id.ivIcon);
        TextView tvAppName = (TextView)v.findViewById(R.id.tvName);
 
        // set data to display
//        ivAppIcon.setImageDrawable(entry.loadIcon());
        tvAppName.setText(entry.getTitle());
 
        // return view
        return v;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.reply:
			// Pass crowdId and sessionTitle on to StartSession2
			// who in turn passes it on to RecordFragment
			Session sesh = (Session) v.getTag();
			
			int crowdId = sesh.getCrowd().getId();
			Intent launchRecordFragment = new Intent(mContext, StartSession2.class);
			launchRecordFragment.putExtra("crowdId", crowdId);
			launchRecordFragment.putExtra("sessionTitle", sesh.getTitle());
			((Activity) mContext).startActivity(launchRecordFragment);
			
			break;
		default:
			break;
		}
	}
	

}
