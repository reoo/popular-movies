package com.raulomana.movies.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raulomana.movies.BuildConfig;
import com.raulomana.movies.model.Movie;
import com.raulomana.movies.model.Review;
import com.raulomana.movies.model.Video;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieAPIUtils {

    @Nullable
    public static Movie getMovie(int movieId) {
        URL moviesRequestUrl = NetworkUtils.buildMovieUrl(BuildConfig.tmdb_api_key, movieId);
        if(moviesRequestUrl != null) {
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                if(response != null) {
                    return MoviesAPIJsonUtils.getMovieFromJson(response);
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NonNull
    public static List<Review> getReviews(int movieId) {
        URL moviesRequestUrl = NetworkUtils.buildReviewsUrl(BuildConfig.tmdb_api_key, movieId);
        if(moviesRequestUrl != null) {
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                return MoviesAPIJsonUtils.parseReviews(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(0);
    }

    @NonNull
    public static List<Video> getVideos(int movieId) {
        URL moviesRequestUrl = NetworkUtils.buildVideosUrl(BuildConfig.tmdb_api_key, movieId);
        if(moviesRequestUrl != null) {
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                return MoviesAPIJsonUtils.parseVideos(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(0);
    }

}
