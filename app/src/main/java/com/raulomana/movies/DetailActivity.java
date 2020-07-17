package com.raulomana.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.raulomana.movies.model.Movie;
import com.raulomana.movies.model.Video;
import com.raulomana.movies.utils.DateUtils;
import com.raulomana.movies.utils.MoviesAPIJsonUtils;
import com.raulomana.movies.utils.NetworkUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity implements MovieDetailAdapter.OnDetailClickListener {

    private static final int VA_INDEX_CONTENT_STATE = 0;
    private static final int VA_INDEX_LOADING_STATE = 1;
    private static final int VA_INDEX_ERROR_STATE = 2;

    private ViewAnimator viewAnimator;
    private RecyclerView itemsList;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        viewAnimator = findViewById(R.id.detail_view_animator);
        itemsList = findViewById(R.id.detail_list_items);
        title = findViewById(R.id.detail_title);

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
        itemsList.setLayoutManager(new LinearLayoutManager(this));
        itemsList.setAdapter(new MovieDetailAdapter(movie, this));

        if(BuildConfig.DEBUG) {
            Toast.makeText(this, "trailers: " + movie.getVideos().size(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(@NonNull Movie movie) {
        Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoClick(@NonNull Movie movie, @NonNull Video video) {
        Uri uri = Uri.parse(video.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
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
