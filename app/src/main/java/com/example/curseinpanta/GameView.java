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
import android.graphics.LinearGradient;
import android.graphics.Path;
import android.graphics.Shader;

import androidx.annotation.NonNull;

import com.example.curseinpanta.entities.Car;
import com.example.curseinpanta.input.InputController;
import com.example.curseinpanta.physics.PhysicsEngine;
import com.example.curseinpanta.utils.SettingsManager;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "GameView";
    private Paint skyPaint;
    private Paint hillPaint1, hillPaint2;
    private Path  hillPath;
    private Car car;
    private GameThread gameThread;
    private InputController inputController;

    private Paint groundPaint;
    private Paint uiPaint;
    private Paint textPaint;
    private Path terrainPath;
    private float cameraX = 0;

    private static final float BASE_LINE = PhysicsEngine.GROUND_HEIGHT;
    private static final float WAVE1_AMP = 220f;
    private static final float WAVE1_LEN = 900f;
    private static final float WAVE2_AMP = 90f;
    private static final float WAVE2_LEN = 450f;

    /**
     * @param worldX   X position in the infinite world
     * @param groundY  base Y coordinate where hills start
     * @return pixel Y of terrain at that worldX
     */
    private float getHillYAt(float worldX, float groundY) {
        // combine two sine-waves for variety
        float h1 = (float)(Math.sin(worldX / WAVE1_LEN) * WAVE1_AMP);
        float h2 = (float)(Math.sin(worldX / WAVE2_LEN + 1.3) * WAVE2_AMP);
        return groundY - (h1 + h2);
    }


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
        terrainPath = new Path();

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

        // Sky gradient: top→bottom
        skyPaint = new Paint();
        skyPaint.setShader(new LinearGradient(
                0, 0,
                0, getResources().getDisplayMetrics().heightPixels,
                Color.parseColor("#87CEEB"),   // light sky blue
                Color.parseColor("#E0FFFF"),   // pale turquoise horizon
                Shader.TileMode.CLAMP
        ));

        // Two hill colors for layering
        hillPaint1 = new Paint();
        hillPaint1.setColor(Color.parseColor("#4CAF50"));  // green
        hillPaint1.setStyle(Paint.Style.FILL);

        hillPaint2 = new Paint();
        hillPaint2.setColor(Color.parseColor("#388E3C"));  // darker green
        hillPaint2.setStyle(Paint.Style.FILL);

        hillPath = new Path();
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

        // center the camera a bit behind the car's x
        float targetCamX = car.getX() - (getWidth() * 0.3f);
        cameraX = Math.max(0, targetCamX);
    }


    public void drawGame(Canvas canvas) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        float groundY = h - BASE_LINE;

        // 1) Static background
        int bgRes = SettingsManager.getBackgroundResId(getContext());
        Bitmap bgBmp = BitmapFactory.decodeResource(getResources(), bgRes);
        Bitmap bgScaled = Bitmap.createScaledBitmap(bgBmp, w, h, true);
        canvas.drawBitmap(bgScaled, 0, 0, null);

        // 2) Back‐layer hills (darker) for some depth
        terrainPath.reset();
        terrainPath.moveTo(0, h);
        terrainPath.lineTo(0, getHillYAt(cameraX + 0, groundY));
        for (int sx = 20; sx <= w + 20; sx += 20) {
            float y = getHillYAt(cameraX + sx, groundY);
            terrainPath.lineTo(sx, y);
        }
        terrainPath.lineTo(w, h);
        terrainPath.close();
        canvas.drawPath(terrainPath, hillPaint2);

        // 3) Front‐layer hills (lighter)
        terrainPath.reset();
        terrainPath.moveTo(0, h);
        terrainPath.lineTo(0, getHillYAt(cameraX + 0, groundY + 40)); // shift up a bit
        for (int sx = 20; sx <= w + 20; sx += 20) {
            float y = getHillYAt(cameraX + sx, groundY + 40);
            terrainPath.lineTo(sx, y);
        }
        terrainPath.lineTo(w, h);
        terrainPath.close();
        canvas.drawPath(terrainPath, hillPaint1);

        // 4) (Optional) ground strip
        canvas.drawRect(0, groundY, w, h, groundPaint);

        // 5) Car (translate world so camera follows)
        canvas.save();
        canvas.translate(-cameraX, 0);
        car.draw(canvas);
        canvas.restore();

        // 6) Driver name in screen‐space
        String name = SettingsManager.getDriverName(getContext());
        float textX = (car.getX() - cameraX) + car.getWidth()/2f;
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

