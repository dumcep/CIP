package com.example.curseinpanta;


public final class GameConfig {
    private GameConfig() {}

    // ------------------- Physics (base) -------------------
    public static final float BASE_ACCELERATION  = 200f; // px/s²
    public static final float BASE_BRAKE_FORCE   = 600f; // px/s²
    public static final float BASE_MAX_SPEED     = 900f; // px/s
    public static final float GRAVITY            = 9.8f * 20;
    public static final float LINEAR_DAMPING     = 50f;

    // ------------------- Coins & upgrades -----------------
    public static final int   COIN_VALUE                 = 1;   // each pickup worth
    public static final int[] PRICE_ACCEL_LEVELS         = {20, 40, 80};  // 3 tiers
    public static final int[] PRICE_BRAKE_LEVELS         = {15, 30, 60};
    public static final int[] PRICE_SPEED_LEVELS         = {25, 50, 100};
    public static final int   PRICE_BACKGROUND_UNLOCK    = 120;

    public static final float[] MULTIPLIER_ACCEL_LEVELS  = {1.2f, 1.4f, 1.7f};
    public static final float[] MULTIPLIER_BRAKE_LEVELS  = {1.1f, 1.3f, 1.5f};
    public static final float[] MULTIPLIER_SPEED_LEVELS  = {1.1f, 1.25f,1.5f};
}
