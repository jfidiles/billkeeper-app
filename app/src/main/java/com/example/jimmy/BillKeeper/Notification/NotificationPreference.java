package com.example.jimmy.BillKeeper.Notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Jimmy on 1/16/2016.
 */
public class NotificationPreference {
    //Increment title number
    public static void save(String key, String value, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        int nr = getPosition("Tnr", context);
        nr++;
        incrementValues("Tnr", nr, context);
    }

    //Increment notification and title number
    public static void incrementValues(String key, int value, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    //Delete notification number from SharedPref
    public static void delete(String key, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    //Get SharedPref
    public static String get(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "");
    }

    //Set title and notification number when application it`s started for the first time
    public static void setDefault(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        if (!sharedPreferences.contains("Tnr")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("Tnr", 1);
            editor.apply();
        }
        if (!sharedPreferences.contains("Snr")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("Snr", 1);
            editor.apply();
        }
    }
    //Get number of notification or title
    public static int getPosition(String key, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(key, 1);
    }
}
