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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.raulomana.movies.db.AppDataBase;
import com.raulomana.movies.model.Movie;
import com.raulomana.movies.model.Review;
import com.raulomana.movies.model.Video;
import com.raulomana.movies.utils.AppExecutors;
import com.raulomana.movies.utils.MovieAPIUtils;
import com.raulomana.movies.viewmodel.AddFavoriteViewModel;
import com.raulomana.movies.viewmodel.AddFavoriteViewModelFactory;

import java.util.List;

public class DetailActivity extends AppCompatActivity implements MovieDetailAdapter.OnDetailClickListener {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int VA_INDEX_CONTENT_STATE = 0;
    private static final int VA_INDEX_LOADING_STATE = 1;
    private static final int VA_INDEX_ERROR_STATE = 2;

    private ViewAnimator viewAnimator;
    private RecyclerView itemsList;
    private TextView title;

    @Nullable
    private MovieDetailAdapter adapter;

    @NonNull
    private AppDataBase dataBase;
    @Nullable
    private Movie movie;

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
                this.movie = movie;
                loadMovie(movie);
            } else {
                viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.menu_movie_detail_share == item.getItemId()) {
            if(movie != null && adapter != null) {
                List<Video> videos = adapter.getVideos();
                if(videos != null && !videos.isEmpty()) {
                    Video video = videos.get(0);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, movie.getTitle() + "\n" + video.getUrl());
                    intent.setType("text/plain");
                    if(intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    item.setVisible(false);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

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
        final int movieId = movie.getMovieId();
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movie pulledMovie = MovieAPIUtils.getMovie(movieId);
                final List<Review> reviews = MovieAPIUtils.getReviews(movieId);
                final List<Video> videos = MovieAPIUtils.getVideos(movieId);

                if(pulledMovie != null) {
                    DetailActivity.this.movie = pulledMovie;
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            setupViewModel(pulledMovie, reviews, videos);
                        }
                    });
                } else {
                    showResultUIThread(null, null, null);
                }
            }
        });
    }

    private void showResultUIThread(@Nullable final Movie movie, @Nullable final List<Review> reviews, @Nullable final List<Video> videos) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if(movie == null) {
                    viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
                } else {
                    viewAnimator.setDisplayedChild(VA_INDEX_CONTENT_STATE);
                    bindMovie(movie, reviews, videos);
                }
            }
        });
    }

    private void bindMovie(@NonNull final Movie movie, @Nullable List<Review> reviews, @Nullable List<Video> videos) {
        title.setText(movie.getTitle());
        itemsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieDetailAdapter(movie, reviews, videos, this);
        itemsList.setAdapter(adapter);
    }

    private void setupViewModel(@NonNull final Movie pulledMovie, @Nullable final List<Review> reviews, @Nullable final List<Video> videos) {
        AddFavoriteViewModelFactory factory = new AddFavoriteViewModelFactory(dataBase, pulledMovie.getMovieId());
        final AddFavoriteViewModel viewModel = ViewModelProviders.of(this, factory).get(AddFavoriteViewModel.class);
        viewModel.getMovieLiveData().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                viewModel.getMovieLiveData().removeObserver(this);
                Log.d(TAG, "setupViewModel: Receiving data base update from movie = [" + (movie == null ? "null" : movie.getTitle()) + "]");
                if(movie != null) {
                    pulledMovie.setFavorite(movie.isFavorite());
                }
                showResultUIThread(pulledMovie, reviews, videos);
            }
        });
    }

    private void storeFavoriteMovie(@NonNull final Movie movieToStore) {
        AddFavoriteViewModelFactory factory = new AddFavoriteViewModelFactory(dataBase, movieToStore.getMovieId());
        final AddFavoriteViewModel favoriteViewModel = ViewModelProviders.of(this, factory).get(AddFavoriteViewModel.class);
        favoriteViewModel.getMovieLiveData().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable final Movie movie) {
                favoriteViewModel.getMovieLiveData().removeObserver(this);
                Log.d(TAG, "storeFavoriteMovie: Receiving data base update from movie = [" + (movie == null ? "null" : movie.getTitle()) + "]");
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
        favoriteViewModel.getMovieLiveData().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                Log.d(TAG, "storeFavoriteMovie(2): Receiving data base update from movie = [" + (movie == null ? "null" : movie.getTitle()) + "]");
                if(movie != null) {
                    DetailActivity.this.movie = movie;
                }
            }
        });
    }

}
