package com.raulomana.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.raulomana.movies.model.Movie;
import com.raulomana.movies.utils.DateUtils;
import com.raulomana.movies.utils.MoviesAPIJsonUtils;
import com.raulomana.movies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private static final int VA_INDEX_LOADING_STATE = 0;
    private static final int VA_INDEX_CONTENT_STATE = 1;
    private static final int VA_INDEX_ERROR_STATE = 2;

    private ViewAnimator viewAnimator;
    private TextView title;
    private TextView description;
    private TextView releaseDate;
    private TextView duration;
    private TextView rating;
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        viewAnimator = findViewById(R.id.detail_view_animator);
        title = findViewById(R.id.detail_title);
        image = findViewById(R.id.detail_image);
        description = findViewById(R.id.detail_description);
        releaseDate = findViewById(R.id.detail_release_date);
        duration = findViewById(R.id.detail_duration);
        rating = findViewById(R.id.detail_rating);

        Intent intent = getIntent();
        if(intent != null) {
            Movie movie = intent.getParcelableExtra(MainActivity.EXTRA_MOVIE);
            if(movie != null) {
                loadMovie(movie);
            } else {
                viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
            }
        }
    }

    private void loadMovie(@NonNull Movie movie) {
        new FetchMovieTask().execute(movie.getId());
    }

    private void bindMovie(@NonNull Movie movie) {
        title.setText(movie.getTitle());
        description.setText(movie.getDescription());
        SimpleDateFormat releaseDateInputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat releaseDateOutputFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        String userFriendlyReleaseDate = DateUtils.parseThenFormat(movie.getReleaseDate(), releaseDateInputFormat, releaseDateOutputFormat);
        releaseDate.setText(userFriendlyReleaseDate);
        rating.setText(getString(R.string.detail_rating, movie.getRating()));
        if(movie.getRuntime() != null) {
            duration.setText(getString(R.string.detail_duration, movie.getRuntime()));
        }

        Picasso.get()
                .load(movie.getImage())
                .placeholder(R.drawable.place_holder)
                .into(image);

        if(BuildConfig.DEBUG) {
            Toast.makeText(this, "trailers: " + movie.getVideos().size(), Toast.LENGTH_SHORT).show();
        }
    }

    public class FetchMovieTask extends AsyncTask<Integer, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewAnimator.setDisplayedChild(VA_INDEX_LOADING_STATE);
        }

        @Override
        protected Movie doInBackground(Integer... params) {

            /* If there's no movie id, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            Integer movieId = params[0];
            if(movieId == null) {
                return null;
            }

            URL moviesRequestUrl = NetworkUtils.buildMovieUrl(BuildConfig.tmdb_api_key, movieId);

            if(moviesRequestUrl == null) {
                return null;
            }

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                if(response == null) {
                    return null;
                }
                return MoviesAPIJsonUtils.getMovieFromJson(response);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null) {
                bindMovie(movie);
                viewAnimator.setDisplayedChild(VA_INDEX_CONTENT_STATE);
            } else {
                viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
            }
        }
    }


}
