package com.example.curseinpanta.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.LinearInterpolator;

import com.example.curseinpanta.R;
import com.example.curseinpanta.utils.CoinManager;

public class MainMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // --- Minecraft-style pulsing title ---
        TextView tvTitle = findViewById(R.id.tvTitle);

        // scale 0.90 → 1.10 and back, forever
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.90f, 1.10f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 0.90f, 1.10f);

        ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(tvTitle, pvhX, pvhY);
        pulse.setDuration(850);            // 1.2 s for one grow-shrink cycle
        pulse.setInterpolator(new LinearInterpolator());
        pulse.setRepeatCount(ObjectAnimator.INFINITE);
        pulse.setRepeatMode(ObjectAnimator.REVERSE);
        pulse.start();

        //  NEW: animated gradient background
        AnimatedGradient.applyTo(findViewById(R.id.menuRoot));

        // coin display
        TextView tvCoins = findViewById(R.id.tvCoins);
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));

        // buttons
        Button btnPlay     = findViewById(R.id.btnPlay);
        Button btnShop     = findViewById(R.id.btnShop);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnCredits  = findViewById(R.id.btnCredits);

        btnPlay.setOnClickListener(v ->
                startActivity(new Intent(this, GameActivity.class))
        );

        btnShop.setOnClickListener(v ->
                startActivity(new Intent(this, ShopActivity.class))
        );

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );

        btnCredits.setOnClickListener(v ->
                startActivity(new Intent(this, CreditsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh coins if you returned from shop
        TextView tvCoins = findViewById(R.id.tvCoins);
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }
}
