package com.example.curseinpanta;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import com.example.curseinpanta.GameView;
import com.example.curseinpanta.data.DbHolder;
import com.example.curseinpanta.data.PlayerDao;
import com.example.curseinpanta.data.PlayerStats;
import com.example.curseinpanta.utils.CoinManager;

public class GameActivity extends Activity implements GameView.CoinListener {
    private int runCoins = 0; // Coins collected this run
    private long startMs;     // For timing the run
    private GameView gameView;
    private TextView tvCoins, tvScore, tvHigh;
    private PlayerDao dao;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable scoreTick = new Runnable() {
        @Override public void run() {
            recalcScore();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        tvCoins = findViewById(R.id.tvCoins);
        tvScore = findViewById(R.id.tvScore);
        tvHigh  = findViewById(R.id.tvHigh);

        gameView = (GameView)findViewById(R.id.gameView);
        gameView.setCoinListener(this);


        dao = com.example.curseinpanta.data.DbHolder.get(this).playerDao();
    }

    @Override protected void onResume() {
        super.onResume();
        runCoins = 0;
        startMs = System.currentTimeMillis();
        handler.post(scoreTick);
    }

    @Override
    public void onCoinPicked() {
        runCoins++;
        recalcScore(); // Update score label on coin pickup
        // Update coins in UI
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }

    private void recalcScore() {
        int seconds = (int)((System.currentTimeMillis() - startMs) / 1000);
        int score = runCoins + seconds * 10;
        tvScore.setText("Score: " + score);

        PlayerStats s = dao.getPlayer();
        if (score > s.highScore) {
            s.highScore = score;
            dao.update(s);  // Update database
            tvHigh.setText("High: " + score);  // Update UI
        }
    }

    @Override protected void onPause() {
        super.onPause();
        // Save coins collected during this run
        if (runCoins > 0) {
            CoinManager.addCoins(this, runCoins);
            tvCoins.setText("Coins: " + CoinManager.getCoins(this));
            runCoins = 0;
        }
        handler.removeCallbacks(scoreTick);
    }

    @Override
    public void onCoinTotalChanged(int newTotal) {
        // Update coins in UI
        tvCoins.setText("Coins: " + newTotal);
    }
}

