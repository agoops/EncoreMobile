package com.encore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.VideoView;

import com.encore.API.APIService;
import com.encore.models.Comment;
import com.encore.models.Session;
import com.encore.util.T;
import com.encore.widget.CommentDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.util.List;

public class InboxViewAdapter extends ArrayAdapter<Session> implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
	private List<Session> mSessionList;
	private static LayoutInflater inflater = null;
	private SessionView rowView;
    private boolean liked;
    private TextView titleTextView, crowdMembersTextView;
    private Button reply, likesBtn, commentsBtn;
    private com.encore.widget.AspectRatioImageView thumbnailIv;
    private Drawable statusLikedIcon, statusUnlikedIcon;

	public InboxViewAdapter(Context c, int textViewResourceId, List<Session> sessions) {
		super(c, textViewResourceId, sessions);
		mContext = c;
		mSessionList = sessions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.inbox_view, parent, false);

        // Get the row views
        titleTextView = (TextView) convertView.findViewById(R.id.tvName);
        reply = (Button) convertView.findViewById(R.id.reply);
        likesBtn = (Button) convertView.findViewById(R.id.likes);
        commentsBtn = (Button) convertView.findViewById(R.id.comments);
        thumbnailIv = (com.encore.widget.AspectRatioImageView) convertView.findViewById(R.id.inboxImageView);
        crowdMembersTextView = (TextView) convertView.findViewById(R.id.crowd_members_tv);

        // Get the like icons so we can use them elsewhere
        statusUnlikedIcon = getContext().getResources().getDrawable(R.drawable.like_black_border);
        statusLikedIcon = getContext().getResources().getDrawable(R.drawable.like_red_invert);

        // Set the position for each view
        reply.setTag(R.string.first_key, position);
        likesBtn.setTag(R.string.first_key, position);
        commentsBtn.setTag(R.string.first_key, position);
        thumbnailIv.setTag(R.string.first_key, position);

        // Assign click listeners
        reply.setOnClickListener(this);
        likesBtn.setOnClickListener(this);
        commentsBtn.setOnClickListener(this);
        thumbnailIv.setOnClickListener(this);

        // TODO: Make this dynamic!
        liked = false;

        // Get the most recently loaded session
        Session entry = mSessionList.get(position);

        // Set session title
        titleTextView.setText(entry.getTitle());

        // Get the crowd members' names
        String names = entry.getMembersFirstNames();
        crowdMembersTextView.setText(names);

        // Get the session's comments
        List<Comment> comments = entry.getComments();

        // Set num comments and num likes
        if(comments.size() == 1) {
            commentsBtn.setText(comments.size() + " comment");
        } else {
            commentsBtn.setText(comments.size() + " comments");
        }

        int numLikes = entry.getLikes();
        if(numLikes == 1) {
            likesBtn.setText(numLikes + " like");
        } else {
            likesBtn.setText(numLikes + " likes");
        }

        // Set the thumbnail
        if(entry.getThumbnailUrl() != null) {
            new DownloadImageTask((com.encore.widget.AspectRatioImageView) convertView.findViewById(R.id.inboxImageView))
                    .execute(entry.getThumbnailUrl());
        }

        return convertView;
	}

	@Override
	public void onClick(View v) {
//        final Session sesh = (Session) v.getTag();
        int position = (Integer) v.getTag(R.string.first_key);
        Session sesh = mSessionList.get(position);

        switch(v.getId())
		{
        case R.id.reply:
            // Pass crowdId and sessionTitle on to StartSession2
            // who in turn passes it on to RecordFragment
            int crowdId = sesh.getCrowdId();
            Intent launchRecordFragment = new Intent(mContext, CameraActivity2.class);
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
            // TODO: Problem -> likesBtn refers to different row
            // Toggle like icon and set updated like count
//            int numLikes = sesh.getLikes();
//            Log.d(TAG, "session title: " + sesh.getTitle() + "\nlikesBtn: " + likesBtn.getId());
//            if(liked) {
//                likesBtn.setCompoundDrawablesWithIntrinsicBounds(statusUnlikedIcon, null, null, null);
//                if(numLikes == 1) {
//                    likesBtn.setText("1 like");
//                } else {
//                    likesBtn.setText(numLikes + " likes");
//                }
//            } else {
//                likesBtn.setCompoundDrawablesWithIntrinsicBounds(statusLikedIcon, null, null, null);
//                if(numLikes+1 == 1) {
//                    likesBtn.setText("1 like");
//                } else {
//                    likesBtn.setText(numLikes+1 + " likes");
//                }
//            }
//            liked = !liked;

			Intent likesApi = new Intent(mContext, APIService.class);
			likesApi.putExtra(T.API_TYPE, T.CREATE_LIKE);
			likesApi.putExtra(T.SESSION_ID, sesh.getId());
			mContext.startService(likesApi);
			break;
		case R.id.inboxImageView:
			// Clicking the thumbnail will play the video
			Intent clipApi = new Intent(mContext, APIService.class);
			clipApi.putExtra(T.API_TYPE, T.GET_CLIP_STREAM);
			int sessionId = sesh.getId();
			clipApi.putExtra(T.SESSION_ID, sessionId);
			ResultReceiver receiver = new ClipStreamReceiver(new Handler());
			clipApi.putExtra(T.RECEIVER, receiver);
			mContext.startService(clipApi);
			
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
	
	private class ClipStreamReceiver extends ResultReceiver {
        public ClipStreamReceiver(Handler handler) {
                super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 1) {
                        Log.d(TAG, "APIService returned successful with clip stream");
                        Log.d(TAG, "Attempting to play clip");
                        String result = resultData.getString("result");
                        Log.d(TAG, "result from apiservice is: " + result);
                        JsonParser jsonParser = new JsonParser();
                		JsonArray jsonClipsArray = jsonParser.parse(result).getAsJsonArray();
                		
                		int numClipsInSession = jsonClipsArray.size();
                		
                		//Get last clipElement, get clip url
                		String url = jsonClipsArray.get(0).getAsJsonObject().get("url").getAsString();
                		Log.d(TAG, "url is: " + url);
                		Uri uri = Uri.parse(url);
                		
                		//this link works. it's asian people playing ping pong CLASSIC
                		Uri hardcodeUri = Uri.parse("http://students.mimuw.edu.pl/~nh209484/Video000.3gp");
                		
                		//Problems: can stream back a video from android, but not from what michael made. 
                		//I know android records in mpeg4.
                		showVideoDialog(uri);
    
                        
                } else {
                        Log.d(TAG, "APIService get session clip url failed?");
                }
        }
	}
	
	// Asynchronously downloads the thumbnail and displays it
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		com.encore.widget.AspectRatioImageView thumbnailIv;

	    public DownloadImageTask(com.encore.widget.AspectRatioImageView bmImage) {
	        this.thumbnailIv = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap thumbnail = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            thumbnail = BitmapFactory.decodeStream(in);
	            thumbnailIv.setScaleType(ScaleType.FIT_XY);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return thumbnail;
	    }

	    protected void onPostExecute(Bitmap result) {
	        thumbnailIv.setImageBitmap(result);
	    }
	}
	
	private void showVideoDialog(Uri uri) {

		final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.video_dialog);
        dialog.setCancelable(true);

        VideoView videoView = (VideoView) dialog.findViewById(R.id.video_dialog_video_view);
        videoView.setZOrderOnTop(true);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				dialog.cancel();
			}
		});
        VideoPlayer vp = new VideoPlayer(videoView, mContext);
        vp.playVideo(uri);
        dialog.show();
	}
	public void setItemList(List<Session> sessions) {
		this.mSessionList = sessions;
	}
}