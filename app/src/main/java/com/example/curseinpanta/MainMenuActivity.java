package com.example.curseinpanta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.curseinpanta.utils.CoinManager;

public class MainMenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // background
        ImageView bg = findViewById(R.id.backgroundImage);
        bg.setImageResource(R.drawable.bg_main_menu);

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
