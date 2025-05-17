package com.example.curseinpanta.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.animation.LinearInterpolator;

import com.example.curseinpanta.GameActivity;
import com.example.curseinpanta.R;
import com.example.curseinpanta.data.DbHolder;
import com.example.curseinpanta.data.PlayerDao;
import com.example.curseinpanta.data.PlayerStats;
import com.example.curseinpanta.graphics.AnimatedGradient;
import com.example.curseinpanta.utils.CoinManager;



public class MainMenuActivity extends Activity {

    private PlayerDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu); // Only once!

        // --- animated gradient background ---
        AnimatedGradient.applyTo(findViewById(R.id.menuRoot));

        // --- title animation ---
        TextView tvTitle = findViewById(R.id.tvTitle);
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.90f, 1.10f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 0.90f, 1.10f);
        ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(tvTitle, pvhX, pvhY);
        pulse.setDuration(850);
        pulse.setInterpolator(new LinearInterpolator());
        pulse.setRepeatCount(ObjectAnimator.INFINITE);
        pulse.setRepeatMode(ObjectAnimator.REVERSE);
        pulse.start();

        // --- Initialize DAO ---
        dao = DbHolder.get(this).playerDao();

        // --- Bootstrap DB if missing ---
        PlayerStats stats = dao.getPlayer();
        if (stats == null) {
            stats = new PlayerStats();
            stats.coins = getSharedPreferences("game_prefs", MODE_PRIVATE).getInt("coins", 0);
            stats.highScore = 0;
            dao.insert(stats);
        }

        // --- Coin display ---
        TextView tvCoins = findViewById(R.id.tvCoins);
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));

        // --- Buttons ---
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
        // Refresh coins (e.g., after visiting shop)
        TextView tvCoins = findViewById(R.id.tvCoins);
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }
}
