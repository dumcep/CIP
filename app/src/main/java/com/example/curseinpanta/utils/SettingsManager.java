package com.example.curseinpanta.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.curseinpanta.R;

public class SettingsManager {
    private static final String KEY_ROUGHNESS = "roughness";    // float 0-1
    private static final String KEY_NIGHT     = "night_mode";   // boolean
    private static final String PREFS = "game_prefs";
    private static final String KEY_DRIVER_NAME   = "driver_name";
    private static final String KEY_BG_RES_ID     = "background_res_id";

    // default values
    private static final String DEFAULT_DRIVER_NAME = "Player";
    private static final int    DEFAULT_BG_RES_ID   = R.drawable.bg_main_menu;

    public static String getDriverName(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, MODE_PRIVATE);
        return p.getString(KEY_DRIVER_NAME, DEFAULT_DRIVER_NAME);
    }

    public static void setDriverName(Context ctx, String name) {
        ctx.getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putString(KEY_DRIVER_NAME, name)
                .apply();
    }

    public static int getBackgroundResId(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, MODE_PRIVATE);
        return p.getInt(KEY_BG_RES_ID, DEFAULT_BG_RES_ID);
    }

    public static void setBackgroundResId(Context ctx, int resId) {
        ctx.getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putInt(KEY_BG_RES_ID, resId)
                .apply();
    }
    public static float getRoughness(Context ctx) {
        return ctx.getSharedPreferences(PREFS, MODE_PRIVATE)
                .getFloat(KEY_ROUGHNESS, 0.5f);               // default medium
    }
    public static void  setRoughness(Context ctx, float v) {
        ctx.getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit().putFloat(KEY_ROUGHNESS, v).apply();
    }

    public static boolean isNight(Context ctx) {
        return ctx.getSharedPreferences(PREFS, MODE_PRIVATE)
                .getBoolean(KEY_NIGHT, false);
    }
    public static void setNight(Context ctx, boolean night) {
        ctx.getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit().putBoolean(KEY_NIGHT, night).apply();
    }
}
