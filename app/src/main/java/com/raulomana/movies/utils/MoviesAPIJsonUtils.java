package com.raulomana.movies.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raulomana.movies.model.Movie;
import com.raulomana.movies.model.Review;
import com.raulomana.movies.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoviesAPIJsonUtils {
    // more about the videos spec on https://developers.themoviedb.org/3/movies/get-movie-videos
    private static final String VIDEO_TYPE_TRAILER = "Trailer";

    private static final String VIDEO_SITE_YOUTUBE = "YouTube";
    private static final String VIDEO_SITE_VIMEO = "Vimeo";

    @NonNull
    public static List<Movie> getMoviesFromJson(@NonNull String json) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray results = jsonObject.getJSONArray("results");
        if(results != null) {
            for(int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                Movie movie = MoviesAPIJsonUtils.getMovieFromJson(item.toString());
                movies.add(movie);
            }
        }
        return movies;
    }

    @NonNull
    public static Movie getMovieFromJson(@NonNull String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        int id = 0;
        if(jsonObject.has("id")) {
            id = jsonObject.getInt("id");
        }
        String title = "";
        if(jsonObject.has("title")) {
            title = jsonObject.getString("title");
        }
        String description = "";
        if(jsonObject.has("overview")) {
            description = jsonObject.getString("overview");
        }
        String image = null;
        if(jsonObject.has("poster_path")) {
            String path = jsonObject.getString("poster_path");
            if(path != null) {
                image = NetworkUtils.MOVIES_IMAGES_BASE_URL + path;
            }
        }
        double rating = 0.0f;
        if(jsonObject.has("vote_average")) {
            rating = jsonObject.getDouble("vote_average");
        }
        double popularity = 0.0f;
        if(jsonObject.has("popularity")) {
            popularity = jsonObject.getDouble("popularity");
        }
        String releaseDate = "";
        String displayReleaseDate = "";
        if(jsonObject.has("release_date")) {
            releaseDate = jsonObject.getString("release_date");
            final SimpleDateFormat releaseDateInputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            final SimpleDateFormat releaseDateOutputFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
            displayReleaseDate = DateUtils.parseThenFormat(releaseDate, releaseDateInputFormat, releaseDateOutputFormat);
        }
        Integer runtime = null;
        if(jsonObject.has("runtime")) {
            runtime = jsonObject.getInt("runtime");
        }

        List<Video> videos = new ArrayList<>();
        if(jsonObject.has("videos")) {
            JSONObject videosJSON = jsonObject.getJSONObject("videos");
            if(videosJSON.has("results")) {
                JSONArray results = videosJSON.getJSONArray("results");
                for(int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    if(item.has("type") && item.has("key") && item.has("site") && item.has("name")) {
                        String type = item.getString("type");
                        if(VIDEO_TYPE_TRAILER.equals(type)) {
                            String key = item.getString("key");
                            String site = item.getString("site");
                            String name = item.getString("name");
                            String url = null;
                            if(VIDEO_SITE_YOUTUBE.equals(site)) {
                                url = "https://www.youtube.com/watch?v=" + key;
                            } else {
                                url = "https://vimeo.com/" + key;
                            }
                            videos.add(new Video(name, url, site, type));
                        }
                    }
                }
            }
        }

        List<Review> reviews = new ArrayList<>();
        if(jsonObject.has("reviews")) {
            JSONObject videosJSON = jsonObject.getJSONObject("reviews");
            if(videosJSON.has("results")) {
                JSONArray results = videosJSON.getJSONArray("results");
                for(int i = 0; i < results.length(); i++) {
                    JSONObject item = results.getJSONObject(i);
                    if(item.has("author") && item.has("content") && item.has("id") && item.has("url")) {
                        String author = item.getString("author");
                        String content = item.getString("content");
                        String reviewId = item.getString("id");
                        String url = item.getString("url");
                        reviews.add(new Review(reviewId, author, content, url));
                    }
                }
            }
        }

        return new Movie(id, title, description, image, rating, popularity, releaseDate, displayReleaseDate, runtime, false);
    }

    @NonNull
    public static List<Review> parseReviews(@Nullable String json) throws JSONException {
        if(json == null) {
            return new ArrayList<>(0);
        }

        List<Review> reviews = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        if(jsonObject.has("results")) {
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                if(item.has("author") && item.has("content") && item.has("id") && item.has("url")) {
                    String author = item.getString("author");
                    String content = item.getString("content");
                    String reviewId = item.getString("id");
                    String url = item.getString("url");
                    reviews.add(new Review(reviewId, author, content, url));
                }
            }
        }

        return reviews;
    }

    @NonNull
    public static List<Video> parseVideos(@Nullable String json) throws JSONException {
        if(json == null) {
            return new ArrayList<>(0);
        }

        List<Video> videos = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        if(jsonObject.has("results")) {
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                if(item.has("type") && item.has("key") && item.has("site") && item.has("name")) {
                    String type = item.getString("type");
                    if(VIDEO_TYPE_TRAILER.equals(type)) {
                        String key = item.getString("key");
                        String site = item.getString("site");
                        String name = item.getString("name");
                        String url = null;
                        if(VIDEO_SITE_YOUTUBE.equals(site)) {
                            url = "https://www.youtube.com/watch?v=" + key;
                        } else {
                            url = "https://vimeo.com/" + key;
                        }
                        videos.add(new Video(name, url, site, type));
                    }
                }
            }
        }

        return videos;
    }


}
