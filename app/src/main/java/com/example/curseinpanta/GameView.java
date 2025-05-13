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

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "GameView";

    private Car car;
    private GameThread gameThread;
    private InputController inputController;

    private Paint groundPaint;
    private Paint uiPaint;
    private Paint textPaint;

    // 1) XML constructor
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // 2) Code constructor
    public GameView(Context context) {
        super(context);
        init(context);
    }

    // common setup
    private void init(Context context) {
        // holder & callback
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // paints
        Paint carPaint = new Paint();
        carPaint.setColor(Color.RED);

        groundPaint = new Paint();
        groundPaint.setColor(Color.GREEN);

        uiPaint = new Paint();
        uiPaint.setColor(Color.argb(50, 255, 255, 255));

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // game objects & controller
        car = new Car(100, 500, 100, 50, carPaint);
        inputController = new InputController(car);

        // game loop thread
        gameThread = new GameThread(holder, this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
        gameThread.setRunning(true);
        gameThread.setPaused(false);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder,
                               int format, int width, int height) {
        Log.d(TAG, "Surface changed: " + width + "×" + height);
        car.setY(height - PhysicsEngine.GROUND_HEIGHT - car.getHeight());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
        gameThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted joining game thread, retrying...", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void update(float deltaTime) {
        car.update(deltaTime, getWidth(), getHeight());
    }

    public void drawGame(Canvas canvas) {
        // background
        int bgRes = SettingsManager.getBackgroundResId(getContext());
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), bgRes);
        Bitmap scaled = Bitmap.createScaledBitmap(
                bmp, canvas.getWidth(), canvas.getHeight(), true);
        canvas.drawBitmap(scaled, 0, 0, null);

        // ground
        float top = canvas.getHeight() - PhysicsEngine.GROUND_HEIGHT;
        canvas.drawRect(0, top, canvas.getWidth(), canvas.getHeight(), groundPaint);

        // car
        car.draw(canvas);

        // driver name
        String name = SettingsManager.getDriverName(getContext());
        float textX = car.getX() + car.getWidth()  / 2f;
        float textY = car.getY() - 16f;
        canvas.drawText(name, textX, textY, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return inputController.onTouchEvent(event, getWidth());
    }

    public void pause() {
        Log.d(TAG, "Pausing game");
        gameThread.setPaused(true);
    }

    public void resume() {
        Log.d(TAG, "Resuming game");
        gameThread.setPaused(false);
    }
}
