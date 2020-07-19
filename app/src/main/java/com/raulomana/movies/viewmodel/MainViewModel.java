package com.raulomana.movies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.raulomana.movies.db.AppDataBase;
import com.raulomana.movies.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> favoritesLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        favoritesLiveData = AppDataBase.getInstance(application).movieDao().getAllFavorites();
    }

    public LiveData<List<Movie>> getFavoritesLiveData() {
        return favoritesLiveData;
    }
}
