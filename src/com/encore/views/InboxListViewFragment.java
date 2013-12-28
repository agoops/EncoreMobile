package com.encore.views;

import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.encore.API.APIService;
import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.SessionView;
import com.encore.VideoPlayer;
import com.encore.models.Session;
import com.encore.models.Sessions;
import com.encore.util.T;
import com.google.gson.Gson;

import java.util.Arrays;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class InboxListViewFragment extends Fragment implements OnRefreshListener {
	private static final String TAG = "InboxListViewFragment";
	private VideoView videoView;
	private InboxViewAdapter adapter;
	private Session[] sessions;
	private ListView listView;
	private ProgressBar progressBar;
    private static ResultReceiver receiver;
	private PullToRefreshLayout pullToRefreshLayout;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.video_list_fragment, container, false);

		// Show Progress Bar
		progressBar = (ProgressBar) view.findViewById(R.id.progress_inbox);
		progressBar.setVisibility(View.VISIBLE);

//		// Setup pull to refresh
		pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.pulltorefresh_inbox);
		ActionBarPullToRefresh.from(getActivity())
            .options(Options.create().refreshOnUp(true).build())
			.allChildrenArePullable()
			.listener(this)
			.setup(pullToRefreshLayout);

		listView = (ListView) view.findViewById(R.id.video_list_view2);

		// Populate inbox
		adapter = new InboxViewAdapter(getActivity(), 0, null);
		listView.setAdapter(adapter);

	    receiver = new SessionListReceiver(new Handler());
	    getRaps(receiver);


//	    lv.setOnItemClickListener(new ResponseListener());
	    return view;
    }


    @Override
    public void onRefreshStarted(View view) {
        getRaps(receiver);
    }

	public void getRaps(ResultReceiver receiver) {
		Intent api = new Intent(getActivity(), APIService.class);
		api.putExtra(T.API_TYPE, T.GET_SESSIONS);
		api.putExtra(T.RECEIVER, receiver);

		getActivity().startService(api);
	}

	public InboxViewAdapter getAdapter () {
		return adapter;
	}

	public class ResponseListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			SessionView sv = (SessionView) view;
			Session session = sv.getSession();
			int sessionId = session.getId();
			Toast.makeText(getActivity(), "sessionId: " + sessionId, Toast.LENGTH_SHORT);
			// TODO: Make request to last clip with session id
			// TODO: Play locally using url
			MediaPlayer mp = new MediaPlayer();
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mp.setDataSource(url);
//			mp.prepare();
//			mp.start();
		}
    }

	private void showVideoDialog(Uri uri) {

		final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.video_dialog);
        dialog.setCancelable(true);

        this.videoView = (VideoView) dialog.findViewById(R.id.video_dialog_video_view);
        videoView.setZOrderOnTop(true);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				dialog.cancel();
			}
		});
        VideoPlayer vp = new VideoPlayer(this.videoView, getActivity());
        vp.playVideo(uri);

        //set up button
//        Button button = (Button) dialog.findViewById(R.id.cancel);
//        button.setOnClickListener(new OnClickListener() {
//        @Override
//            public void onClick(View v) {
//                dialog.cancel();;
//            }
//        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
	}

	private class SessionListReceiver extends ResultReceiver {
        public SessionListReceiver(Handler handler) {
                super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 1) {
                        Log.d(TAG, "APIService returned successfully with sessions");
                        // hide progress bar
                        progressBar.setVisibility(View.GONE);
                        
                        String result = resultData.getString("result");
                        sessions = (new Gson()).fromJson(result, Sessions.class).getSessions();
                        adapter.setItemList(Arrays.asList(sessions));
                        adapter.notifyDataSetChanged();
                        pullToRefreshLayout.setRefreshComplete();
                } else {
                        Log.d(TAG, "APIService get session failed?");
                }
        }
	}

}
