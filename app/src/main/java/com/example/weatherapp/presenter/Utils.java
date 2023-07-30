package com.example.weatherapp.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utils {
    public static String convertDateFormat(String inputDate) {
        LocalDate date = LocalDate.parse(inputDate);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);

        return date.format(outputFormatter);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
