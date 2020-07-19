package com.raulomana.movies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raulomana.movies.model.Movie;

@Database(entities = { Movie.class }, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    @Nullable
    private static AppDataBase instance;

    private static final Object LOCK = new Object();

    @NonNull
    public static AppDataBase getInstance(@NonNull Context context) {
        if(instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context, AppDataBase.class, "moviesdb").build();
            }
        }
        return instance;
    }

    public abstract MovieDao movieDao();

}
