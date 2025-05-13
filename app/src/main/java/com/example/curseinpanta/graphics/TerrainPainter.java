package com.example.curseinpanta.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Color;

public class TerrainPainter {
    private static final float BASE_LINE = 220f;   // px from bottom
    private static final float A1 = 220f, L1 = 900f;
    private static final float A2 =  90f, L2 = 450f;

    private final Paint backPaint, frontPaint, stripPaint;
    private final Path  path = new Path();

    public TerrainPainter() {
        backPaint  = makePaint("#388E3C"); // dark green
        frontPaint = makePaint("#4CAF50"); // light green
        stripPaint = makePaint("#4CAF50"); // thin ground band
    }
    private static Paint makePaint(String hex) {
        Paint p = new Paint();
        p.setColor(Color.parseColor(hex));
        p.setStyle(Paint.Style.FILL);
        return p;
    }

    private float yAt(float worldX, float groundY) {
        float h1 = (float)(Math.sin(worldX / L1)       * A1);
        float h2 = (float)(Math.sin(worldX / L2 + 1.3f)* A2);
        return groundY - (h1 + h2);
    }

    public void draw(Canvas c, float camX) {
        int w = c.getWidth(), h = c.getHeight();
        float groundY = h - BASE_LINE;

        // --- back layer ---
        path.reset(); path.moveTo(0, h);
        path.lineTo(0, yAt(camX, groundY));
        for (int sx = 20; sx <= w+20; sx+=20)
            path.lineTo(sx, yAt(camX + sx, groundY));
        path.lineTo(w, h); path.close();
        c.drawPath(path, backPaint);

        // --- front layer ---
        path.reset(); path.moveTo(0, h);
        path.lineTo(0, yAt(camX, groundY + 40));
        for (int sx = 20; sx <= w+20; sx+=20)
            path.lineTo(sx, yAt(camX + sx, groundY + 40));
        path.lineTo(w, h); path.close();
        c.drawPath(path, frontPaint);

        // --- ground strip (covers tiny gaps) --- Commented
//        c.drawRect(0, groundY, w, h, stripPaint);
    }

    /** helps GameView adjust car Y on resize */
    public float getGroundY(int screenHeight) {
        return screenHeight - BASE_LINE;
    }
    public float getGroundYAt(float worldX, int screenHeight) {
        float baseline = screenHeight - BASE_LINE;
        // mirror the same two-wave formula
        return yAt(worldX, baseline);
    }
}
