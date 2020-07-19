package com.raulomana.movies;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewAnimator;

import com.raulomana.movies.db.AppDataBase;
import com.raulomana.movies.model.Movie;
import com.raulomana.movies.utils.AppExecutors;
import com.raulomana.movies.utils.MoviesAPIJsonUtils;
import com.raulomana.movies.utils.NetworkUtils;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnMovieClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int COLUMNS = 2;

    private static final int VA_INDEX_LOADING_STATE = 0;
    private static final int VA_INDEX_CONTENT_STATE = 1;
    private static final int VA_INDEX_ERROR_STATE = 2;

    public static final int POPULARITY = 0;
    public static final int RATING = 1;

    public static final String EXTRA_MOVIE = "movie";
    public static final String EXTRA_ID = "id";

    private ViewAnimator viewAnimator;
    private RecyclerView moviesList;
    @Nullable
    private MenuItem orderByRatingMenuItem;
    @Nullable
    private MenuItem orderByPopularityMenuItem;
    @Nullable
    private MenuItem viewFavoritesMenuItem;

    @Nullable
    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewAnimator = findViewById(R.id.movies_view_animator);
        moviesList = findViewById(R.id.movies_list_items);

        pullMovies(NetworkUtils.POPULAR_TYPE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies, menu);
        orderByRatingMenuItem = menu.findItem(R.id.menu_movies_sort_by_rating);
        orderByPopularityMenuItem = menu.findItem(R.id.menu_movies_sort_by_popularity);
        viewFavoritesMenuItem = menu.findItem(R.id.menu_movies_view_favorites);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        boolean isPopularityClicked = R.id.menu_movies_sort_by_popularity == itemId;
        boolean isRatingClicked = R.id.menu_movies_sort_by_rating == itemId;
        boolean isFavoritesClicked = R.id.menu_movies_view_favorites == itemId;
        if(isPopularityClicked || isRatingClicked || isFavoritesClicked) {
            if(orderByPopularityMenuItem != null && orderByRatingMenuItem != null && viewFavoritesMenuItem != null) {
                String type = isPopularityClicked ? NetworkUtils.POPULAR_TYPE
                        : isRatingClicked ? NetworkUtils.TOP_RATED_TYPE
                        : NetworkUtils.CACHE_TYPE;
                pullMovies(type);
                orderByPopularityMenuItem.setChecked(isPopularityClicked);
                orderByRatingMenuItem.setChecked(isRatingClicked);
                viewFavoritesMenuItem.setChecked(isFavoritesClicked);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(@NonNull Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    private void bindMovies(@NonNull final List<Movie> movies) {
        this.movies = movies;
        MoviesAdapter adapter = new MoviesAdapter(movies, this);
        moviesList.setAdapter(adapter);
        moviesList.setLayoutManager(new GridLayoutManager(MainActivity.this, COLUMNS));
    }

    private void sortBy(int sortBy) {
        if(movies != null) {
            if(POPULARITY == sortBy) {
                Collections.sort(movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie o1, Movie o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });
                if(moviesList.getAdapter() != null) {
                    moviesList.getAdapter().notifyDataSetChanged();
                }
            } else if(RATING == sortBy) {
                Collections.sort(movies, new Comparator<Movie>() {
                    @Override
                    public int compare(Movie o1, Movie o2) {
                        return Double.compare(o2.getRating(), o1.getRating());
                    }
                });
                if(moviesList.getAdapter() != null) {
                    moviesList.getAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    private void pullMovies(@NonNull final String type) {
        viewAnimator.setDisplayedChild(VA_INDEX_LOADING_STATE);
        if(NetworkUtils.POPULAR_TYPE.equals(type) || NetworkUtils.TOP_RATED_TYPE.equals(type)) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    URL moviesRequestUrl = NetworkUtils.buildMoviesListUrl(BuildConfig.tmdb_api_key, type, 1);

                    if(moviesRequestUrl != null) {
                        try {
                            String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                            if(response != null) {
                                List<Movie> movies = MoviesAPIJsonUtils.getMoviesFromJson(response);
                                showMovies(movies);
//                                storeMovies(movies);
                            } else {
                                showMovies(null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showMovies(null);
                        }
                    } else {
                        showMovies(null);
                    }
                }
            });
        } else {
            AppDataBase.getInstance(getApplication()).movieDao().getAllFavorites().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    AppDataBase.getInstance(getApplication()).movieDao().getAllFavorites().removeObserver(this);
                    Log.d(TAG, "onChanged() called with: movies = [" + ( movies == null ? "null" : movies.size() ) + "]");
                    showMovies(movies);
                }
            });
        }
    }

    private void showMovies(@Nullable final List<Movie> movies) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (movies != null) {
                    bindMovies(movies);
                    viewAnimator.setDisplayedChild(VA_INDEX_CONTENT_STATE);
                } else {
                    viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
                }
            }
        });
    }

    private void storeMovies(@NonNull final List<Movie> movies) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDataBase.getInstance(MainActivity.this).movieDao().save(movies);
            }
        });
    }

}
