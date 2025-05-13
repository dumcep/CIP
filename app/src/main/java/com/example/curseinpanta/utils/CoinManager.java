package com.example.curseinpanta.utils;

import android.content.Context;

public class CoinManager {
    private static final String PREFS_NAME = "game_prefs";
    private static final String KEY_COINS  = "coins";

    public static int getCoins(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_COINS, 0);
    }

    public static void setCoins(Context ctx, int amount) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_COINS, amount)
                .apply();
    }

    public static void addCoins(Context ctx, int delta) {
        setCoins(ctx, getCoins(ctx) + delta);
    }
}
