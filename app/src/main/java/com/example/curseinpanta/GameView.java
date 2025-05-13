package com.example.curseinpanta; // Use your actual package name

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log; // For debugging output
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "GameView"; // Tag for logging

    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false; // To handle pausing

    // --- Control State ---
    private volatile boolean isGasPressed = false;
    private volatile boolean isBrakePressed = false;
    // Store the pointer ID for gas/brake to handle multi-touch release correctly (optional but good practice)
    private int gasPointerId = -1;
    private int brakePointerId = -1;


    // --- Game Objects ---
    private float carX = 100;
    private float carY = 500;
    private float carWidth = 100;
    private float carHeight = 50;

    // --- Physics ---
    private float velocityX = 0;
    private float velocityY = 0;
    private final float GRAVITY = 9.8f * 20; // Pixels per second squared (Adjust multiplier for feel)
    private final float ACCELERATION = 200f; // Pixels per second squared
    private final float BRAKING_FORCE = 300f; // Pixels per second squared
    private final float FRICTION = 0.98f;    // Multiplier per frame (closer to 1 = less friction) - OR -
    private final float LINEAR_DAMPING = 50f; // Velocity decrease per second (Alternative friction)

    // --- Timing ---
    private long lastUpdateTimeNano = 0;

    // --- Drawing Tools ---
    private Paint carPaint;
    private Paint groundPaint;
    private Paint uiPaint; // For drawing control areas (optional)

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        carPaint = new Paint();
        carPaint.setColor(Color.RED);

        groundPaint = new Paint();
        groundPaint.setColor(Color.GREEN);

        uiPaint = new Paint(); // Optional: for visualizing touch areas
        uiPaint.setColor(Color.argb(50, 255, 255, 255)); // Semi-transparent white
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
        lastUpdateTimeNano = System.nanoTime(); // Initialize time tracking
        startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface changed: " + width + "x" + height);
        // Recalculate positions if needed based on new size
        carY = height - 200; // Example re-position
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
        stopGameLoop();
    }

    // --- Game Loop ---
    private void startGameLoop() {
        if (gameThread != null && gameThread.isAlive()) {
            return; // Avoid starting multiple threads
        }
        isRunning = true;
        isPaused = false;
        Runnable gameRunnable = () -> {
            while (isRunning) {
                if (isPaused) {
                    // If paused, just yield time slices
                    try {
                        Thread.sleep(50); // Sleep a bit longer when paused
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue; // Skip the rest of the loop
                }

                Canvas canvas = null;
                long currentTimeNano = System.nanoTime();
                // Calculate delta time in seconds
                float deltaTime = (currentTimeNano - lastUpdateTimeNano) / 1_000_000_000.0f;
                lastUpdateTimeNano = currentTimeNano;

                // Avoid huge deltaTime spikes if resuming from pause or on first frame
                if (deltaTime > 0.1f) {
                    deltaTime = 0.016f; // Clamp delta time to avoid large jumps (~60fps)
                }

                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas == null) continue;

                    synchronized (surfaceHolder) {
                        update(deltaTime); // Pass delta time to update logic
                        drawGame(canvas);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in game loop", e);
                }
                finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } catch(Exception e){
                            Log.e(TAG, "Error unlocking canvas", e);
                        }
                    }
                }
            }
            Log.d(TAG, "Game thread finished.");
        };
        gameThread = new Thread(gameRunnable);
        Log.d(TAG, "Starting game thread...");
        gameThread.start();
    }

    private void stopGameLoop() {
        isRunning = false;
        if (gameThread == null) return;

        boolean retry = true;
        while (retry) {
            try {
                Log.d(TAG, "Attempting to join game thread...");
                gameThread.join();
                retry = false;
                Log.d(TAG, "Game thread joined successfully.");
            } catch (InterruptedException e) {
                Log.w(TAG, "Interrupted while joining game thread, retrying...", e);
                Thread.currentThread().interrupt();
            }
        }
        gameThread = null;
    }

    // --- Game Logic Update ---
    private void update(float deltaTime) {
        // --- Apply Controls ---
        if (isGasPressed) {
            velocityX += ACCELERATION * deltaTime;
        }
        if (isBrakePressed) {
            velocityX -= BRAKING_FORCE * deltaTime;
        }

        // --- Apply Friction/Damping ---
        // Option 1: Multiplicative Friction (can feel skatey, careful near zero)
        // velocityX *= FRICTION; // This needs adjustment if using deltaTime

        // Option 2: Linear Damping (often feels better)
        if (velocityX > 0) {
            velocityX -= LINEAR_DAMPING * deltaTime;
            if (velocityX < 0) velocityX = 0; // Don't let damping reverse direction
        } else if (velocityX < 0) {
            velocityX += LINEAR_DAMPING * deltaTime;
            if (velocityX > 0) velocityX = 0;
        }


        // --- Apply Gravity ---
        velocityY += GRAVITY * deltaTime;

        // --- Update Position ---
        carX += velocityX * deltaTime;
        carY += velocityY * deltaTime;

        // --- Basic Ground Collision ---
        int screenHeight = getHeight();
        float groundLevel = screenHeight - 100;

        if (carY + carHeight > groundLevel) {
            carY = groundLevel - carHeight; // Place car exactly on the ground
            velocityY = 0; // Stop vertical movement
            // Could add simple bounce here: velocityY = -velocityY * BOUNCE_FACTOR;
        }

        // --- Keep car within screen bounds (Optional) ---
        if (carX < 0) {
            carX = 0;
            velocityX = 0; // Stop horizontal movement at edge
        }
        if (carX + carWidth > getWidth()) {
            carX = getWidth() - carWidth;
            velocityX = 0; // Stop horizontal movement at edge
        }
    }

    // --- Drawing ---
    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.CYAN); // Background

        // Draw Ground
        int screenHeight = canvas.getHeight();
        float groundTop = screenHeight - 100;
        canvas.drawRect(0, groundTop, canvas.getWidth(), screenHeight, groundPaint);

        // Draw Car
        // Later: Use canvas.save(), canvas.translate(), canvas.rotate(), drawRect, canvas.restore() for rotation
        canvas.drawRect(carX, carY, carX + carWidth, carY + carHeight, carPaint);

        // Optional: Draw UI touch areas for visualization
        // int screenWidth = canvas.getWidth();
        // canvas.drawRect(0, 0, screenWidth / 2f, screenHeight, uiPaint); // Left (Brake)
        // canvas.drawRect(screenWidth / 2f, 0, screenWidth, screenHeight, uiPaint); // Right (Gas)
    }

    // --- Input Handling ---
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // Use getActionMasked for multi-touch
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        int screenWidth = getWidth();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: // Handle additional fingers touching down
                if (x < screenWidth / 2f) { // Left side touched
                    isBrakePressed = true;
                    brakePointerId = pointerId; // Track which finger is braking
                    Log.d(TAG, "Brake pressed (Pointer ID: " + pointerId + ")");
                } else { // Right side touched
                    isGasPressed = true;
                    gasPointerId = pointerId; // Track which finger is accelerating
                    Log.d(TAG, "Gas pressed (Pointer ID: " + pointerId + ")");
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: // Handle a finger being lifted
                if (pointerId == brakePointerId) { // Check if the finger lifted was the one braking
                    isBrakePressed = false;
                    brakePointerId = -1; // Reset tracker
                    Log.d(TAG, "Brake released (Pointer ID: " + pointerId + ")");
                } else if (pointerId == gasPointerId) { // Check if the finger lifted was the one accelerating
                    isGasPressed = false;
                    gasPointerId = -1; // Reset tracker
                    Log.d(TAG, "Gas released (Pointer ID: " + pointerId + ")");
                }
                break;

            case MotionEvent.ACTION_CANCEL: // Handle interruption (e.g., app goes to background)
                isBrakePressed = false;
                isGasPressed = false;
                brakePointerId = -1;
                gasPointerId = -1;
                Log.d(TAG, "Touch cancelled");
                break;
        }

        // We return true because we are handling the touch events here.
        return true;
    }

    // --- Pause / Resume (Optional but good practice) ---
    public void pause() {
        Log.d(TAG, "Pausing game");
        isPaused = true;
        // Consider stopping physics updates or timers here if needed
    }

    public void resume() {
        Log.d(TAG, "Resuming game");
        lastUpdateTimeNano = System.nanoTime(); // Reset timer to avoid jump
        isPaused = false;
        // Restart any paused timers/physics if necessary
    }
}