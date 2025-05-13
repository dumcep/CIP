package com.example.curseinpanta.world;

import android.content.Context;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.example.curseinpanta.entities.Coin;
import com.example.curseinpanta.graphics.TerrainPainter;
import com.example.curseinpanta.GameConfig;     // ← fixed import
import com.example.curseinpanta.utils.CoinManager;

public class CoinField {
    private final ArrayList<Coin> coins = new ArrayList<>();
    private final Random rnd = new Random();
    private final TerrainPainter terrain;
    private final Context ctx;

    private static final float SPAWN_INTERVAL = 500f;   // px world
    private float nextSpawnX = 400f;

    public CoinField(TerrainPainter terrain, Context ctx) {
        this.terrain  = terrain;
        this.ctx      = ctx;
    }

    /** Keep coins a bit ahead of the camera */
    public void spawnIfNeeded(float camRight, int screenH) {
        while (nextSpawnX < camRight + 1200f) {
            float x = nextSpawnX;
            nextSpawnX += SPAWN_INTERVAL + rnd.nextInt(200);

            float ground = terrain.getGroundYAt(x, screenH);
            coins.add(new Coin(x, ground - 60)); // hover above ground
        }
    }

    /** Collision + collection */
    public void checkCollect(float carX, float carY, float carW, float carH) {
        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin c = it.next();
            // distance centre-to-centre
            float dx = (c.x + Coin.R) - (carX + carW / 2f);
            float dy = (c.y + Coin.R) - (carY + carH / 2f);
            float distSq = dx * dx + dy * dy;
            float collisionDist = Coin.R + Math.min(carW, carH) / 2f;

            if (distSq < collisionDist * collisionDist) {
                it.remove();                     // remove from field
                CoinManager.addCoins(ctx, GameConfig.COIN_VALUE);  // ← use config
            }
        }
    }

    public ArrayList<Coin> getCoins() { return coins; }
}
