package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.encore.R;

public class VideoListFragment extends Fragment {
	
	
	 @Override
	    public View onCreateView(LayoutInflater inflater,
	            ViewGroup container, Bundle savedInstanceState) {
	        // The last two arguments ensure LayoutParams are inflated
	        // properly.
	        View rootView = inflater.inflate(
	                R.layout.video_list_fragment, container, false);
	        
	        ListView listView = (ListView)rootView.findViewById(R.id.video_list_view2);
	        return rootView;
	    }
}
