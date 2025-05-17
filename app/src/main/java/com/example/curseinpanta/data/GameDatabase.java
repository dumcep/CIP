package com.example.curseinpanta.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { PlayerStats.class }, version = 1)
public abstract class GameDatabase extends RoomDatabase {

    public abstract PlayerDao playerDao();

    // singleton helper
    private static volatile GameDatabase INSTANCE;

    public static GameDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (GameDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    GameDatabase.class,
                                    "game_db"
                            ).allowMainThreadQueries()   // fine for single small table
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
