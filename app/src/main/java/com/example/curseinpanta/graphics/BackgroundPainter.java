package com.example.curseinpanta.graphics;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Color;

public class BackgroundPainter {
    private final Paint paint;

    public BackgroundPainter(int screenHeight) {
        paint = new Paint();
        paint.setShader(new LinearGradient(
                0, 0, 0, screenHeight,
                Color.parseColor("#87CEEB"),   // light blue
                Color.parseColor("#E0FFFF"),   // turquoise horizon
                Shader.TileMode.CLAMP));
    }

    public void draw(Canvas c) {
        c.drawPaint(paint);
    }
}
