package com.raulomana.movies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.raulomana.movies.db.AppDataBase;

public class AddFavoriteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private AppDataBase dataBase;
    private int movieId;

    public AddFavoriteViewModelFactory(@NonNull AppDataBase dataBase, int movieId) {
        this.dataBase = dataBase;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddFavoriteViewModel(dataBase, movieId);
    }
}
