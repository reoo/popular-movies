package com.raulomana.movies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.raulomana.movies.db.AppDataBase;
import com.raulomana.movies.model.Movie;

public class AddFavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private AppDataBase dataBase;
    @NonNull
    private Movie movie;

    public AddFavoriteViewModelFactory(@NonNull AppDataBase dataBase, @NonNull Movie movie) {
        this.dataBase = dataBase;
        this.movie = movie;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddFavoriteViewModel(dataBase, movie);
    }
}
