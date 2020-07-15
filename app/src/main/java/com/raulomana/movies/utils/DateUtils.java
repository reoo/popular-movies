package com.raulomana.movies.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    @NonNull
    public static String parseThenFormat(@Nullable String date,
                                         @Nullable SimpleDateFormat inputFormat,
                                         @Nullable SimpleDateFormat outputFormat) {
        Date convertedDate;
        String readable = "";

        if(date == null || inputFormat == null || outputFormat == null){
            return readable;
        }

        try {
            convertedDate = inputFormat.parse(date);
            readable = outputFormat.format(convertedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return readable;
    }
}
