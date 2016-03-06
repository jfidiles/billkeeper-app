package com.work.jfidiles.BillKeeper;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreferences {
    private static final String PREFS_NAME = "AuthorisationPreferences";
    public static void set(String name, String value, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static String get(String name, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String value = settings.getString(name, "");
        return value;
    }
}
