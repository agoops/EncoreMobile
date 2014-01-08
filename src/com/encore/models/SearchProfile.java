package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/7/14.
 */
public class SearchProfile {
    @SerializedName("profiles")
    private User[] users;

    public SearchProfile() {
        this.users = null;
    }

    public SearchProfile(User[] users) {
        this.users = users;
    }

    public User[] getUsers() {
        return users;
    }

    public List<User> getAsList() {
        return new ArrayList<User>(Arrays.asList(users));
    }
}
