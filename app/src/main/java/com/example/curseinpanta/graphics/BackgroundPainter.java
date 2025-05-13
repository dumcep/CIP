package com.example.curseinpanta.graphics;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Color;

public class BackgroundPainter {

    // day palette
    private static final int TOP_DAY = Color.parseColor("#87CEEB");
    private static final int BOT_DAY = Color.parseColor("#E0FFFF");

    // night palette
    private static final int TOP_NIGHT = Color.parseColor("#003366");
    private static final int BOT_NIGHT = Color.parseColor("#000022");

    private boolean night = false;
    private final Paint paint = new Paint();
    private final RectF rect  = new RectF();

    private final int screenHeight;

    public BackgroundPainter(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /** Toggle from SettingsManager */
    public void setNight(boolean n) { night = n; }

    public void draw(Canvas c) {
        // choose palette
        int top = night ? TOP_NIGHT : TOP_DAY;
        int bot = night ? BOT_NIGHT : BOT_DAY;

        // build fresh shader for current palette
        LinearGradient lg = new LinearGradient(
                0, 0, 0, screenHeight,
                top, bot,
                Shader.TileMode.CLAMP
        );
        paint.setShader(lg);

        // fill entire canvas
        rect.set(0, 0, c.getWidth(), c.getHeight());
        c.drawRect(rect, paint);
    }
}
