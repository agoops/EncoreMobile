package Fragments;

import util.T;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.encore.R;

public class NewsfeedFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	// Used to display the list's data
	SimpleCursorAdapter mAdapter;
	static final String[] PROJECTION = new String[] {
		T.SESSION_TITLE, T.SESSION_VIDEO_URL, 
		T.SESSION_LIKES, T.SESSION_COMMENTS, T.SESSION_SHARES};
	static final String SELECTION = "";
	
	static final Uri CONTENT_URI = Uri.parse("http://www.rapchat-django.herokuapp.com/sessions/newsfeed"); // replace with call to APIService
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.newsfeed_fragment, container, false);
		
		String[] fromColumns = {T.SESSION_TITLE, T.SESSION_VIDEO_URL, 
				T.SESSION_LIKES, T.SESSION_COMMENTS, T.SESSION_SHARES};

		int[] toViews = {R.id.newsfeed_session_title, R.id.newsfeed_session_video, R.id.newsfeed_session_likes,
				R.id.newsfeed_session_comments, R.id.newsfeed_session_shares};
		
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.newfeed_list_row, null,
				fromColumns, toViews, 0);
		setListAdapter(mAdapter);
		
		// Prepare the loader
		getLoaderManager().initLoader(0, null, this);
		
		// 0. Show loader
		// 1. Setup query
		// 2. Run in asynctask
		// 3. Populate LV with results
		
		return v;
	}
	
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// A CursorLoader will take care of creating a 
		// Cursor for the data being displayed
		return new CursorLoader(getActivity(), CONTENT_URI, 
				PROJECTION, SELECTION, null, null);
	}
	
	// Called when a previously created loader has finished loading
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}
	
	// Called when a previously created loader is reset, making data unavailable
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	@Override 
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
    }
}
