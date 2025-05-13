package com.example.curseinpanta.entities;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.example.curseinpanta.physics.PhysicsEngine;

public class Car {
    private float x, y;
    private final float width, height;
    private float velocityX = 0, velocityY = 0;
    private boolean isAccelerating = false;
    private boolean isBraking = false;
    private final Paint paint;

    public Car(float x, float y, float width, float height, Paint paint) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.paint = paint;
    }

    public void setAccelerating(boolean accelerating) {
        this.isAccelerating = accelerating;
    }

    public void setBraking(boolean braking) {
        this.isBraking = braking;
    }

    public void update(float deltaTime, int screenWidth, int screenHeight) {
        // — Apply controls —
        if (isAccelerating) velocityX += PhysicsEngine.ACCELERATION * deltaTime;
        if (isBraking)      velocityX -= PhysicsEngine.BRAKING_FORCE * deltaTime;

        // — Linear damping friction —
        if (velocityX > 0) {
            velocityX -= PhysicsEngine.LINEAR_DAMPING * deltaTime;
            if (velocityX < 0) velocityX = 0;
        } else if (velocityX < 0) {
            velocityX += PhysicsEngine.LINEAR_DAMPING * deltaTime;
            if (velocityX > 0) velocityX = 0;
        }

        // — Gravity —
        velocityY += PhysicsEngine.GRAVITY * deltaTime;

        // — Position update —
        x += velocityX * deltaTime;
        y += velocityY * deltaTime;

        // — Ground collision —
        float groundLevel = screenHeight - PhysicsEngine.GROUND_HEIGHT;
        if (y + height > groundLevel) {
            y = groundLevel - height;
            velocityY = 0;
        }

        // — Keep within horizontal bounds —
        if (x < 0) {
            x = 0;
            velocityX = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
            velocityX = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public float getHeight() { return height; }
    public void setY(float y) { this.y = y; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
}
