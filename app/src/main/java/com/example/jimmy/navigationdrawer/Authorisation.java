package com.example.jimmy.navigationdrawer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by Jimmy on 1/11/2016.
 */
public class Authorisation {
    private static final String PREFS_NAME = "AuthorisationPreferences";
    public static void storePreference(String name, String token, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, token);
        editor.commit();
    }

    public static String getPreference(String name, Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        String value = settings.getString(name, "");
        return value;
    }

    public static void UpdateToken (Context context) {
        Utilities.setAPIContext(context);
        new APIConnect.UpdateTokenTask().execute();
    }

}
