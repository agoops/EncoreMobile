package com.encore.views;

import java.util.ArrayList;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.VideoView;

import com.encore.R;
import com.encore.SessionView;
import com.encore.SessionViewAdapter;
import com.encore.VideoPlayer;
import com.encore.models.Session;
public class VideoListViewFragment extends Fragment{
	private static String tag = "VideoListViewFragment";
	private VideoView videoView;
	private SessionViewAdapter adapter;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.video_list_fragment, container, false);
		
	    ListView lv = (ListView) view.findViewById(R.id.video_list_view2);
	    adapter = new SessionViewAdapter(container.getContext(), new ArrayList<Session>());
	    lv.setAdapter(adapter);    
	    lv.setOnItemClickListener(new ViewVideoListener());
	    return view;
    }
	
	public SessionViewAdapter getAdapter() {
		return adapter;
	}
	
	public class ViewVideoListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(tag, "parent = " + parent.toString() );
			Log.d(tag, "view = " + view.toString());
			SessionView element = (SessionView) view;
			Uri uri = Uri.parse(element.getUri());
			Log.d(tag, "uri = " + uri.toString());
			
			showVideoDialog(uri);

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
        dialog.show();
	}

	
}
