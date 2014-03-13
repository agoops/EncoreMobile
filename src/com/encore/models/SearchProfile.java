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
    private Profile[] profiles;

    public SearchProfile() {
        this.profiles = null;
    }

    public SearchProfile(Profile[] profiles) {
        this.profiles = profiles;
    }

    public Profile[] getProfiles() {
        return profiles;
    }

    public List<Profile> getAsList() {
        if(profiles == null) {
            return new ArrayList<Profile>();
        }

        return new ArrayList<Profile>(Arrays.asList(profiles));
    }
}
