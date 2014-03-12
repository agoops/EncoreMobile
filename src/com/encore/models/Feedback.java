package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 3/12/14.
 */
public class Feedback {
    @SerializedName("Feedback")
    private String feedback;

    public Feedback(String feedback) {
        this.feedback = feedback;
    }
}
