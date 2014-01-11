package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 1/5/14.
 */
public class UpdateUser {
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone_number")
    private String phoneNumber;

    public UpdateUser() {
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
    }

    public UpdateUser(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
