package com.example.curseinpanta.data;
import com.example.curseinpanta.data.PlayerStats;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlayerStats stats);

    @Update
    void update(PlayerStats stats);

    @Query("SELECT * FROM PlayerStats LIMIT 1")
    PlayerStats getPlayer(); // Returns null if not exists
}

