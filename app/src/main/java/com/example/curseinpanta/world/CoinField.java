package com.example.curseinpanta.world;

import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import com.example.curseinpanta.entities.Coin;
import com.example.curseinpanta.utils.CoinManager;
import android.content.Context;
import com.example.curseinpanta.graphics.TerrainPainter;

public class CoinField {
    private final ArrayList<Coin> coins = new ArrayList<>();
    private final Random rnd = new Random();
    private final TerrainPainter terrain;
    private final Context ctx;

    private static final float SPAWN_INTERVAL = 500f;   // px world units
    private float nextSpawnX = 400f;

    public CoinField(TerrainPainter terrain, Context ctx) {
        this.terrain = terrain;
        this.ctx     = ctx;
    }

    /** Ensure coins exist a bit ahead of camera */
    public void spawnIfNeeded(float camRight, int screenH) {
        while (nextSpawnX < camRight + 1200f) {               // spawn 1200px ahead
            float x = nextSpawnX;
            nextSpawnX += SPAWN_INTERVAL + rnd.nextInt(200);  // jitter spacing

            float ground = terrain.getGroundYAt(x, screenH);
            coins.add(new Coin(x, ground - 60));              // float a bit above ground
        }
    }

    /** Called each frame – remove & count collected */
    public void checkCollect(float carX, float carY, float carW, float carH) {
        RectF carRect = new RectF(carX, carY, carX+carW, carY+carH);

        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin c = it.next();
            if (c.collected) { it.remove(); continue; }

            float dx = Math.abs((c.x + Coin.R) - (carX + carW/2f));
            float dy = Math.abs((c.y + Coin.R) - (carY + carH/2f));
            float distSq = dx*dx + dy*dy;
            if (distSq < (Coin.R + Math.min(carW,carH)/2f) *
                    (Coin.R + Math.min(carW,carH)/2f)) {
                // collect
                c.collected = true;
                CoinManager.addCoins(ctx, 1);
                it.remove();
            }
        }
    }

    public ArrayList<Coin> getCoins() { return coins; }
}
