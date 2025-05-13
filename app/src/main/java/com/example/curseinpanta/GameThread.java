package com.example.curseinpanta;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Handles the main game loop: timing, updating and rendering.
 */
public class GameThread extends Thread {
    private static final String TAG = "GameThread";

    private final SurfaceHolder surfaceHolder;
    private final GameView     gameView;
    private volatile boolean   running = false;
    private volatile boolean   paused  = false;
    private long               lastTimeNano;

    public GameThread(SurfaceHolder holder, GameView view) {
        this.surfaceHolder = holder;
        this.gameView      = view;
    }

    public void setRunning(boolean run) { this.running = run; }
    public void setPaused (boolean p)   { this.paused  = p;   }

    @Override
    public void run() {
        Log.d(TAG, "Starting game loop");
        lastTimeNano = System.nanoTime();

        while (running) {
            if (paused) {
                try { Thread.sleep(50); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                continue;
            }

            long now   = System.nanoTime();
            float dt   = (now - lastTimeNano) / 1_000_000_000f;
            lastTimeNano = now;
            if (dt > 0.1f) dt = 0.016f;  // clamp

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    synchronized (surfaceHolder) {
                        gameView.update(dt);
                        gameView.drawGame(canvas);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loop", e);
            } finally {
                if (canvas != null) {
                    try { surfaceHolder.unlockCanvasAndPost(canvas); }
                    catch (Exception e) { Log.e(TAG, "Unlock failed", e); }
                }
            }
        }
        Log.d(TAG, "Game loop ended");
    }
}
