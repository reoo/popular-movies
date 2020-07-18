/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.raulomana.movies.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_URL = "https://api.themoviedb.org";

    public static final String MOVIES_API_BASE_URL = MOVIES_URL;
    public static final String MOVIES_IMAGES_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public final static String POPULAR_TYPE = "popular";
    public final static String TOP_RATED_TYPE = "top_rated";

    private final static String API_KEY_PARAM = "api_key";
    public static final String VIDEOS_PARAM = "append_to_response";
    private final static String PAGE_PARAM = "page";

    @Nullable
    public static URL buildMoviesListUrl(@NonNull String apiKey, @NonNull String type, int page) {
        Uri builtUri = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .path("/3/movie/" + type)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    @Nullable
    public static URL buildMovieUrl(@NonNull String apiKey, int movieId) {
        Uri builtUri = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .path("/3/movie/" + movieId)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(VIDEOS_PARAM, "videos,reviews")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @Nullable
    public static String getResponseFromHttpUrl(@NonNull URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}