package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by babakpourkazemi on 3/10/14.
 */
public class Clips {
    @SerializedName("clips")
    private ArrayList<Clip> clips;

    public Clips(ArrayList<Clip> clips) {
        this.clips = clips;
    }

    public ArrayList<Clip> getClips() {
        return this.clips;
    }
}
