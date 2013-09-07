package com.encore;

import android.content.Context;
import android.media.MediaPlayer;
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
	
	public Audio (Context context, String pathname) {
		this.context = context;
		this.pathname = pathname;
	}
	
    @Override
    protected Void doInBackground(Void... params) {
        player = MediaPlayer.create(context, R.raw.loop1); 
        player.setLooping(true); // Set looping 
        player.setVolume(100,100); 
        startTime = System.currentTimeMillis();
        player.start(); 
        Log.d(tag, "Chillin in doInBackground()");
        while (!isCancelled() ){
        	
        }
        player.stop();
        player.reset();
        player.release();
        
        return null;
    }
    

    

    
    
  

}