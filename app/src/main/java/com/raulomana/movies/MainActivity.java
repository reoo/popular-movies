package com.raulomana.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewAnimator = findViewById(R.id.movies_view_animator);
        moviesList = findViewById(R.id.movies_list_items);

        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies, menu);
        orderByRatingMenuItem = menu.findItem(R.id.menu_movies_sort_by_rating);
        orderByPopularityMenuItem = menu.findItem(R.id.menu_movies_sort_by_popularity);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(R.id.menu_movies_sort_by_popularity == itemId) {
            if(orderByPopularityMenuItem != null && orderByRatingMenuItem != null) {
                new FetchMoviesTask().execute(NetworkUtils.POPULAR_TYPE);
                orderByPopularityMenuItem.setChecked(true);
                orderByRatingMenuItem.setChecked(false);
//                sortBy(POPULARITY);
            }
            return true;
        } else if(R.id.menu_movies_sort_by_rating == itemId) {
            if(orderByPopularityMenuItem != null && orderByRatingMenuItem != null) {
                new FetchMoviesTask().execute(NetworkUtils.TOP_RATED_TYPE);
                orderByPopularityMenuItem.setChecked(false);
                orderByRatingMenuItem.setChecked(true);
//                sortBy(RATING);
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

    private void loadMovies() {
        new FetchMoviesTask().execute(NetworkUtils.POPULAR_TYPE);
    }

    private void bindMovies(@NonNull final List<Movie> movies) {
        this.movies = movies;
        MoviesAdapter adapter = new MoviesAdapter(movies, this);
        moviesList.setAdapter(adapter);
        moviesList.setLayoutManager(new GridLayoutManager(MainActivity.this, COLUMNS));
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDataBase.getInstance(MainActivity.this).movieDao().save(movies);
            }
        });
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

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewAnimator.setDisplayedChild(VA_INDEX_LOADING_STATE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            /* If there's no movies type, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String type = params[0];
            URL moviesRequestUrl = NetworkUtils.buildMoviesListUrl(BuildConfig.tmdb_api_key, type, 1);

            if(moviesRequestUrl == null) {
                return null;
            }

            try {
                String response = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                if(response == null) {
                    return null;
                }
                return MoviesAPIJsonUtils.getMoviesFromJson(response);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                bindMovies(movies);
                viewAnimator.setDisplayedChild(VA_INDEX_CONTENT_STATE);
            } else {
                viewAnimator.setDisplayedChild(VA_INDEX_ERROR_STATE);
            }
        }
    }

}
