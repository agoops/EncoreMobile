package Fragments;

import java.util.ArrayList;
import java.util.List;

import util.T;
import widget.SessionsAdapter;
import Bus.BusProvider;
import Bus.SessionsEvent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.encore.R;
import com.encore.API.APIService;
import com.encore.API.models.Session;
import com.squareup.otto.Subscribe;

public class NewsfeedFragment extends ListFragment { // implements LoaderManager.LoaderCallbacks<Cursor> 
	// Used to display the list's data
//	SimpleCursorAdapter mAdapter;
//	static final String[] PROJECTION = new String[] {
//		T.SESSION_TITLE, T.SESSION_VIDEO_URL, 
//		T.SESSION_LIKES, T.SESSION_COMMENTS, T.SESSION_SHARES};
//	static final String SELECTION = "";
	
	SessionsAdapter mAdapter;
	static List<Session> mData;
	APIService api;
	ListView lv;
	
	static final Uri CONTENT_URI = Uri.parse("http://www.rapchat-django.herokuapp.com/sessions/"); // replace with call to APIService
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.newsfeed_fragment, container, false);
		BusProvider.getInstance().register(this);
		
		api = new APIService();
		
//		mAdapter = new SimpleCursorAdapter(getActivity(),
//				R.layout.newsfeed_list_row, null,
//				fromColumns, toViews, 0);
//		setListAdapter(mAdapter);

		
//		Bus related shiz
		
//		Intent apiIntent = new Intent(getActivity(), APIService.class);
//		apiIntent.putExtra(T.API_TYPE, T.GET_SESSIONS);
		
//		getActivity().startService(apiIntent);
		
//		populateNewsfeed();
		
//		String[] fromColumns = {T.SESSION_TITLE, T.SESSION_VIDEO_URL, 
//				T.SESSION_LIKES, T.SESSION_COMMENTS, T.SESSION_SHARES};
//
//		int[] toViews = {R.id.newsfeed_session_title, R.id.newsfeed_session_video, R.id.newsfeed_session_likes,
//				R.id.newsfeed_session_comments, R.id.newsfeed_session_shares};
//		
//		mAdapter = new SimpleCursorAdapter(getActivity(),
//				R.layout.newfeed_list_row, null,
//				fromColumns, toViews, 0);
//		setListAdapter(mAdapter);
		
		// Prepare the loader
//		getLoaderManager().initLoader(0, null, this);
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<Session> dummy = new ArrayList<Session>();
		dummy.add(new Session());
		dummy.add(new Session());
		dummy.add(new Session());
		
		mAdapter = new SessionsAdapter(getActivity(), 0, dummy);
		setListAdapter(mAdapter);
	}
	
	public void populateNewsfeed() {
	}
	
	@Subscribe public void sessionsAvailable(SessionsEvent event) {
		// Grab sessions from bus
		mData = event.sessions;
		Log.d("Newsfeed Fragment", "Sessions event triggered: " + mData.toString());
		
		// Populate listview with it
	}
	
	
	
	// -----------------------------------------------
	// -----------------------------------------------
	
	// Called when a new Loader needs to be created
//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		// A CursorLoader will take care of creating a 
//		// Cursor for the data being displayed
//		return new CursorLoader(getActivity(), CONTENT_URI, 
//				PROJECTION, SELECTION, null, null);
//	}
//	
//	// Called when a previously created loader has finished loading
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		mAdapter.swapCursor(data);
//	}
//	
//	// Called when a previously created loader is reset, making data unavailable
//	public void onLoaderReset(Loader<Cursor> loader) {
//		mAdapter.swapCursor(null);
//	}
//	
//	@Override 
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Do something when a list item is clicked
//    }
}
