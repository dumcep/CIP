package com.example.curseinpanta;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.example.curseinpanta.utils.SettingsManager;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText    etName = findViewById(R.id.etDriverName);
        RadioGroup  rgBg   = findViewById(R.id.rgBackground);
        Button      btnSave= findViewById(R.id.btnSaveSettings);

        // populate current settings
        etName.setText(SettingsManager.getDriverName(this));
        int currentBg = SettingsManager.getBackgroundResId(this);
        if (currentBg == R.drawable.bg_alternate) {
            rgBg.check(R.id.rbAlternate);
        } else {
            rgBg.check(R.id.rbDefault);
        }

        btnSave.setOnClickListener(v -> {
            // save driver name
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                SettingsManager.setDriverName(this, name);
            }
            // save background choice
            int chosenRes = (rgBg.getCheckedRadioButtonId() == R.id.rbAlternate)
                    ? R.drawable.bg_alternate
                    : R.drawable.bg_main_menu;
            SettingsManager.setBackgroundResId(this, chosenRes);

            finish();
        });
    }
}
