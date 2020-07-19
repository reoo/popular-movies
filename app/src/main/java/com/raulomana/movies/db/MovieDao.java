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

    @Query("SELECT * FROM movie WHERE movieId = :movieId")
    LiveData<Movie> getMovie(int movieId);

    @Insert
    void save(@NonNull Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(@NonNull Movie movie);

    @Query("SELECT * FROM movie WHERE favorite ORDER BY popularity DESC")
    LiveData<List<Movie>> getAllFavorites();


}
