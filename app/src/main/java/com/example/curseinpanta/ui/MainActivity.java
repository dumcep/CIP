package com.example.curseinpanta.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.curseinpanta.GameView;

public class MainActivity extends AppCompatActivity { // Or Activity

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause(); // Call GameView's pause method
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume(); // Call GameView's resume method
    }
}