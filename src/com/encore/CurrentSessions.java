package com.encore;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class CurrentSessions extends Activity {
	
	private ListView mListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_sessions);
		mListView = (ListView) findViewById(R.id.session_list_view);
		SessionViewAdapter adapter = new SessionViewAdapter(this, null);
	
	}
}
