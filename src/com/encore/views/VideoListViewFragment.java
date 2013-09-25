package com.encore.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.encore.R;
import com.encore.SessionTemp;
import com.encore.SessionView;
import com.encore.SessionViewAdapter;

public class VideoListViewFragment extends Fragment{
	private static String tag = "VideoListViewFragment";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.video_list_fragment, container, false);
		
	    ListView lv = (ListView) view.findViewById(R.id.video_list_view2);
	    List<SessionTemp> list = getTempSessionList();
	    lv.setAdapter(new SessionViewAdapter(container.getContext(), list));    
	    lv.setOnItemClickListener(new ViewVideoListener());
	    return view;
    }
	
	
	private List<SessionTemp> getTempSessionList() {
		Drawable icon = getResources().getDrawable(R.drawable.action_people);
		List<SessionTemp> temp = new ArrayList<SessionTemp>();
		
		for (int i = 0; i < 10; ++i) {
			SessionTemp entry = new SessionTemp("This is session " + i, icon);
			entry.setUri(Uri.parse("/storage/sdcard0/DCIM/Camera/20130923_224141.mp4"));
			temp.add(entry);
		}
		
		return temp;
	}
	private Context getApplicationContext() {
		return getApplicationContext();
	}
	public class ViewVideoListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(tag, "parent = " + parent.toString() );
			Log.d(tag, "view = " + view.toString());
			SessionView element = (SessionView) view;
			Uri uri = element.getData().getUri();
			Log.d(tag, "uri = " + uri.toString());
			
			Toast.makeText(getActivity().getBaseContext(), uri.toString(),
                    Toast.LENGTH_SHORT).show();
//			startActivity(new Intent(Intent.ACTION_VIEW, uri));
//		    Log.i("Video", "Video Playing....");

		}
		
	}
	
}
