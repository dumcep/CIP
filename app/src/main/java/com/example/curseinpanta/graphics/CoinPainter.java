package com.example.curseinpanta.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import com.example.curseinpanta.entities.Coin;
import java.util.List;

public class CoinPainter {
    private final Paint rim, fill;

    public CoinPainter() {
        rim  = new Paint(Paint.ANTI_ALIAS_FLAG);
        rim.setColor(Color.rgb(218,165,32));    // darker gold
        fill = new Paint(Paint.ANTI_ALIAS_FLAG);
        fill.setColor(Color.rgb(255,215,0));    // bright gold
    }

    public void draw(Canvas c, List<Coin> coins, float camX) {
        for (Coin coin : coins) {
            float cx = coin.x - camX;
            float cy = coin.y;
            c.drawCircle(cx, cy, Coin.R, rim);
            c.drawCircle(cx, cy, Coin.R - 4, fill);
        }
    }
}
