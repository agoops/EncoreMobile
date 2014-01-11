package com.encore.models;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

/**
 * Created by babakpourkazemi on 1/8/14.
 */
public class Paginator {
    @SerializedName("count")
    private int count;

    @SerializedName("next")
    private URL next;

    @SerializedName("previous")
    private URL previous;

    @SerializedName("results")
    private Session[] results;

    public Paginator() {
        this.count = -1;
        this.next = null;
        this.previous = null;
        this.results = null;
    }

    public Paginator(int count, URL next, URL previous, Session[] results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public Session[] getResults() {
        return results;
    }

    public URL getNext() {
        return next;
    }

    public URL getPrevious() {
        return previous;
    }
}
