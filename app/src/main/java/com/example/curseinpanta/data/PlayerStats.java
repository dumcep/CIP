package com.example.curseinpanta.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "player_stats")
public class PlayerStats {

    @PrimaryKey
    public int id = 1;

    public int coins;
    public int highScore;
}
