package com.raulomana.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.raulomana.movies.db.AppDataBase;
import com.raulomana.movies.model.Movie;
import com.raulomana.movies.model.Video;
import com.raulomana.movies.utils.AppExecutors;
import com.raulomana.movies.utils.MoviesAPIJsonUtils;
import com.raulomana.movies.utils.NetworkUtils;
import com.raulomana.movies.viewmodel.AddFavoriteViewModel;
import com.raulomana.movies.viewmodel.AddFavoriteViewModelFactory;

import java.net.URL;

public class DetailActivity extends AppCompatActivity implements MovieDetailAdapter.OnDetailClickListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int VA_INDEX_CONTENT_STATE = 0;
    private static final int VA_INDEX_LOADING_STATE = 1;
    private static final int VA_INDEX_ERROR_STATE = 2;

    private ViewAnimator viewAnimator;
    private RecyclerView itemsList;
    private TextView title;

    private AppDataBase dataBase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        viewAnimator = findViewById(R.id.detail_view_animator);
        itemsList = findViewById(R.id.detail_list_items);
        title = findViewById(R.id.detail_title);

        dataBase = AppDataBase.getInstance(DetailActivity.this);

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

    @Override
    public void onFavoriteClick(@NonNull final Movie movie) {
        movie.setFavorite(!movie.isFavorite());
        storeFavoriteMovie(movie);
    }

    @Override
    public void onVideoClick(@NonNull Movie movie, @NonNull Video video) {
        Uri uri = Uri.parse(video.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void loadMovie(@NonNull final Movie movie) {
        viewAnimator.setDisplayedChild(VA_INDEX_LOADING_STATE);
        final int movieId = movie.getId();
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                URL moviesRequestUrl = NetworkUtils.buildMovieUrl(BuildConfig.tmdb_api_key, movieId);

                if(moviesRequestUrl != null) {
                    try {
                        String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                        if(response != null) {
                            final Movie fromJson = MoviesAPIJsonUtils.getMovieFromJson(response);
                            showResultUIThread(fromJson);
                        } else {
                            showResultUIThread(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showResultUIThread(null);
                    }
                } else {
                    showResultUIThread(null);
                }
            }
        });
    }

    private void showResultUIThread(@Nullable final Movie movie) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if(movie == null) {
                    viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
                } else {
                    viewAnimator.setDisplayedChild(VA_INDEX_CONTENT_STATE);
                    bindMovie(movie);
                }
            }
        });
    }

    private void bindMovie(@NonNull final Movie movie) {
        title.setText(movie.getTitle());
        itemsList.setLayoutManager(new LinearLayoutManager(this));
        itemsList.setAdapter(new MovieDetailAdapter(movie, this));
    }

    private void storeFavoriteMovie(@NonNull final Movie movieToStore) {
        AddFavoriteViewModelFactory factory = new AddFavoriteViewModelFactory(dataBase, movieToStore);
        final AddFavoriteViewModel favoriteViewModel = ViewModelProviders.of(this, factory).get(AddFavoriteViewModel.class);
        favoriteViewModel.getMovieLiveData().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable final Movie movie) {
                favoriteViewModel.getMovieLiveData().removeObserver(this);
                Log.d(TAG, "Receiving data base update from movie = [" + (movie == null ? "null" : movie.getTitle()) + "]");
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(movie == null) {
                            dataBase.movieDao().save(movieToStore);
                        } else {
                            // already cached so just update
                            dataBase.movieDao().update(movieToStore);
                        }
                    }
                });
            }
        });
    }

}
