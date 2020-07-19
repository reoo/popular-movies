/*
 * Copyright (c) 2020
 * Omniexperience All rights reserved.
 */

package com.raulomana.movies.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    @Nullable
    private static AppExecutors sInstance;
    @NonNull
    private final Executor diskIO;
    @NonNull
    private final Executor mainThread;
    @NonNull
    private final Executor networkIO;

    private AppExecutors(@NonNull Executor diskIO, @NonNull Executor networkIO, @NonNull Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    @NonNull
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    @NonNull
    public Executor diskIO() {
        return diskIO;
    }

    @NonNull
    public Executor mainThread() {
        return mainThread;
    }

    @NonNull
    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
