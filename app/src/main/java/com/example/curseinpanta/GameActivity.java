package com.example.curseinpanta;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.curseinpanta.utils.CoinManager;

public class GameActivity extends Activity {
    private GameView gameView;
    private TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate our layout (not new GameView(this)!)
        setContentView(R.layout.activity_game);

        // Bind our views by their IDs
        gameView = findViewById(R.id.gameView);
        tvCoins  = findViewById(R.id.tvCoins);

        // Initial coin display
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
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
