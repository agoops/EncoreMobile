package com.encore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import util.T;
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
import android.widget.EditText;
import android.widget.TextView;

import com.encore.API.APIService;
import com.encore.API.models.Comment;
import com.encore.API.models.Session;

public class InboxViewAdapter extends BaseAdapter implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
	private List<Session> mSessionList;
	private static LayoutInflater inflater = null;
	private SessionView rowView;

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
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = (SessionView) convertView;
		final SessionHolder viewHolder;
		
		// Get the inbox_view
		if(convertView == null) {
			rowView = (SessionView) inflater.inflate(R.layout.inbox_view, parent, false);
			viewHolder = new SessionHolder();
			
			viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.tvName);
			viewHolder.likesTextView = (TextView) rowView.findViewById(R.id.likes);
			viewHolder.commentsTextView = (TextView) rowView.findViewById(R.id.comments);
			viewHolder.reply = (Button) rowView.findViewById(R.id.reply);
			
			rowView.setTag(viewHolder);
			
			// get the selected entry
			Log.d(TAG, "populating position: " + position);
			Session entry = (Session) mSessionList.get(position);
			
			// For each listview, store the session
			viewHolder.reply.setTag(entry);
			viewHolder.reply.setOnClickListener((OnClickListener) this);
			rowView.setSession(entry);
			
			// set session title
			viewHolder.titleTextView.setText(entry.getTitle());
			
			// set comment string and views for comments, and click listener
			// Get a list of comments from the session
			List<Comment> comments = entry.getComments();
			viewHolder.commentsTextView.setText(comments.size() + " comments");
			for (int i = 0; i < comments.size(); ++i) {
				// And construct a TextView to represent the Comment
				TextView oneComment = new TextView(mContext);
				Log.d(TAG, comments.get(i).toString());
				oneComment.setText(comments.get(i).getText());
				oneComment.setVisibility(TextView.GONE);
				rowView.addCommentView(oneComment);
				// Add the comment as a child to rowView
				rowView.addView(oneComment);
			}
			
			// Add Comment button
			Button addComment = new Button(mContext);
			addComment.setText("Add Comment");
			addComment.setVisibility(Button.GONE);
			addComment.setId(999); // Randomly chosen id.
			addComment.setTag(entry);
			addComment.setOnClickListener((OnClickListener) this);
			
			// Comment Field ET
			EditText commentField = new EditText(mContext);
			commentField.setHint("What's on your mind?");
			commentField.setVisibility(EditText.GONE);
			commentField.setId(1234); // Randomly chosen id.
			
			rowView.addCommentView(commentField);
			rowView.addView(commentField);
			rowView.addCommentView(addComment);
			rowView.addView(addComment);
			
			viewHolder.commentsTextView.setOnClickListener((OnClickListener) this);
		} else {
			// Avoids calling v.findViewById on resource every time
			viewHolder = ((SessionHolder) rowView.getTag());
		}
		
        // return view
        return rowView;
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
		case R.id.comments:
			SessionView sv = (SessionView) v.getParent().getParent();
			sv.toggleCommentsVisible();
			break;
		case 999:
			Log.d(TAG, "Add comment button clicked.");
			
			Session currentSession = (Session) v.getTag();
			// TODO: commentText is always empty argghhh 
			String commentText = ((EditText) rowView.findViewById(1234)).getText().toString();
			Log.d(TAG, "EditText is: " + ((EditText)rowView.findViewById(1234)).toString());
			
			Intent api = new Intent(mContext, APIService.class);
			api.putExtra(T.API_TYPE, T.CREATE_COMMENT);
			api.putExtra(T.SESSION_ID, currentSession.getId());
			api.putExtra(T.COMMENT_TEXT, commentText);
			mContext.startService(api);
			break;
		default:
			break;
		}
	}
	
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
	/**
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 * @return a generated ID value
	 */
	public static int generateViewId() {
	    for (;;) {
	        final int result = sNextGeneratedId.get();
	        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	        int newValue = result + 1;
	        if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	        if (sNextGeneratedId.compareAndSet(result, newValue)) {
	            return result;
	        }
	    }
	}
	
	static class SessionHolder {
		TextView titleTextView, likesTextView, commentsTextView;
		Button reply, addComment;
	}
}
