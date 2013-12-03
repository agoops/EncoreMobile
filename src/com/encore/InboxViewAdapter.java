package com.encore;

import java.util.ArrayList;
import java.util.List;

import util.T;
import widget.CommentsAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.API.models.Comment;
import com.encore.API.models.Session;

public class InboxViewAdapter extends BaseAdapter implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
	private List<Session> mSessionList;
	private static LayoutInflater inflater = null;
	private SessionView rowView;
	private EditText commentField;
	
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
		if(rowView == null) {
			rowView = (SessionView) inflater.inflate(R.layout.inbox_view, parent, false);
			viewHolder = new SessionHolder();
			
			viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.tvName);
			viewHolder.reply = (Button) rowView.findViewById(R.id.reply);
			viewHolder.likesBtn = (Button) rowView.findViewById(R.id.likes);
			viewHolder.favoritesBtn = (Button) rowView.findViewById(R.id.favorites);
			viewHolder.commentsBtn = (Button) rowView.findViewById(R.id.comments);
			
			// get the selected entry
			Log.d(TAG, "populating position: " + position);
			Session entry = (Session) mSessionList.get(position);
			
			// For each listview, store the session
			viewHolder.reply.setTag(entry);
			viewHolder.likesBtn.setTag(entry);
			viewHolder.favoritesBtn.setTag(entry);
			viewHolder.commentsBtn.setTag(entry);
			viewHolder.reply.setOnClickListener((OnClickListener) this);
			viewHolder.likesBtn.setOnClickListener((OnClickListener) this);
			viewHolder.favoritesBtn.setOnClickListener((OnClickListener) this);
			viewHolder.commentsBtn.setOnClickListener((OnClickListener) this);
			
			rowView.setSession(entry);
			
			// set session title
			viewHolder.titleTextView.setText(entry.getTitle());
			
			// set comment string and views for comments, and click listener
			// Get a list of comments from the session
//			List<Comment> comments = entry.getComments();
//			viewHolder.commentsBtn.setText(comments.size() + " comments");
			
			rowView.setTag(viewHolder);
		} else {
			// Avoids calling v.findViewById on resource every time
			viewHolder = ((SessionHolder) rowView.getTag());
		}
		
        // return view
        return rowView;
	}
	
	@Override
	public void onClick(View v) {
		final Session sesh = (Session) v.getTag();
		switch(v.getId())
		{
		case R.id.reply:
			// Pass crowdId and sessionTitle on to StartSession2
			// who in turn passes it on to RecordFragment
			int crowdId = sesh.getCrowd().getId();
			Intent launchRecordFragment = new Intent(mContext, StartSession2.class);
			launchRecordFragment.putExtra("crowdId", crowdId);
			launchRecordFragment.putExtra("sessionTitle", sesh.getTitle());
			((Activity) mContext).startActivity(launchRecordFragment);
			break;
		case R.id.comments:
			// Open an AlertDialog that holds a listview of current comments, as well as the ability to create your own comments
			// TODO: Either spiffy up the dialog or make a new activity. The activity is probably easier to do.
			
			// Instantiate an AlertDialog.Builder with its constructor
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			
			// Create the dialog's layout
			LinearLayout layout = new LinearLayout(mContext);
			ListView commentsLV = new ListView(mContext);
			final EditText commentField = new EditText(mContext);
			// Get a list of comments
			List<Comment> comments = sesh.getComments();
			// Create a CommentsAdapter instance and setAdapter
			CommentsAdapter adapter = new CommentsAdapter(mContext, R.layout.comment_list_row, comments);
			commentsLV.setAdapter(adapter);
			
			// Add the views to the layout
			layout.addView(commentField);
			layout.addView(commentsLV);
			builder.setView(layout);
			
			// Set the dialog characteristics
			builder.setTitle("Create a comment")
				.setPositiveButton("Post Comment", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Post
						Toast.makeText(mContext, "Posting: " + commentField.getText().toString(), Toast.LENGTH_SHORT).show();
						Intent api = new Intent(mContext, APIService.class);
						api.putExtra(T.API_TYPE, T.CREATE_COMMENT);
						api.putExtra(T.SESSION_ID, sesh.getId());
						api.putExtra(T.COMMENT_TEXT, commentField.getText().toString());
						mContext.startService(api);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Cancel
					}
				});
			
			
			AlertDialog dialog = builder.create();
			dialog.show();
			
			SessionView sv = (SessionView) v.getParent().getParent();
			sv.toggleCommentsVisible();
			break;
		case R.id.likes:
			// TODO: Toggle between "Like" and "Unlike". Currently it only increments
			Intent likesApi = new Intent(mContext, APIService.class);
			likesApi.putExtra(T.API_TYPE, T.CREATE_LIKE);
			likesApi.putExtra(T.SESSION_ID, sesh.getId());
			mContext.startService(likesApi);
			break;
		case R.id.favorites:
			Intent favApi = new Intent(mContext, APIService.class);
			favApi.putExtra(T.API_TYPE, T.CREATE_FAVORITE);
			favApi.putExtra(T.SESSION_ID, sesh.getId());
			mContext.startService(favApi);
		default:
			break;
		}
	}
	
	static class SessionHolder {
		TextView titleTextView;
		Button reply, likesBtn, commentsBtn, favoritesBtn;
	}
}
