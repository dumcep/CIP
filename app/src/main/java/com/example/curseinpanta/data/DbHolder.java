package com.example.curseinpanta.data;

import android.content.Context;
import androidx.room.Room;

public final class DbHolder {
    private DbHolder() {}                // util class

    private static volatile GameDatabase INSTANCE;

    public static GameDatabase get(Context ctx) {
        if (INSTANCE == null) {
            synchronized (DbHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(           // ← HERE
                                    ctx.getApplicationContext(),
                                    GameDatabase.class,
                                    "game_db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
