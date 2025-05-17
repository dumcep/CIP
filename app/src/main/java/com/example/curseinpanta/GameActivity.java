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
    private int runCoins   = 0;   // ← coins collected in this run
    private int startTime;        // seconds offset

    private GameView gameView;
    private TextView tvCoins, tvScore, tvHigh;
    private PlayerDao dao;

    private int   baselineCoins;
    private long  startMs;



    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable scoreTick = new Runnable() {
        @Override public void run() {
            recalcScore();                    // update Score + maybe High
            handler.postDelayed(this, 1000);  // every second
        }
    };

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_game);

        dao = DbHolder.get(this).playerDao();

        tvCoins = findViewById(R.id.tvCoins);
        tvScore = findViewById(R.id.tvScore);
        tvHigh  = findViewById(R.id.tvHigh);

        gameView = findViewById(R.id.gameView);
        gameView.setCoinListener(this);

        PlayerStats s = dao.getPlayer();
        tvCoins.setText("Coins: " + s.coins);
        tvHigh .setText("Highscore: "  + s.highScore);
    }

    @Override protected void onResume() {
        super.onResume();
        runCoins   = 0;
        startTime  = (int)(System.currentTimeMillis()/1000);
        handler.post(scoreTick);
    }

    // ---- listener callbacks ----
    @Override
    public void onCoinPicked() {
        runCoins++;          // or whatever you track per run
        recalcScore();       // update score immediately
    }


    @Override protected void onPause() {
        super.onPause();
        if (runCoins > 0) {
            CoinManager.addCoins(this, runCoins);
            runCoins = 0;
            // now update the on‐screen coin total:
            tvCoins.setText("Coins: " + CoinManager.getCoins(this));
        }
        handler.removeCallbacks(scoreTick);
        gameView.pause();
    }

    /** CoinListener – runs *only* when a coin is actually collected or spent */

    /** seconds*10 + coinsGained; store/refresh High */
    private void recalcScore() {
        int seconds = (int)(System.currentTimeMillis()/1000) - startTime;
        int score   = runCoins + seconds*10;
        tvScore.setText("Score: " + score);

        PlayerStats s = dao.getPlayer();
        if (score > s.highScore) {
            s.highScore = score;
            dao.update(s);
            tvHigh.setText("High: " + score);
        }
    }
}
