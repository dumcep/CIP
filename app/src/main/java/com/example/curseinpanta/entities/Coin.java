package com.example.curseinpanta.entities;

import com.example.curseinpanta.GameConfig;
import com.example.curseinpanta.utils.CoinManager;

public class Coin {
    public float x, y;   // world space
    public static final float R = 18f;

    public boolean collected = false;

    public Coin(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
