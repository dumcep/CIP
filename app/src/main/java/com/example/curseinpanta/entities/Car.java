package com.example.curseinpanta.entities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.curseinpanta.physics.PhysicsEngine;
import com.example.curseinpanta.utils.UpgradeManager;
import com.example.curseinpanta.GameConfig;

public class Car {
    private final Context ctx;     // <-- store once

    private float x, y;
    private final float width, height;
    private float velocityX = 0, velocityY = 0;
    private boolean isAccelerating = false;
    private boolean isBraking = false;
//    private final Paint paint;

    private final Paint bodyPaint;
    private final Paint wheelPaint;
    public Car(Context ctx, float x, float y, float w, float h, Paint bodyPaint) {
        this.ctx    = ctx;       // remember for upgrades / settings
        this.x      = x;
        this.y      = y;
        this.width  = w;
        this.height = h;

        this.bodyPaint  = bodyPaint;          // or whatever paints you use
        this.wheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.wheelPaint.setColor(Color.BLACK);
    }
    public void setAccelerating(boolean accelerating) {
        this.isAccelerating = accelerating;
    }

    public void setBraking(boolean braking) {
        this.isBraking = braking;
    }

    public void update(float deltaTime, int screenWidth, int screenHeight) {
        float accel = GameConfig.BASE_ACCELERATION * UpgradeManager.accelMult(ctx);
        float brake = GameConfig.BASE_BRAKE_FORCE  * UpgradeManager.brakeMult(ctx);
        float maxSp = GameConfig.BASE_MAX_SPEED    * UpgradeManager.speedMult(ctx);
        // — Apply controls —
        if(isAccelerating) velocityX += accel * deltaTime;
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
        // clamp
        velocityX = Math.max(-maxSp, Math.min(maxSp, velocityX));
        //Boundaries
//        if (x + width > screenWidth) {
//            x = screenWidth - width;
//            velocityX = 0;
//        }
    }

    public void draw(Canvas c) {
        // --- wheels ---
        float wheelR = height * 0.4f;
        float wheelY = y + height - wheelR;             // bottoms aligned with chassis
        c.drawCircle(x + wheelR*1.2f, wheelY, wheelR, wheelPaint);            // left wheel
        c.drawCircle(x + width - wheelR*1.2f, wheelY, wheelR, wheelPaint);    // right wheel

        // --- chassis ---
        c.drawRect(x, y, x + width, y + height * 0.6f, bodyPaint);

        // --- cab / roof ---
        float cabLeft   = x + width * 0.3f;
        float cabRight  = x + width * 0.75f;
        float cabBottom = y + height * 0.6f;
        float cabTop    = y + height * 0.25f;
        Paint cabPaint = new Paint(bodyPaint);
        cabPaint.setColor(Color.rgb(200,50,50));        // slightly darker red
        c.drawRect(cabLeft, cabTop, cabRight, cabBottom, cabPaint);
    }

    public float getHeight() { return height; }
    public void setY(float y) { this.y = y; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
}
