package com.encore;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.encore.API.models.Comment;
import com.encore.API.models.Session;

public class InboxViewAdapter extends BaseAdapter {

	private static String tag = "InboxViewAdapter";
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
		Log.d(tag, "populating position: " + position);
		// this shouldn't be an applicationInfo object
		Session entry = (Session) mSessionList.get(position);

		// reference to convertView
		SessionView v = (SessionView) convertView;

		// inflate new layout if null
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = (SessionView) inflater.inflate(R.layout.inbox_view, parent,
					false);
			Button reply = (Button) v.findViewById(R.id.reply);
			reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					// ((Activity)mContext).startActivityForResult(new
					// Intent(mContext, AndroidVideoCapture.class), 0);
					SessionView sv = (SessionView) arg0;
					Session session = sv.getSession();

					Intent intent = new Intent(mContext, StartSession2.class);
					intent.putExtra("reply", true);
					intent.putExtra("sessionid", sv.getId());

					mContext.startActivity(intent);
				}

			});
			v.setSession(entry);
		}

		// load controls from layout resources
		TextView titleTextView = (TextView) v.findViewById(R.id.tvName);
		TextView likesTextView = (TextView) v.findViewById(R.id.likes);
		TextView commentsTextView = (TextView) v.findViewById(R.id.comments);

		// set data to display
		titleTextView.setText(entry.getTitle());
		List<Comment> comments = entry.getComments();
		commentsTextView.setText(comments.size() + " comments");
		for (int i = 0; i < comments.size(); ++i){
			TextView oneComment = new TextView(mContext);
			Log.d(tag, comments.get(i).toString());
			oneComment.setText(comments.get(i).getText());
			v.addView(new TextView(mContext));
			
		}
		// return view
		return v;
	}

}
