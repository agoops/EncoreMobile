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
import android.widget.ProgressBar;
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
import java.util.HashSet;
import java.util.List;

public class InboxViewAdapter extends ArrayAdapter<Session> implements OnClickListener {
	
	private static final String TAG = "InboxViewAdapter";
	private Context mContext;
	private List<Session> mSessionList;
	private static LayoutInflater inflater = null;
	private SessionView rowView;
    private TextView titleTextView, crowdMembersTextView, likesTv, commentsTv;
    private ImageView commentsIcon;
    private Button reply, likeButton;
    private com.encore.widget.AspectRatioImageView thumbnailIv;
    private ProgressBar thumbnailProgressBar;

    private HashSet<Integer> likedSessionIds;

    // TODO: Cap the listview, but make it never ending

	public InboxViewAdapter(Context c, int textViewResourceId, List<Session> sessions) {
		super(c, textViewResourceId, sessions);
		mContext = c;
		mSessionList = sessions;

        // When the adapter is instantiated, we'll the user's liked sessions
        likedSessionIds = null;
        getUsersLikes();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.inbox_view, parent, false);
//        showProgressBar(convertView);

        Log.d(TAG, "unavailable");
        while(likedSessionIds == null) {
            // Wait until we've gotten our liked session ids
            Log.d(TAG, "waiting...");
            continue;
        }
        Log.d(TAG, "available");

        // Get the most recently loaded session
        Session entry = mSessionList.get(position);

        initializeViews(convertView);
        setTags(entry);
        assignOnClickListeners();
        populateViewsWithData(convertView, entry);

