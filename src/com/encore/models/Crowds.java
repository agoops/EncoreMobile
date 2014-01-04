package com.encore.models;

import com.google.gson.annotations.SerializedName;

public class Crowds {
    @SerializedName("crowds")
    private Crowd[] crowds;

    public Crowds() {
        this.crowds = null;
    }

    public Crowds(Crowd[] crowds) {
        this.crowds = crowds;
    }

    public Crowd[] getCrowds() {
        return this.crowds;
    }

    public String toString() {
        String result = "";
        for(Crowd c : crowds) {
            result += c.toString() + "\n";
        }
        return result;
    }
}
