package com.example.unserakus.storages;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;

public class Prefences {
    protected Activity activity;
    public Prefences(Activity activity) {
        this.activity = activity;
    }

    protected SharedPreferences getSharedPreferences() {
        return this.activity.getSharedPreferences("UserPreference", MODE_PRIVATE);
    }

    public String getToken() {
        return getSharedPreferences().getString("TOKEN", null);
    }

    @SuppressLint("CommitPrefEdits")
    public void setToken(String token) {
      SharedPreferences.Editor editor = getSharedPreferences().edit();
      editor.putString("TOKEN", token);
      editor.apply();
    }

}
