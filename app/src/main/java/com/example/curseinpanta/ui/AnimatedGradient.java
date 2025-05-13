package com.example.curseinpanta.ui;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.graphics.ColorUtils;

public final class AnimatedGradient {

    private AnimatedGradient() {}  // util class

    private static final int TOP_COLOR    = Color.parseColor("#87CEEB");  // light sky
    private static final int BOTTOM_FROM  = Color.parseColor("#E0FFFF");  // pale turquoise
    private static final int BOTTOM_TO    = Color.parseColor("#B2DFDB");  // slightly darker blue-green
    private static final long DURATION_MS = 8_000;                        // one direction

    public static void applyTo(final View view) {
        final GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { TOP_COLOR, BOTTOM_FROM }
        );
        view.setBackground(drawable);

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(DURATION_MS);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);

        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            int blended = ColorUtils.blendARGB(BOTTOM_FROM, BOTTOM_TO, t);
            drawable.setColors(new int[] { TOP_COLOR, blended });
            // no need to invalidate: GradientDrawable does it
        });
        anim.start();
    }
}
