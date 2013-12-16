package com.encore.views;

import java.util.ArrayList;
import java.util.Arrays;

import util.T;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.SessionView;
import com.encore.VideoPlayer;
import com.encore.API.APIService;
import com.encore.API.models.Session;
import com.encore.API.models.Sessions;
import com.google.gson.Gson;

public class InboxListViewFragment extends Fragment{
	private static final String TAG = "InboxListViewFragment";
	private VideoView videoView;
	private InboxViewAdapter adapter;
	private Session[] sessions;
	private ListView sessionsLV;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.video_list_fragment, container, false);
		sessionsLV = (ListView) view.findViewById(R.id.video_list_view2);
		
		adapter = new InboxViewAdapter(getActivity(), R.layout.inbox_view, null);
		sessionsLV.setAdapter(adapter);
		
	    ResultReceiver receiver = new SessionListReceiver(new Handler());
	    getRaps(receiver);
		
//	    lv.setOnItemClickListener(new ResponseListener());
	    return view;
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
	
	private void populateListView() {
		sessionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, android.view.View view,
		            int position, long id) {
		    	// Do something if a lv item is clicked? Could be useful...
		    }
		});
	}
	
	private class SessionListReceiver extends ResultReceiver {
        public SessionListReceiver(Handler handler) {
                super(handler);
                // TODO Auto-generated constructor stub
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 1) {
                        Log.d(TAG, "APIService returned successfully with sessions");
                        
                        String result = resultData.getString("result");
                        sessions = (new Gson()).fromJson(result, Sessions.class).getSessions();
//                        populateListView();
                        // TODO: Use the async calls below
                        adapter.setItemList(Arrays.asList(sessions));
                        adapter.notifyDataSetChanged();
                } else {
                        Log.d(TAG, "APIService get session failed?");
                }
        }
	}
}
