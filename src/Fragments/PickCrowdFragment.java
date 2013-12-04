package Fragments;

import java.util.Arrays;
import java.util.List;

import util.T;
import widget.CrowdAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.encore.R;
import com.encore.API.APIService;
import com.encore.API.models.Crowd;
import com.encore.API.models.Crowds;
import com.encore.views.HomeActivity;
import com.google.gson.Gson;

public class PickCrowdFragment extends Fragment implements OnClickListener {
	private static final String TAG = "PickCrowdFragment";
	private View v;
	ListView crowdsLV;
	EditText sessionTitle;
	APIService api;
	CrowdAdapter adapter;
	private String jsonResult = "";
	private Crowd[] crowds = null;
	private List<Crowd> selectedCrowds;
	String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/testaudio.mp4";
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "PickCrowdFragment successfully launched");
		v = inflater.inflate(R.layout.pick_crowd_fragment, container, false);
		crowdsLV = (ListView) v.findViewById(R.id.crowds_lv);
		sessionTitle = (EditText) v.findViewById(R.id.session_title);
		
		// Make a 'GET crowds' request to find the user's crowds
		Intent apiIntent = new Intent(getActivity(), APIService.class);
		CrowdReceiver receiver = new CrowdReceiver(new Handler());
		apiIntent.putExtra(T.API_TYPE, T.GET_CROWDS);
		apiIntent.putExtra("receiver", receiver);
		getActivity().startService(apiIntent);
		
		// Set click listeners
		((Button) v.findViewById(R.id.send_button))
			.setOnClickListener((OnClickListener) this);
		((Button) v.findViewById(R.id.create_new_crowd_button))
		.setOnClickListener((OnClickListener) this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id)
		{
		case R.id.send_button:
			// The CrowdAdapter also keeps track of selected crowds 
			selectedCrowds = adapter.getSelectedCrowds();
			String seshTitle = sessionTitle.getText().toString();
			
			if(seshTitle == null || seshTitle.equals("")) {
				Toast.makeText(getActivity(), "Forgetting a session title?", Toast.LENGTH_LONG).show();
				
			} else if(selectedCrowds.size() <= 0) { 
				Toast.makeText(getActivity(), "Yo! Pick a crowd.", Toast.LENGTH_LONG).show();
				
			} else {
				// Only one crowd will ever be picked.
				int crowdId = selectedCrowds.get(0).getId();
				
				// Make a "POST sessions" to create a new session
				Intent apiIntent = new Intent(getActivity(),
						APIService.class);
				apiIntent.putExtra(T.API_TYPE, T.CREATE_SESSION);
				apiIntent.putExtra(T.FILEPATH, this.path);
				apiIntent.putExtra(T.SESSION_TITLE, seshTitle);
				apiIntent.putExtra(T.SESSION_USE_EXISTING_CROWD, true);
				apiIntent.putExtra(T.SESSION_CROWD_TITLE, "");
				apiIntent.putExtra(T.SESSION_CROWD_MEMBERS, "");
				apiIntent.putExtra(T.SESSION_CROWD_ID, "" + crowdId); 
				Log.d(TAG, "crowdId: " + crowdId);
				
				getActivity().startService(apiIntent);
				
				// Once sent, go back home
				Intent launchHome = new Intent(getActivity(), HomeActivity.class);
				startActivity(launchHome);
			}
			break;
		case R.id.create_new_crowd_button:
			// Begin a new transaction and create a CreateCrowdFragment
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			CreateCrowdFragment fragment = new CreateCrowdFragment();
			
			ft.replace(R.id.fragment_placeholder, fragment);
			ft.addToBackStack(null);
			ft.commit();
			break;
		default:
			break;
		}
	}
	
	public void populateListView() {
		adapter = new CrowdAdapter(getActivity(), R.layout.crowd_list_row, Arrays.asList(crowds));
		crowdsLV.setAdapter(adapter);
		crowdsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, android.view.View view,
		            int position, long id) {
		    	// Do something if a lv item is clicked? Could be useful...
		    }
		});
	}
	
	private class CrowdReceiver extends ResultReceiver {
        public CrowdReceiver(Handler handler) {
                super(handler);
        }
        
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == T.GET_CROWDS) {
                        Log.d(TAG, "APIService returned successful");
                        Gson mGson = new Gson();
                        jsonResult = resultData.getString("crowdsJson");
                        crowds = mGson.fromJson(jsonResult, Crowds.class).getCrowds();
                        populateListView();
                }
                else {
                        Log.d(TAG, "getCrowds() failed");
                }
        }
    }
}
