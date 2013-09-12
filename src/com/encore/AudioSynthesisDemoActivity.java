package com.encore;

import android.app.Activity;
import android.os.Bundle;

public class AudioSynthesisDemoActivity extends Activity {
	Metronome m;
	
	
	@Override
	public void onPause() {
		super.onPause();
		m.stop();
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);
        m = new Metronome();
        m.setBpm(120);
        
        m.play();
//        AudioGenerator audio = new AudioGenerator(8000);
//
//        double[] silence = audio.getSineWave(200, 8000, 0);
//
//        int noteDuration = 2400;
//
//        double[] doNote = audio.getSineWave(noteDuration/2, 8000, 523.25);
//        double[] reNote = audio.getSineWave(noteDuration/2, 8000, 587.33);
//        double[] faNote = audio.getSineWave(noteDuration, 8000, 698.46);
//        double[] laNote = audio.getSineWave(noteDuration, 8000, 880.00);
//        double[] laNote2 =
//                audio.getSineWave((int) (noteDuration*1.25), 8000, 880.00);
//        double[] siNote = audio.getSineWave(noteDuration/2, 8000, 987.77);
//        double[] doNote2 =
//                audio.getSineWave((int) (noteDuration*1.25), 8000, 523.25);
//        double[] miNote = audio.getSineWave(noteDuration/2, 8000, 659.26);
//        double[] miNote2 = audio.getSineWave(noteDuration, 8000, 659.26);
//        double[] doNote3 = audio.getSineWave(noteDuration, 8000, 523.25);
//        double[] miNote3 = audio.getSineWave(noteDuration*3, 8000, 659.26);
//        double[] reNote2 = audio.getSineWave(noteDuration*4, 8000, 587.33);
//
//        audio.createPlayer();
//        audio.writeSound(doNote);
//        audio.writeSound(silence);
//        audio.writeSound(reNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(laNote);
//        audio.writeSound(silence);
//        audio.writeSound(laNote2);
//        audio.writeSound(silence);
//        audio.writeSound(siNote);
//        audio.writeSound(silence);
//        audio.writeSound(laNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(doNote2);
//        audio.writeSound(silence);
//        audio.writeSound(miNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(miNote2);
//        audio.writeSound(silence);
//        audio.writeSound(doNote3);
//        audio.writeSound(silence);
//        audio.writeSound(miNote3);
//        audio.writeSound(silence);
//        audio.writeSound(doNote);
//        audio.writeSound(silence);
//        audio.writeSound(reNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(laNote);
//        audio.writeSound(silence);
//        audio.writeSound(laNote2);
//        audio.writeSound(silence);
//        audio.writeSound(siNote);
//        audio.writeSound(silence);
//        audio.writeSound(laNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(doNote2);
//        audio.writeSound(silence);
//        audio.writeSound(miNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(faNote);
//        audio.writeSound(silence);
//        audio.writeSound(miNote2);
//        audio.writeSound(silence);
//        audio.writeSound(miNote2);
//        audio.writeSound(silence);
//        audio.writeSound(reNote2);
//
//        audio.destroyAudioTrack();

    }
}