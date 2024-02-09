package com.joc_educativ;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingActivity extends AppCompatActivity {

    private View decorView;
    private ProgressBar progressBar;
    private TextView percentageText;
    private Button playButton;

    private int progressStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        percentageText = findViewById(R.id.percentageText);
        playButton = findViewById(R.id.playButton);

        decorView = getWindow().getDecorView();//hide system bars

        progressAnimation();//loading

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity();
            }
        });
    }

    //hide system bars
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //---

    private void progressAnimation() {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;

                    handler.post(new Runnable() {// update progress bar
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            percentageText.setText(progressBar.getProgress() + "%");

                            if (progressBar.getProgress()==100)
                                playButton.setVisibility(View.VISIBLE);
                        }
                    });

                    try {
                        Thread.sleep(100); // wait 100 milliseconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void openCategoryActivity(){
        Intent intent = new Intent(this, CategoryMenuActivity.class);
        startActivity(intent);
        finish();
    }
}