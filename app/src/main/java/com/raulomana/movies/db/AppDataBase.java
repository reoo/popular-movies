package com.raulomana.movies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.raulomana.movies.model.Movie;

@Database(entities = { Movie.class }, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    @NonNull
    private static AppDataBase instance;

    @NonNull
    public static AppDataBase getInstance(@NonNull Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context, AppDataBase.class, "moviesdb").build();
        }
        return instance;
    }

    public abstract MovieDao movieDao();

}