        return convertView;
    }

    public void initializeViews(View convertView) {
        // Get the row views
        titleTextView = (TextView) convertView.findViewById(R.id.session_title_tv);
        reply = (Button) convertView.findViewById(R.id.reply);
        likeButton = (Button) convertView.findViewById(R.id.like_button);
        likesTv = (TextView) convertView.findViewById(R.id.likes_tv);
        commentsTv = (TextView) convertView.findViewById(R.id.comments_tv);
        commentsIcon = (ImageView) convertView.findViewById(R.id.comments_icon);
        thumbnailIv = (com.encore.widget.AspectRatioImageView) convertView.findViewById(R.id.inboxImageView);
        crowdMembersTextView = (TextView) convertView.findViewById(R.id.crowd_members_tv);
        thumbnailProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_thumbnail);
    }

    public void setTags(Session entry) {
        // Assign the appropriate data to each view
        int numLikes = entry.getLikes();

        reply.setTag(R.string.first_key, entry);
        likeButton.setTag(R.string.first_key, entry);
        likesTv.setTag(R.string.first_key, entry);
        commentsTv.setTag(R.string.first_key, entry);
        commentsIcon.setTag(R.string.first_key, entry);
        thumbnailIv.setTag(R.string.first_key, entry);

        likesTv.setTag(R.string.second_key, likeButton); // TV will need the button's selector status
        likesTv.setTag(R.string.third_key, likesTv);
        likeButton.setTag(R.string.second_key, likeButton);
        likeButton.setTag(R.string.third_key, likesTv);
    }

    public void assignOnClickListeners() {
        // Assign click listeners
        reply.setOnClickListener(this);
        likesTv.setOnClickListener(this);
        commentsTv.setOnClickListener(this);
        commentsIcon.setOnClickListener(this);
        thumbnailIv.setOnClickListener(this);
        likeButton.setOnClickListener(this);
    }

    public void populateViewsWithData(View convertView, Session entry) {
        // Set session title
        titleTextView.setText(entry.getTitle());

        // Get the crowd members' names
        String names = entry.getMembersFirstNames();
        crowdMembersTextView.setText(names);

        // Set the appropriate like icon
        int sessionId = entry.getId();
        likeButton.setSelected(
                likedSessionIds.contains(sessionId));

        // set num comments and num likes
        List<Comment> comments = entry.getComments();
        int commentsSize = comments.size();
        if(commentsSize == 1) {
            commentsTv.setText(commentsSize + " comment");
        } else {
            commentsTv.setText(commentsSize + " comments");
        }

        int numLikes = entry.getLikes();
        if(numLikes == 1) {
            likesTv.setText(numLikes + " like");
        } else {
            likesTv.setText(numLikes + " likes");
        }

        // Set the thumbnail
        if(entry.getThumbnailUrl() != null) {
            new DownloadImageTask(
                    (com.encore.widget.AspectRatioImageView) convertView.findViewById(R.id.inboxImageView), thumbnailProgressBar)
                    .execute(entry.getThumbnailUrl());
        }
    }

    @Override
    public int getCount() {
        if(mSessionList != null) {
            return mSessionList.size();
        }

        return 0;
    }

	@Override
	public void onClick(View v) {
//        final Session sesh = (Session) v.getTag();
        Session sesh = (Session) v.getTag(R.string.first_key);

        switch(v.getId())
		{
        case R.id.reply:
            // Pass crowdId and sessionTitle on to StartSession2
            // who in turn passes it on to RecordFragment
            launchRecordFragment(sesh);
            break;
		case R.id.comments_tv:
			// Open an AlertDialog to show comments
			openComments(sesh);
			break;
        case R.id.comments_icon:
            // Open an AlertDialog to show comments
            openComments(sesh);
            break;
		case R.id.likes_tv:
            likeServerSide(sesh); // POST like
            likeClientSide(sesh, v); // Update count & toggle icon
			break;
        case R.id.like_button:
            likeServerSide(sesh); // POST like
            likeClientSide(sesh, v); // Update count & toggle icon
            break;
		case R.id.inboxImageView:
			// Clicking the thumbnail will play the video
			playVideo(sesh);
            break;
        default:
            break;
		}
	}

    private void launchRecordFragment(Session sesh) {
        int crowdId = sesh.getCrowdId();
        Intent launchRecordFragment = new Intent(mContext, CameraActivity2.class);
        launchRecordFragment.putExtra("crowdId", crowdId);
        launchRecordFragment.putExtra("sessionTitle", sesh.getTitle());
        launchRecordFragment.putExtra("sessionId", sesh.getId());
        ((Activity) mContext).startActivity(launchRecordFragment);
    }

    private void likeServerSide(Session sesh) {
        Log.d(TAG, "Making like request");
        Intent likesApi = new Intent(mContext, APIService.class);
        likesApi.putExtra(T.API_TYPE, T.CREATE_LIKE);
        likesApi.putExtra(T.SESSION_ID, sesh.getId());
        mContext.startService(likesApi);
    }

    private void likeClientSide(Session sesh, View v) {
        Log.d(TAG, "updating likes count");
        Button tempLikesButton = (Button) v.getTag(R.string.second_key);
        TextView tempLikesTv = (TextView) v.getTag(R.string.third_key);
        boolean isCurrentlyLiked = tempLikesButton.isSelected();
        int numLikes = sesh.getLikes();

        if(isCurrentlyLiked) {
            // Decrement
            tempLikesTv.setText(
                    (numLikes - 1 == 1) ?
                            numLikes - 1 + " like" : numLikes - 1 + " likes");
            sesh.setLikes(numLikes - 1);
            likedSessionIds.remove(sesh.getId());
        } else {
            // Increment
            tempLikesTv.setText(
                    (numLikes + 1 == 1) ?
                            numLikes + 1 + " like" : numLikes + 1 + " likes");
            sesh.setLikes(numLikes + 1);
            likedSessionIds.add(sesh.getId());
        }
        tempLikesButton.setSelected(!tempLikesButton.isSelected());
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

    private void playVideo(Session sesh) {
        Intent clipApi = new Intent(mContext, APIService.class);
        ResultReceiver receiver = new ClipStreamReceiver(new Handler());
        int sessionId = sesh.getId();

        clipApi.putExtra(T.API_TYPE, T.GET_CLIP_STREAM);
        clipApi.putExtra(T.SESSION_ID, sessionId);
        clipApi.putExtra(T.RECEIVER, receiver);
        mContext.startService(clipApi);
    }

    private void getUsersLikes() {
        Intent api = new Intent(mContext, APIService.class);
        LikesReceiver likesReceiver = new LikesReceiver(new Handler());
        api.putExtra(T.API_TYPE, T.GET_LIKES);
        api.putExtra(T.RECEIVER, likesReceiver);
        mContext.startService(api);
    }

    private void hideProgressBar(ProgressBar thumbnailPb) {
        thumbnailPb.setVisibility(View.GONE);;
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
                likedSessionIds = new Gson().fromJson(result, Likes.class)
                        .getLikedSessionIds();
            } else {
                Log.d(TAG, "APIService get session clip url failed?");
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        com.encore.widget.AspectRatioImageView thumbnailIv;
        ProgressBar thumbnailPb;

        public DownloadImageTask(com.encore.widget.AspectRatioImageView bitmapImage, ProgressBar thumbnailPb) {
            this.thumbnailIv = bitmapImage;
            this.thumbnailPb = thumbnailPb;
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
            hideProgressBar(thumbnailPb);
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

    // Used in InboxListViewFragment
	public void setItemList(List<Session> sessions) {
		this.mSessionList = sessions;
	}
}