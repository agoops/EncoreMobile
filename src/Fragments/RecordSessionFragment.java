package Fragments;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import util.T;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
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
			.getAbsolutePath() + "/testaudio.pcm";
	
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
					record();
//					recorder.start();
				}
			});
			recordThread.start();
			break;
		}
		case R.id.btnStop: {
			Log.d(TAG, "stop clicked");
			enableButtons(false);
			isRecording = false;
//			recorder.stop();
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
			// Make a "POST sessions" to create a new session
//			Intent apiIntent = new Intent(getActivity(),
//					APIService.class);
//			apiIntent.putExtra(T.API_TYPE, T.CREATE_SESSION);
//			apiIntent.putExtra(T.SESSION_TITLE, sessionTitle);
//			apiIntent.putExtra(T.SESSION_USE_EXISTING_CROWD, true);
//			apiIntent.putExtra(T.SESSION_CROWD_TITLE, "");
//			apiIntent.putExtra(T.SESSION_CROWD_MEMBERS, "");
//			apiIntent.putExtra(T.SESSION_CROWD_ID, "" + crowdId); 
//			Log.d(TAG, "crowdId: " + crowdId);
//			
//			getActivity().startService(apiIntent);
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
	public void record() {
		Long time1 = System.currentTimeMillis();
		int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		File file = new File(path);

		// Delete any previous recording.
		if (file.exists())
			file.delete();

		// Create the new file.
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create "
					+ file.toString());
		}

		try {
			// Create a DataOuputStream to write the audio data into the saved
			// file.
			OutputStream os = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			DataOutputStream dos = new DataOutputStream(bos);

			// Create a new AudioRecord object to record the audio.
			int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz,
					channelConfiguration, audioEncoding);
			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, sampleRateInHz,
					channelConfiguration, audioEncoding, bufferSize);

			short[] buffer = new short[bufferSize];
			Long time2 = System.currentTimeMillis();
			audioRecord.startRecording();
			Long time3 = System.currentTimeMillis();
			Log.d(tag, "Time to set up audiorecord: " + (time2 - time1));
			Log.d(tag, "Time from startRecording() to while loop: "
					+ (time3 - time2));

			while (isRecording) {
				int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
				for (int i = 0; i < bufferReadResult; i++)
					dos.writeShort(buffer[i]);
			}

			audioRecord.stop();
			dos.close();

		} catch (Throwable t) {
			Log.e("AudioRecord", "Recording Failed");
		}
	}

	public void play() {
		// Get the file we want to playback.
		File file = new File(path);
		// Get the length of the audio stored in the file (16 bit so 2 bytes per
		// short)
		// and create a short array to store the recorded audio.
		int musicLength = (int) (file.length() / 2);
		short[] music = new short[musicLength];
		Log.d(tag, "size of file: " + file.length());

		try {
			// Create a DataInputStream to read the audio data back from the
			// saved file.
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);

			// Read the file into the music array.
			int i = 0;
			while (dis.available() > 0) {
				music[i] = dis.readShort();
				i++;
			}

			// Close the input streams.
			dis.close();

			// Create a new AudioTrack object using the same parameters as the
			// AudioRecord
			// object used to create the file.
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, musicLength,
					AudioTrack.MODE_STREAM);
			// Start playback
			audioTrack.play();

			// Write the music buffer to the AudioTrack object
			audioTrack.write(music, 0, musicLength);

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
