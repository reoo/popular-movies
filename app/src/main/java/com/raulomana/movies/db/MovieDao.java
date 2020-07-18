package com.raulomana.movies.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import com.raulomana.movies.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @NonNull
    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(@NonNull Movie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(@NonNull List<Movie> movie);

    @Update
    int update(@NonNull Movie movie);

}
