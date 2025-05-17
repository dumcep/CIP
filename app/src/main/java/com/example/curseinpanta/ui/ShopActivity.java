package com.example.curseinpanta.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.curseinpanta.R;
import com.example.curseinpanta.GameConfig;          // ← correct package
import com.example.curseinpanta.graphics.AnimatedGradient;
import com.example.curseinpanta.utils.CoinManager;
import com.example.curseinpanta.utils.UpgradeManager;

public class ShopActivity extends Activity {

    private TextView tvCoins;
    private Button   btnAccel, btnBrake, btnSpeed, btnBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // animated gradient on the root
        AnimatedGradient.applyTo(findViewById(R.id.shopRoot));

        // coin label
        tvCoins = findViewById(R.id.tvCoins);
        updateCoins();

        // buttons
        btnAccel = findViewById(R.id.btnUpgradeAccel);
        btnBrake = findViewById(R.id.btnUpgradeBrake);
        btnSpeed = findViewById(R.id.btnUpgradeSpeed);
        btnBg    = findViewById(R.id.btnUnlockBg);

        bindUpgrade(btnAccel, () -> UpgradeManager.buyAccel(this));
        bindUpgrade(btnBrake, () -> UpgradeManager.buyBrake(this));
        bindUpgrade(btnSpeed, () -> UpgradeManager.buySpeed(this));
        bindUpgrade(btnBg,    () -> UpgradeManager.buyBackground(this));

        refreshButtonStates();
    }

    /** Generic wrapper: try to buy, refresh UI, show toast if no coins */
    private void bindUpgrade(Button btn, UpgradeAttempt attempt) {
        btn.setOnClickListener(v -> {
            boolean success = attempt.buy();
            if (!success) {
                Toast.makeText(this, "Not enough coins!", Toast.LENGTH_SHORT).show();
            }
            updateCoins();
            refreshButtonStates();
        });
    }

    /** Enable/disable buttons when maxed or already unlocked */
    private void refreshButtonStates() {
        btnAccel.setEnabled(UpgradeManager.getAccelLvl(this)
                < GameConfig.MULTIPLIER_ACCEL_LEVELS.length);
        btnBrake.setEnabled(UpgradeManager.getBrakeLvl(this)
                < GameConfig.MULTIPLIER_BRAKE_LEVELS.length);
        btnSpeed.setEnabled(UpgradeManager.getSpeedLvl(this)
                < GameConfig.MULTIPLIER_SPEED_LEVELS.length);
        btnBg.setEnabled(!UpgradeManager.isBgUnlocked(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCoins();
        refreshButtonStates();
    }

    private void updateCoins() {
        tvCoins.setText("Coins: " + CoinManager.getCoins(this));
    }

    // --------------- functional interface -----------------
    @FunctionalInterface
    private interface UpgradeAttempt { boolean buy(); }
}
