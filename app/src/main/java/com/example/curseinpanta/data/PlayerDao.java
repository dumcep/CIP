package com.example.curseinpanta.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PlayerDao {

    @Query("SELECT * FROM player_stats WHERE id = 1")
    PlayerStats getPlayer();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlayerStats stats);

    @Update
    void update(PlayerStats stats);
}
