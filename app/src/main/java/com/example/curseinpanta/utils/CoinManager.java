package com.example.curseinpanta.utils;

import android.content.Context;

import com.example.curseinpanta.data.DbHolder;
import com.example.curseinpanta.data.PlayerDao;
import com.example.curseinpanta.data.PlayerStats;

/** Thin wrapper around Room so the rest of the game never touches SQL directly. */
public final class CoinManager {

    private CoinManager() {}                         // util class; no instances

    private static PlayerDao dao(Context ctx) {
        return DbHolder.get(ctx).playerDao();
    }

    /** Current permanent coin balance. */
    public static int getCoins(Context ctx) {
        PlayerStats s = dao(ctx).getPlayer();
        return (s == null) ? 0 : s.coins;
    }
    public static void addCoins(Context ctx, int delta) {
        PlayerDao d = dao(ctx);
        PlayerStats s = d.getPlayer();
        if (s == null) {                      // first call on fresh install
            s = new PlayerStats();            // id = 1 row
        }
        s.coins = Math.max(0, s.coins + delta);
        d.insert(s);                          // REPLACE because id is primary key
    }
}
