package com.example.curseinpanta;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.example.curseinpanta.data.DbHolder;
import com.example.curseinpanta.data.PlayerDao;
import com.example.curseinpanta.data.PlayerStats;
import com.example.curseinpanta.utils.CoinManager;

public class GameActivity extends Activity implements GameView.CoinListener {

    private int runCoins = 0;
    private long startMs;
    private GameView gameView;
    private TextView tvCoins, tvScore, tvHigh;
    private PlayerDao dao;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable scoreTick = new Runnable() {
        @Override
        public void run() {
            recalcScore();
            handler.postDelayed(this, 1000);
        }
    };
    private GameThread gameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize views
        tvCoins = findViewById(R.id.tvCoins);
        tvScore = findViewById(R.id.tvScore);
        tvHigh  = findViewById(R.id.tvHigh);

        // Initialize database
        dao = DbHolder.get(this).playerDao();

        // Setup game view
        gameView = findViewById(R.id.gameView);
        gameView.setCoinListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        runCoins = 0;
        startMs = System.currentTimeMillis();
        handler.post(scoreTick);

        // Update coin and high score display
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
        PlayerStats s = dao.getPlayer();
        tvHigh.setText("High: " + s.highScore);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(scoreTick);
        if (gameView != null) {
            gameView.stopGame();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null) {
            gameView.stopGame();
        }
    }

    @Override
    public void onCoinPicked() {
        runCoins++;
        CoinManager.addCoins(this, 1);
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
        recalcScore();
    }

    private void recalcScore() {
        int seconds = (int) ((System.currentTimeMillis() - startMs) / 1000);
        int score = runCoins + seconds * 10;
        tvScore.setText("Score: " + score);

        PlayerStats s = dao.getPlayer();
        if (score > s.highScore) {
            s.highScore = score;
            new Thread(() -> {
                dao.update(s);
            }).start();
        }
    }

    @Override
    public void onCoinTotalChanged(int newTotal) {
        tvCoins.setText("Coins: " + newTotal);
    }

}
