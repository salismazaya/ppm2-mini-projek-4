package com.example.unserakus;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unserakus.api.models.User; // Pastikan import model User
import com.google.gson.Gson;

public class SharedPreferencesHelper {

    private static final String PREF_NAME = "UnserakusPrefs";
    private static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    private static final String KEY_USER = "LOGGED_IN_USER";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- Token ---
    public static void saveToken(Context context, String token) {
        getPrefs(context).edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        return getPrefs(context).getString(KEY_AUTH_TOKEN, null);
    }

    // --- User ---
    public static void saveUser(Context context, User user) {
        String userJson = new Gson().toJson(user);
        getPrefs(context).edit().putString(KEY_USER, userJson).apply();
    }

    public static User getUser(Context context) {
        String userJson = getPrefs(context).getString(KEY_USER, null);
        Log.d("PREFE", userJson);
        if (userJson == null) {
            return null;
        }
        return new Gson().fromJson(userJson, User.class);
    }

    public static int getLoggedInUserId(Context context) {
        User user = getUser(context);
        return (user != null) ? user.getId() : -1;
    }

    // --- Clear (Logout) ---
    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}