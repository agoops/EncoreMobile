package com.encore;

import java.io.IOException;
import java.util.List;

import util.T;
import widget.CommentDialog;
import widget.CommentsAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.API.models.Comment;
import com.encore.API.models.Session;
import com.google.gson.Gson;

public class InboxViewAdapter extends ArrayAdapter<Session> implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
	private List<Session> mSessionList;
	private static LayoutInflater inflater = null;
	private SessionView rowView;
	private EditText commentField;
	
	public InboxViewAdapter(Context c, int textViewResourceId, List<Session> sessions) {
		super(c, textViewResourceId, sessions);
		mContext = c;
		mSessionList = sessions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = (SessionView) convertView;
		final SessionHolder viewHolder;
		
		// Get the inbox_view
//		if(rowView == null) {
			rowView = (SessionView) inflater.inflate(R.layout.inbox_view, parent, false);
			viewHolder = new SessionHolder();
			
			viewHolder.titleTextView = (TextView) rowView.findViewById(R.id.tvName);
			viewHolder.play = (Button) rowView.findViewById(R.id.stream_clip);
			viewHolder.reply = (Button) rowView.findViewById(R.id.reply);
			viewHolder.likesBtn = (Button) rowView.findViewById(R.id.likes);
			viewHolder.favoritesBtn = (Button) rowView.findViewById(R.id.favorites);
			viewHolder.commentsBtn = (Button) rowView.findViewById(R.id.comments);
			
			// get the selected entry
			Session entry = (Session) mSessionList.get(position);
			
			// For each listview, store the session
			viewHolder.reply.setTag(entry);
			viewHolder.play.setTag(entry);
			viewHolder.likesBtn.setTag(entry);
			viewHolder.favoritesBtn.setTag(entry);
			viewHolder.commentsBtn.setTag(entry);
			
			viewHolder.reply.setOnClickListener((OnClickListener) this);
			viewHolder.likesBtn.setOnClickListener((OnClickListener) this);
			viewHolder.favoritesBtn.setOnClickListener((OnClickListener) this);
			viewHolder.commentsBtn.setOnClickListener((OnClickListener) this);
			viewHolder.play.setOnClickListener((OnClickListener) this);
			
			rowView.setSession(entry);
			
			// set session title
			viewHolder.titleTextView.setText(entry.getTitle());
			
			// Get a list of comments from the session
			List<Comment> comments = entry.getComments();
			viewHolder.commentsBtn.setText(comments.size() + " comments");
			
			rowView.setTag(viewHolder);
//		} 
//		else {
//			// Avoids calling v.findViewById on resource every time
//			viewHolder = ((SessionHolder) rowView.getTag());
//		}
		
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
			int crowdId = sesh.getCrowdId();
			Intent launchRecordFragment = new Intent(mContext, StartSession2.class);
			launchRecordFragment.putExtra("crowdId", crowdId);
			launchRecordFragment.putExtra("sessionTitle", sesh.getTitle());
			launchRecordFragment.putExtra("sessionId", sesh.getId());
			((Activity) mContext).startActivity(launchRecordFragment);
			
			break;
		case R.id.comments:
			// Open an AlertDialog that shows a session's comments,
			// and allows a user to post a new comment
			
			CommentDialog cDialog = new CommentDialog(mContext);
			Bundle dArgs = new Bundle();
			String json = (new Gson()).toJson(sesh.getComments());
			dArgs.putInt("sessionId", sesh.getId());
			dArgs.putString("comments", json);
			cDialog.setDialogArgs(dArgs);
			cDialog.show();
			
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
		case R.id.stream_clip:
			// TODO: Make GET for url
//			Intent clipApi = new Intent(mContext, APIService.class);
//			clipApi.putExtra(T.API_TYPE, T.GET_CLIP_STREAM);
//			clipApi.putExtra(T.SESSION_ID, sesh.getId());
//			ResultReceiver receiver = new ClipStreamReceiver(new Handler());
//			clipApi.putExtra(T.RECEIVER, receiver);
//			mContext.startService(clipApi);

			Log.d(TAG, "APIService returned successful with clip stream");
            Log.d(TAG, "Attempting to play clip");
            MediaPlayer mp = new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mp.setDataSource("http://172.27.122.251:3000/sessions/clip/"+sesh.getId());
				mp.prepare();
				mp.start();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//            Thread thread = new Thread();
//            Log.d(TAG, "Button licked");
            
//            int sessionId = sesh.getId();
//            Log.d(TAG, "sessionId:" + sessionId);
//            
//            StreamTask stream= new StreamTask();
//            stream.execute(new Void[3]);
            break;
		default:
			break;
		}
	}
	
	@Override
	public int getCount() {
		if(mSessionList != null) {
			return mSessionList.size();
		}
		
		return 0;
	}
	
	static class SessionHolder {
		TextView titleTextView;
		Button play, reply, likesBtn, commentsBtn, favoritesBtn;
	}
	
	private class ClipStreamReceiver extends ResultReceiver {
        public ClipStreamReceiver(Handler handler) {
                super(handler);
                // TODO Auto-generated constructor stub
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == T.GET_CLIP_STREAM) {
                        Log.d(TAG, "APIService returned successful with clip stream");
                        Log.d(TAG, "Attempting to play clip");
                        MediaPlayer mp = new MediaPlayer();
            			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            			try {
							mp.setDataSource("http://172.27.122.251:3000/sessions/clip/");
							mp.prepare();
							mp.start();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    
                        String result = resultData.getString("result");
                        Log.d(TAG, "result is: " + result);
                        
                } else {
                        Log.d(TAG, "APIService get session failed?");
                }
        }
	}
	
	public void setItemList(List<Session> sessions) {
		this.mSessionList = sessions;
	}
}
