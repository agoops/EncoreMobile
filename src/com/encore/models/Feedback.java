package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 3/12/14.
 */
public class Feedback {
    @SerializedName("message")
    private String message;

    public Feedback(String message) {
        this.message = message;
    }
}
