package com.example.curseinpanta.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.curseinpanta.GameView;
import com.example.curseinpanta.R;
import com.example.curseinpanta.utils.CoinManager;

public class GameActivity extends Activity implements GameView.CoinListener {

    private GameView gameView;
    private TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        tvCoins  = findViewById(R.id.tvCoins);

        // *this* activity wants coin updates
        gameView.setCoinListener(this);

        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }
    // ---- CoinListener implementation ----

    public void onCoinTotalChanged(int newTotal) {
        tvCoins.setText("Coins: " + newTotal);
    }


    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        // Refresh coins if they changed
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}

