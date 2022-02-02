package com.azmathunzai.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by virgo on 10/3/16.
 */
public class Splash extends AppCompatActivity {
    private ProgressBar progressBar;
    int progress=0;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        CountDownTimer timer = new CountDownTimer(3000,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progress);
                progress= progress+6;
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        timer.start();
    }
}
