package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class FriendRequestProfile {
    @SerializedName("sender")
    private User sender;

    @SerializedName("requested")
    private User requested;

    @SerializedName("is_accepted")
    private Boolean isAccepted;

    public FriendRequestProfile() {
        this.sender = null;
        this.requested = null;
        this.isAccepted = false;
    }

    public User getSender() {
        return sender;
    }

    public User getRequested() {
        return requested;
    }
}
