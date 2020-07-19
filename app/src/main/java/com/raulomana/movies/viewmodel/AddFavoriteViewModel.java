package com.raulomana.movies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.raulomana.movies.db.AppDataBase;
import com.raulomana.movies.model.Movie;

public class AddFavoriteViewModel extends ViewModel {
    @NonNull
    private LiveData<Movie> movieLiveData;

    public AddFavoriteViewModel(@NonNull AppDataBase dataBase, @NonNull Movie movie) {
        movieLiveData = dataBase.movieDao().getMovie(movie.getId());
    }

    @NonNull
    public LiveData<Movie> getMovieLiveData() {
        return movieLiveData;
    }
}
