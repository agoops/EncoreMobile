package com.encore;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class Audio extends AsyncTask<Void, Void, Void> {
	private static String tag = "Audio";
	Context context;
	String pathname;
	MediaPlayer player;
	Long startTime;
	Long millisInStart;
	Long millisInEnd;
	AudioInfo audioInfo;
	
	public Audio (Context context, String pathname, AudioInfo audioInfo) {
		this.context = context;
		this.pathname = pathname;
		this.audioInfo = audioInfo;
	}
	
    @Override
    protected Void doInBackground(Void... params) {
    	Uri uri = Uri.parse(pathname);
    	Log.d(tag, "here in Audio");
    	Log.d(tag, "path: " + pathname);
       // player = MediaPlayer.create(context, R.raw.loop1); 
    	player = MediaPlayer.create(context,uri); 
        player.setLooping(true); // Set looping 
        player.setVolume(100,100); 
        //audioInfo.setSongStart(System.currentTimeMillis());
        player.start(); 
        publishProgress();
        Log.d(tag, "Chillin in doInBackground()");
        while (!isCancelled() ){
        	
        }
        player.stop();
        player.reset();
        player.release();
        
        return null;
    }
    

    

               
    
  

}