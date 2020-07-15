package com.raulomana.movies.utils;

import android.support.annotation.NonNull;

import com.raulomana.movies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoviesAPIJsonUtils {

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

//                int id = 0;
//                if(item.has("id")) {
//                    id = item.getInt("id");
//                }
//                String title = "";
//                if(item.has("title")) {
//                    title = item.getString("title");
//                }
//                String description = "";
//                if(item.has("overview")) {
//                    description = item.getString("overview");
//                }
//                String image = null;
//                if(item.has("poster_path")) {
//                    String path = item.getString("poster_path");
//                    if(path != null) {
//                        image = NetworkUtils.MOVIES_IMAGES_BASE_URL + path;
//                    }
//                }
//                double rating = 0.0f;
//                if(item.has("vote_average")) {
//                    rating = item.getDouble("vote_average");
//                }
//                double popularity = 0.0f;
//                if(item.has("popularity")) {
//                    popularity = item.getDouble("popularity");
//                }
//                String releaseDate = "";
//                if(item.has("release_date")) {
//                    releaseDate = item.getString("release_date");
//                }
//                Integer runtime = null;
//                if(item.has("runtime")) {
//                    runtime = item.getInt("runtime");
//                }
//
//                movies.add(new Movie(id, title, description, image, rating, popularity, releaseDate, runtime));
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
        if(jsonObject.has("release_date")) {
            releaseDate = jsonObject.getString("release_date");
        }
        Integer runtime = null;
        if(jsonObject.has("runtime")) {
            runtime = jsonObject.getInt("runtime");
        }

        return new Movie(id, title, description, image, rating, popularity, releaseDate, runtime);
    }


}
