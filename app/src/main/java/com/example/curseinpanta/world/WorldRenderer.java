package com.example.curseinpanta.world;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import com.example.curseinpanta.entities.Car;
import com.example.curseinpanta.graphics.BackgroundPainter;
import com.example.curseinpanta.graphics.Camera;
import com.example.curseinpanta.graphics.TerrainPainter;
import com.example.curseinpanta.utils.SettingsManager;
import com.example.curseinpanta.graphics.CoinPainter;
import com.example.curseinpanta.entities.Coin;
import com.example.curseinpanta.world.CoinField;

public class WorldRenderer {

    private final CoinField   coinField;
    private final CoinPainter coinPainter;
    private final Context ctx;   // need for CoinManager
    private final BackgroundPainter bgPainter;
    private final TerrainPainter    terrainPainter;
    private final Camera            camera;
    private final Car               car;
    private final Paint             namePaint;
    private final int screenHeight;


    public WorldRenderer(Context ctx, Car car, int  screenH, Paint namePaint) {

        this.ctx          = ctx;           // store for CoinManager
        this.car          = car;
        this.screenHeight = screenH;
        this.namePaint    = namePaint;

        this.camera          = new Camera();
        this.terrainPainter  = new TerrainPainter();
        this.bgPainter       = new BackgroundPainter(screenH);
        this.coinPainter     = new CoinPainter();
        this.coinField       = new CoinField(terrainPainter, ctx);
    }
    public void update(int screenW) {
        camera.update(car.getX(), screenW);

        // --- NEW: set car Y to terrain height each frame ---
        float groundY = terrainPainter.getGroundYAt(car.getX(), screenHeight);
        car.setY(groundY - car.getHeight());

        // COINS: spawn ahead & collision
        coinField.spawnIfNeeded(camera.getX() + screenW, screenHeight);
        coinField.checkCollect(car.getX(), car.getY(), car.getWidth(), car.getHeight());
    }

    public void draw(Canvas c, String driverName) {
        // sky
        bgPainter.draw(c);

        // terrain
        terrainPainter.draw(c, camera.getX());

        // car
        c.save();
        c.translate(-camera.getX(), 0);
        car.draw(c);
        c.restore();

        // driver label in screen space
        float sx = car.getX() - camera.getX() + car.getWidth()/2f;
        c.drawText(driverName, sx, car.getY() - 16, namePaint);
        coinPainter.draw(c, coinField.getCoins(), camera.getX());
    }

    public void onScreenResize(int h) {
        // keep car on ground when rotation/resizing
        float groundY = terrainPainter.getGroundY(h);
        car.setY(groundY - car.getHeight());
    }
}
