package com.encore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UploadService2 extends Service {

	private static String tag = "UploadService";
	private static String urlServer = "http://158.130.153.103:8000/upload/";

	ProgressDialog dialog = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(tag, "onCreate() Upload Service");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "onStartCommand()");
		pathToOurFile = intent.getStringExtra("path");
		Log.d(tag,"path: " + pathToOurFile);
		UploadTask uTask = new UploadTask();
		uTask.execute(null, null);
//		uploadFile();
//		stopSelf();
		return Service.START_NOT_STICKY;
	}

	@Override
	public ComponentName startService(Intent i) {
		super.startService(i);
		Log.d(tag, "startService() UploadService");
		
		return null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public class UploadTask extends AsyncTask<Void,Void,Void> {
		
		public UploadTask () {
			
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			uploadFile();
			stopSelf();
			return null;
		}
		
	}

	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;

	String pathToOurFile;
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";

	int bytesRead, bytesAvailable, bufferSize;
	byte[] buffer;
	int maxBufferSize = 1 * 1024 * 1024;

	public int uploadFile() {
		try {
			//Toast.makeText(this, "Uploading File", Toast.LENGTH_LONG).show();

			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			Log.d(tag,"posted in the paint");

			// Enable POST method
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"clip\";filename=\""
							+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			Log.d(tag,"reading");

			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);
			Log.d(tag,"getting responses");

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			//Toast.makeText(this, "Successfully uploaded file", Toast.LENGTH_LONG).show();
			Log.d(tag, "done");
			return serverResponseCode;
		} catch (Exception ex) {
			Log.d(tag, "Caught Exception");
			ex.printStackTrace();
		}
		return -1;
	}
}
