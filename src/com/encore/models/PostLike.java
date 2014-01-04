package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 12/27/13.
 */
public class PostLike {
    @SerializedName("session")
    private int sessionId;

    public PostLike() {
        this.sessionId = -1;
    }

    public PostLike(int sessionId) {
        this.sessionId = sessionId;
    }
}
