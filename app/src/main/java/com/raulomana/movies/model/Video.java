package com.raulomana.movies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Video implements Parcelable {
    @NonNull
    private String name;
    @NonNull
    private String url;
    @NonNull
    private String site;
    @NonNull
    private String type;

    public Video(@NonNull String name, @NonNull String url, @NonNull String site, @NonNull String type) {
        this.name = name;
        this.url = url;
        this.site = site;
        this.type = type;
    }

    protected Video(Parcel in) {
        name = in.readString();
        url = in.readString();
        site = in.readString();
        type = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getSite() {
        return site;
    }

    public void setSite(@NonNull String site) {
        this.site = site;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(site);
        dest.writeString(type);
    }
}
