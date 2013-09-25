package com.encore.views;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.encore.R;
import com.encore.SessionTemp;
import com.encore.SessionViewAdapter;

public class InboxListViewFragment extends Fragment{

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		
		View view = inflater.inflate(R.layout.video_list_fragment, container, false);
		
	    ListView lv = (ListView) view.findViewById(R.id.video_list_view2);
	    List<SessionTemp> list = getTempResponseList();
	    lv.setAdapter(new SessionViewAdapter(container.getContext(), list));
	    lv.setOnItemClickListener(new ResponseListener());
	    return view;
    }
	
	
	private List<SessionTemp> getTempResponseList() {
		Drawable icon = getResources().getDrawable(R.drawable.action_people);
		List<SessionTemp> temp = new ArrayList<SessionTemp>();
		
		for (int i = 0; i < 10; ++i) {
			temp.add(new SessionTemp("Respond to clip " + i + "!", icon));
		}
		
		return temp;
	}
	
	public class ResponseListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
		}
		
	}

}
