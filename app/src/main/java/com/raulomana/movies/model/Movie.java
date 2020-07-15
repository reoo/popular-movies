package com.raulomana.movies.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Movie implements Parcelable {
    int id;
    @NonNull
    private String title;
    @Nullable
    private String description;
    @Nullable
    private String image;
    private double rating;
    private double popularity;
    @NonNull
    private String releaseDate;
    @Nullable
    private Integer runtime;

    public Movie(int id, @NonNull String title, @Nullable String description, @Nullable String image, double rating, double popularity, @NonNull String releaseDate, @Nullable Integer runtime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.rating = rating;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        rating = in.readDouble();
        popularity = in.readDouble();
        releaseDate = in.readString();
        if (in.readByte() == 0) {
            runtime = null;
        } else {
            runtime = in.readInt();
        }
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    public void setImage(@Nullable String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    @NonNull
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(@NonNull String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Nullable
    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(@Nullable Integer runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeDouble(rating);
        dest.writeDouble(popularity);
        dest.writeString(releaseDate);
        if (runtime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(runtime);
        }
    }
}
