package com.encore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.VideoView;

import com.encore.API.APIService;
import com.encore.models.Comment;
import com.encore.models.Likes;
import com.encore.models.Session;
import com.encore.util.T;
import com.encore.widget.CommentDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxViewAdapter extends ArrayAdapter<Session> implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
	private List<Session> mSessionList;
	private static LayoutInflater inflater = null;
	private SessionView rowView;
    private boolean isLiked;
    private TextView titleTextView, crowdMembersTextView, likesTv, commentsTv;
    private ImageView likesIcon, commentsIcon;
    private Button reply;
    private com.encore.widget.AspectRatioImageView thumbnailIv;
    private Map<Integer, ImageView> likesMapping;
    private Map<Integer, Boolean> isLikedMapping;
    private Map<Integer, Integer> numLikesMapping;
    private Map<Integer, TextView> likesTvMapping;

    // TODO: Cap the listview, but make it never ending

	public InboxViewAdapter(Context c, int textViewResourceId, List<Session> sessions) {
		super(c, textViewResourceId, sessions);
		mContext = c;
		mSessionList = sessions;
        likesMapping = new HashMap<Integer, ImageView>();
        isLikedMapping = new HashMap<Integer, Boolean>();
        numLikesMapping = new HashMap<Integer, Integer>();
        likesTvMapping = new HashMap<Integer, TextView>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.inbox_view, parent, false);

        // Get the row views
        titleTextView = (TextView) convertView.findViewById(R.id.tvName);
        reply = (Button) convertView.findViewById(R.id.reply);
        likesTv = (TextView) convertView.findViewById(R.id.likes_tv);
        likesIcon = (ImageView) convertView.findViewById(R.id.likes_icon);
        commentsTv = (TextView) convertView.findViewById(R.id.comments_tv);
        commentsIcon = (ImageView) convertView.findViewById(R.id.comments_icon);
        thumbnailIv = (com.encore.widget.AspectRatioImageView) convertView.findViewById(R.id.inboxImageView);
        crowdMembersTextView = (TextView) convertView.findViewById(R.id.crowd_members_tv);

        // Set the position for each view
        reply.setTag(R.string.first_key, position);
        likesTv.setTag(R.string.first_key, position);
        likesIcon.setTag(R.string.first_key, position);
        commentsTv.setTag(R.string.first_key, position);
        commentsIcon.setTag(R.string.first_key, position);
        thumbnailIv.setTag(R.string.first_key, position);

        // Assign click listeners
        reply.setOnClickListener(this);
        likesTv.setOnClickListener(this);
        likesIcon.setOnClickListener(this);
        commentsTv.setOnClickListener(this);
        commentsIcon.setOnClickListener(this);
        thumbnailIv.setOnClickListener(this);

        // Get the most recently loaded session
        Session entry = mSessionList.get(position);

        // Maps sessionId <-> likesIcon
        likesMapping.put(entry.getId(), likesIcon);
        isLikedMapping.put(entry.getId(), false);
        numLikesMapping.put(entry.getId(), entry.getLikes());
        likesTvMapping.put(entry.getId(), likesTv);
        getUsersLikes(); // TODO: Should only call this once somewhere else
//        isLiked = (likesMapping.get(position) == null) ? false : true;
//        likesIcon.setImageResource((isLiked) ? R.drawable.like_red_invert : R.drawable.like_black_border);

        // Set session title
        titleTextView.setText(entry.getTitle());

        // Get the crowd members' names
        String names = entry.getMembersFirstNames();
        crowdMembersTextView.setText(names);

        // Get the session's comments
        List<Comment> comments = entry.getComments();

        // Set num comments and num likes
        if(comments.size() == 1) {
            commentsTv.setText(comments.size() + " comment");
        } else {
            commentsTv.setText(comments.size() + " comments");
        }

        int numLikes = entry.getLikes();
        if(numLikes == 1) {
            likesTv.setText(numLikes + " like");
        } else {
            likesTv.setText(numLikes + " likes");
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
		case R.id.comments_tv:
			// Open an AlertDialog which shows a session's comments,
			// and allows a user to post a new comment
			openComments(sesh);
			break;
        case R.id.comments_icon:
            openComments(sesh);
            break;
		case R.id.likes_tv:
            // TODO: Problem -> likesBtn refers to different row
            // Toggle like icon and set updated like count
//            int numLikes = sesh.getLikes();
//            Log.d(TAG, "session title: " + sesh.getTitle() + "\nlikesBtn: " + likesBtn.getId());
//            if(isLiked) {
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
//            isLiked = !isLiked;
            like(sesh);
			break;
        case R.id.likes_icon:
            like(sesh);
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

    private void like(Session sesh) {
        Log.d(TAG, "Making like request");
        Intent likesApi = new Intent(mContext, APIService.class);
        likesApi.putExtra(T.API_TYPE, T.CREATE_LIKE);
        likesApi.putExtra(T.SESSION_ID, sesh.getId());
        mContext.startService(likesApi);

        toggleLikeIcon(sesh.getId());
    }

    private void toggleLikeIcon(int sessionId) {
        // Get the like icon
        ImageView tempIcon = likesMapping.get(sessionId);

        // Determine if it's already been liked
        isLiked = (isLikedMapping.get(sessionId));

        // Change the icon accordingly
        tempIcon.setImageResource( isLiked ?
                R.drawable.like_black_border : R.drawable.like_red_invert);
        isLikedMapping.put(sessionId, isLiked = !isLiked);
        
        // Update the num likes text
        int numLikes = numLikesMapping.get(sessionId);
        TextView tempTv = likesTvMapping.get(sessionId);
        Log.d(TAG, "numLikes: " + numLikes);

        if(isLiked) {
            if(numLikes + 1 == 1) {
                Log.d(TAG, "1");
                tempTv.setText("1 like");
            } else {
                Log.d(TAG, "2");
                tempTv.setText(numLikes + 1 + " likes");
            }
        } else {
            if(numLikes == 1) {
                Log.d(TAG, "3");
                tempTv.setText("1 like");
            } else {
                Log.d(TAG, "4");
                tempTv.setText(numLikes + " likes");
            }
        }

    }

    private void openComments(Session sesh) {
        Log.d(TAG, "Opening comments dialog");
        CommentDialog cDialog = new CommentDialog(mContext);
        Bundle dArgs = new Bundle();
        String json = (new Gson()).toJson(sesh.getComments());
        dArgs.putInt("sessionId", sesh.getId());
        dArgs.putString("comments", json);
        cDialog.setDialogArgs(dArgs);
        cDialog.show();
    }

    private void getUsersLikes() {
        Intent api = new Intent(mContext, APIService.class);
        LikesReceiver likesReceiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, likesReceiver);
        mContext.startService(api);
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

    private class LikesReceiver extends ResultReceiver {
        public LikesReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 1) {
                Log.d(TAG, "APIService returned successful with likes");
                String result = resultData.getString("result");
                Log.d(TAG, "result from apiservice is: " + result);
                int[] likedSessionIds = new Gson().fromJson(result, Likes.class)
                        .getLikedSessionIds();

                // Set liked sessions with an updated icon
                ImageView tempIcon;
                for(int sessionId : likedSessionIds) {
                    tempIcon = likesMapping.get(sessionId);
                    if(tempIcon != null) {
                        tempIcon.setImageResource(R.drawable.like_red_invert);
                        isLikedMapping.put(sessionId, true);
                    }
                }

                // TODO: Progress bar should stop here
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