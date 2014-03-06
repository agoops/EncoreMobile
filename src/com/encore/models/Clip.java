package com.encore.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by babakpourkazemi on 3/5/14.
 */
public class Clip {
    @SerializedName("id")
    private int id;

    @SerializedName("clip")
    private String clip;

    @SerializedName("thumbnail_url")
    private String thumbnail_url;

    @SerializedName("url")
    private String url;

    @SerializedName("clip_num")
    private int clip_num;

    @SerializedName("creator")
    private int creator;

    @SerializedName("session")
    private int session;

    @SerializedName("created")
    private String created;

    @SerializedName("modified")
    private String modified;

    public Clip(String clip, String thumbnail_url, String url, int clip_num,
                int creator, int session, String created, String modified, int id) {
        this.clip = clip;
        this.thumbnail_url = thumbnail_url;
        this.url = url;
        this.clip_num = clip_num;
        this.creator = creator;
        this.session = session;
        this.created = created;
        this.modified = modified;
        this.id = id;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public String getUrl() {
        return url;
    }

    public int getClip_num() {
        return clip_num;
    }

    public int getCreator() {
        return creator;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }
}
