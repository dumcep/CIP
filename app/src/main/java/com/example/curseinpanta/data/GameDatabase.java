package com.example.curseinpanta.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {PlayerStats.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {

    public abstract PlayerDao playerDao();

    // Singleton instance
    private static volatile GameDatabase INSTANCE;

    public static GameDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (GameDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    GameDatabase.class,
                                    "game_db"
                            )
                            .allowMainThreadQueries() // OK for small apps, not recommended for production
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
