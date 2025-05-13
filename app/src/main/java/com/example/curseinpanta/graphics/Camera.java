package com.example.curseinpanta.graphics;

public class Camera {
    private float x = 0;

    /**
     * Centers the camera a little behind the car so the player sees ahead.
     */
    public void update(float carWorldX, int screenWidth) {
        float target = carWorldX - screenWidth * 0.3f;
        x = Math.max(0, target);
    }

    public float getX() { return x; }
}