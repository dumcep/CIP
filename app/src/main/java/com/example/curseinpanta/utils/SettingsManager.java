package com.example.curseinpanta.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.curseinpanta.R;

public class SettingsManager {
    private static final String PREFS = "game_prefs";
    private static final String KEY_DRIVER_NAME   = "driver_name";
    private static final String KEY_BG_RES_ID     = "background_res_id";

    // default values
    private static final String DEFAULT_DRIVER_NAME = "Player";
    private static final int    DEFAULT_BG_RES_ID   = R.drawable.bg_main_menu;

    public static String getDriverName(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return p.getString(KEY_DRIVER_NAME, DEFAULT_DRIVER_NAME);
    }

    public static void setDriverName(Context ctx, String name) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_DRIVER_NAME, name)
                .apply();
    }

    public static int getBackgroundResId(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return p.getInt(KEY_BG_RES_ID, DEFAULT_BG_RES_ID);
    }

    public static void setBackgroundResId(Context ctx, int resId) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_BG_RES_ID, resId)
                .apply();
    }
}
