package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 1/8/14.
 */
public class ApiError {
    @SerializedName("error")
    private String error;

    public ApiError() {
        this.error = null;
    }

    public ApiError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
