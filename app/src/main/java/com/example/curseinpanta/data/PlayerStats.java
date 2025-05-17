package com.example.curseinpanta.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlayerStats {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int coins;
    public int highScore;

    // Optional: constructor
    public PlayerStats() {
        this.coins = 0;
        this.highScore = 0;
    }
}
