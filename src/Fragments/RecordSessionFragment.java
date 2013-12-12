package Fragments;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import util.T;
import android.content.ContentValues;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.encore.R;
import com.encore.RecMicToMp3;
import com.encore.API.APIService;
import com.encore.views.HomeActivity;

public class RecordSessionFragment extends Fragment implements OnClickListener {
	private static final String TAG = "RecordSessionFragment";
	private View v;
	boolean isRecording = false;
	int sampleRateInHz = 11025;
	int crowdId;
	String sessionTitle = null;
	String tag = "StartSession2";
	Thread recordThread = null;
	String path = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/testaudio.mp4";
	
	RecMicToMp3 recorder;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "RecordSessionFragment successfully launched");
		v = inflater.inflate(R.layout.record_session_fragment, container, false);
//		recorder = new RecMicToMp3(path,8000);
		
		crowdId = getArguments().getInt("crowdId");
		sessionTitle = getArguments().getString("sessionTitle");
		Log.d(TAG, "crowdId, sessionTitle: " + crowdId + ", " + sessionTitle);
		if(crowdId == -1) {
			// No crowdId - the user must be creating a new session
			// Hide the send button, show the next button
			((Button) v.findViewById(R.id.next_button)).setVisibility(View.VISIBLE);
			((Button) v.findViewById(R.id.send_clip_btn)).setVisibility(View.INVISIBLE);
		} else {
			// crowdId has already been set - the user must be replying
			// Show the send button, hide the next button
			Log.d(TAG, "About to switch button visibilities");
			((Button) v.findViewById(R.id.next_button)).setVisibility(View.INVISIBLE);
			((Button) v.findViewById(R.id.send_clip_btn)).setVisibility(View.VISIBLE);
		}
		
		setButtonHandlers();
		enableButtons(false);
		return v;                 
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStart: {
			Log.d(TAG, "record clicked");
			enableButtons(true);
			isRecording = true;
			recordThread = new Thread(new Runnable() {
				public void run() {
					recordAudio();
				}
			});
			recordThread.start();
			break;
		}
		case R.id.btnStop: {
			Log.d(TAG, "stop clicked");
			enableButtons(false);
			isRecording = false;
			break;
		}
		case R.id.btnPlayback: {
			try {
				recordThread.join();
			} catch (Exception e) {
			}

			play();
			break;

		}
		case R.id.next_button: {
			// TODO: First check if a recording has been made!
			// Create a new fragment transaction and PickCrowdFragment
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			PickCrowdFragment pickCrowdFragment = new PickCrowdFragment();
			pickCrowdFragment.setPath(path);
			// Replace the RecordSessionFragment with a PickCrowdFragment
			ft.replace(R.id.fragment_placeholder, pickCrowdFragment);
			ft.addToBackStack(null);
			ft.commit();
			break;
		}
		case R.id.send_clip_btn:
		{
			// Only one crowd will be picked at any given time
			// Add clip to the existing session
			int sessionId = getArguments().getInt("sessionId");
			
			Intent addClipIntent = new Intent(getActivity(), APIService.class);
			addClipIntent.putExtra(T.API_TYPE, T.ADD_CLIP);
			addClipIntent.putExtra(T.SESSION_ID, sessionId);
			addClipIntent.putExtra(T.FILEPATH, path); 
			getActivity().startService(addClipIntent);
			
			Intent launchHome = new Intent(getActivity(), HomeActivity.class);
			startActivity(launchHome);
		}
		default: {
			break;
		}
		}
	}
	/**
	 * 
	 * 
	 * 
	 * TODO
	 * 
	 * REPLACE THIS WITH RECORDING MP3
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void recordAudio() {
		Log.d(TAG, "recordAudio called");
		File file = new File(path);

		// Delete any previous recording.
		if (file.exists()) {
			file.delete();
		}
		// Create the new file.
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create "
					+ file.toString());
		}
		
	    final MediaRecorder recorder = new MediaRecorder();
	    ContentValues values = new ContentValues(3);
	    values.put(MediaStore.MediaColumns.TITLE, "mpegAudio");
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // VideoSource.CAMERA, eh?
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	    recorder.setOutputFile(path);
	    try {
	      recorder.prepare();
	    } catch (Exception e){
	        e.printStackTrace();
	    }

	    recorder.start();
	    while(isRecording) {
	    	continue;
	    }
	    recorder.stop();
	}

	public void play() {
		// Get the file we want to playback.
		File file = new File(path);
		Log.d(tag, "size of file: " + file.length());

		try {
			FileInputStream is = new FileInputStream(file);
			MediaPlayer player = new MediaPlayer();
			try {
				// MediaPlayer can only play a FileDescriptor,
				// so we convert our path into one
				player.setDataSource(is.getFD());
				is.close();
				player.prepare();
				player.start();
			} catch (IOException e) {
				Log.e(TAG, "prepare() failed");
			}

		} catch (Throwable t) {
			Log.e("AudioTrack", "Playback Failed");
			t.printStackTrace();
		}
	}

	private void setButtonHandlers() {
		((Button) v.findViewById(R.id.btnStart))
				.setOnClickListener((OnClickListener) this);
		((Button) v.findViewById(R.id.btnStop))
				.setOnClickListener((OnClickListener) this);
		((Button) v.findViewById(R.id.btnPlayback))
				.setOnClickListener((OnClickListener) this);
		((Button) v.findViewById(R.id.next_button))
				.setOnClickListener((OnClickListener) this);
		((Button) v.findViewById(R.id.send_clip_btn))
				.setOnClickListener((OnClickListener) this);
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) v.findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnStop, isRecording);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			finish();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
