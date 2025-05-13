package com.example.curseinpanta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.curseinpanta.entities.Car;
import com.example.curseinpanta.input.InputController;
import com.example.curseinpanta.physics.PhysicsEngine;
import com.example.curseinpanta.utils.SettingsManager;
import com.example.curseinpanta.graphics.BackgroundPainter;
import com.example.curseinpanta.graphics.TerrainPainter;
import com.example.curseinpanta.world.WorldRenderer;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "GameView";

    // dropped 'final' so we can assign in init()
    private Car             car;
    private InputController input;
    private GameThread      thread;
    private WorldRenderer   renderer;

    public GameView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx);
    }

    public GameView(Context ctx) {
        super(ctx);
        init(ctx);
    }

    private void init(Context ctx) {
        // wire up surface callbacks
        getHolder().addCallback(this);

        // create car & input controller
        Paint carPaint = new Paint();
        carPaint.setColor(Color.RED);
        car   = new Car(100, 500, 100, 50, carPaint);
        input = new InputController(car);

        // build name paint
        Paint namePaint = new Paint();
        namePaint.setColor(Color.BLACK);
        namePaint.setTextAlign(Paint.Align.CENTER);
        namePaint.setTextSize(32f);

        // new renderer class handles all drawing
        int screenH = getResources().getDisplayMetrics().heightPixels;
        renderer = new WorldRenderer(car, screenH, namePaint);

        // game loop thread
        thread = new GameThread(getHolder(), this);
    }

    @Override public void surfaceCreated(@NonNull SurfaceHolder h) {
        thread.setRunning(true);
        thread.setPaused(false);
        thread.start();
    }

    @Override public void surfaceChanged(@NonNull SurfaceHolder h, int f, int w, int hgt) {
        renderer.onScreenResize(hgt);
    }

    @Override public void surfaceDestroyed(@NonNull SurfaceHolder h) {
        thread.setRunning(false);
        while (true) {
            try { thread.join(); break; }
            catch (InterruptedException ignored) {}
        }
    }

    /** Called each frame by GameThread */
    public void update(float dt) {
        car.update(dt, getWidth(), getHeight());
        renderer.update(getWidth());
    }

    /** Called each frame by GameThread */
    @Override
    protected void onDraw(Canvas canvas) {
        // not used by our loop; we call drawGame instead
    }

    public void drawGame(Canvas canvas) {
        renderer.draw(canvas, SettingsManager.getDriverName(getContext()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return input.onTouchEvent(ev, getWidth());
    }

    public void pause()  { thread.setPaused(true);  }
    public void resume() { thread.setPaused(false); }
}
