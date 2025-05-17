package com.example.curseinpanta.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.curseinpanta.GameConfig;

public final class UpgradeManager {
    private static final String PREF   = "upgrades";
    private static final String KEY_A  = "accelLvl";
    private static final String KEY_B  = "brakeLvl";
    private static final String KEY_S  = "speedLvl";
    private static final String KEY_BG = "bgUnlocked";

    // ---------- getters ----------
    public static int getAccelLvl(Context c) { return get(c).getInt(KEY_A, 0); }
    public static int getBrakeLvl(Context c) { return get(c).getInt(KEY_B, 0); }
    public static int getSpeedLvl(Context c) { return get(c).getInt(KEY_S, 0); }
    public static boolean isBgUnlocked(Context c){ return get(c).getBoolean(KEY_BG,false); }

    // ---------- multipliers ----------
    public static float accelMult(Context c){
        return levelToMult(getAccelLvl(c), GameConfig.MULTIPLIER_ACCEL_LEVELS);
    }
    public static float brakeMult(Context c){
        return levelToMult(getBrakeLvl(c), GameConfig.MULTIPLIER_BRAKE_LEVELS);
    }
    public static float speedMult(Context c){
        return levelToMult(getSpeedLvl(c), GameConfig.MULTIPLIER_SPEED_LEVELS);
    }

    // ---------- upgrading ----------
    public static boolean buyAccel(Context c){
        return tryBuyLevel(c, KEY_A,
                GameConfig.PRICE_ACCEL_LEVELS,
                GameConfig.MULTIPLIER_ACCEL_LEVELS.length);
    }
    public static boolean buyBrake(Context c){
        return tryBuyLevel(c, KEY_B,
                GameConfig.PRICE_BRAKE_LEVELS,
                GameConfig.MULTIPLIER_BRAKE_LEVELS.length);
    }
    public static boolean buySpeed(Context c){
        return tryBuyLevel(c, KEY_S,
                GameConfig.PRICE_SPEED_LEVELS,
                GameConfig.MULTIPLIER_SPEED_LEVELS.length);
    }
    public static boolean buyBackground(Context c){
        if (isBgUnlocked(c)) return false;
        if (CoinManager.getCoins(c) < GameConfig.PRICE_BACKGROUND_UNLOCK) return false;
        CoinManager.addCoins(c, -GameConfig.PRICE_BACKGROUND_UNLOCK);
        get(c).edit().putBoolean(KEY_BG,true).apply();
        return true;
    }

    // ---------- helpers ----------
    private static SharedPreferences get(Context c){
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
    private static float levelToMult(int lvl, float[] table){
        return lvl==0 ? 1f : table[lvl-1];
    }
    private static boolean tryBuyLevel(Context c, String key,
                                       int[] priceTable, int maxLevels) {

        int lvl = get(c).getInt(key, 0);
        if (lvl >= maxLevels) return false;

        int price = priceTable[lvl];
        if (CoinManager.getCoins(c) < price) return false;

        CoinManager.addCoins(c, -price);        // ← use c, not ctx
        get(c).edit().putInt(key, lvl + 1).apply();
        return true;
    }
}
