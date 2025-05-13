package com.example.curseinpanta;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.curseinpanta.utils.CoinManager;

public class ShopActivity extends Activity {
    private TextView tvCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        tvCoins = findViewById(R.id.tvCoins);
        updateCoins();

        // placeholder behaviour—just show a toast for now
        Button[] buttons = {
                findViewById(R.id.btnUpgradeAccel),
                findViewById(R.id.btnUpgradeBrake),
                findViewById(R.id.btnUpgradeSpeed),
                findViewById(R.id.btnUnlockBg)
        };
        for (Button b : buttons) {
            b.setOnClickListener(v ->
                    Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCoins();
    }

    private void updateCoins() {
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }
}
