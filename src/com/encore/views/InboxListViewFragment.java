package com.encore.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
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

import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.SessionTemp;
import com.encore.SessionView;
import com.encore.VideoPlayer;

public class InboxListViewFragment extends Fragment{
	private static String tag = "InboxListViewFragment";
	private VideoView videoView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.video_list_fragment, container, false);
		
	    ListView lv = (ListView) view.findViewById(R.id.video_list_view2);
	    List<SessionTemp> list = getTempResponseList();
	    lv.setAdapter(new InboxViewAdapter(container.getContext(), list));
	    lv.setOnItemClickListener(new ResponseListener());
	    return view;
    }
	
	
	private List<SessionTemp> getTempResponseList() {
		Drawable icon = getResources().getDrawable(R.drawable.action_people);
		List<SessionTemp> temp = new ArrayList<SessionTemp>();
		
		for (int i = 0; i < 10; ++i) {
			SessionTemp entry = new SessionTemp("Respond to clip " + i+"!", icon);
			entry.setUri(Uri.parse("/storage/sdcard0/DCIM/Camera/20130923_224141.mp4"));
			temp.add(entry);
		}
		
		return temp;
	}
	
	public class ResponseListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(tag, "parent = " + parent.toString() );
			Log.d(tag, "view = " + view.toString());
			SessionView element = (SessionView) view;
			Uri uri = element.getData().getUri();
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

}
