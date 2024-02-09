package com.joc_educativ;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.joc_educativ.CustomView.DrawGameView;

public class GameActivity extends AppCompatActivity {

    private View decorView;
    private DrawGameView gameView;
    public View codeView, codeElementView;
    private ImageButton backButton, playButton, stopButton;
    private int levelId;
    public static int xCar,yCar;
    Thread animationThread;
    private boolean isRunning = true;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        decorView = getWindow().getDecorView();//hide system bars

        Intent intent = getIntent();//get submitted level id
        levelId = intent.getIntExtra("levelId", -1);

        gameView = findViewById(R.id.gameView);
        codeView = findViewById(R.id.codeView);
        codeElementView = findViewById(R.id.codeElementView);

        backButton = findViewById(R.id.backButton);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        //DrawGameView drawGameView = new DrawGameView(this,gameView.getWidth(),gameView.getHeight());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (animationThread == null) {
                    isRunning = true;
                    //xCar=-1;
                    //yCar=-1;
                    //reset();
                    System.out.println("RUN");
                    playGame();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (animationThread != null) {
                    System.out.println("STOP");
                    isRunning = false;
                    animationThread = null;
                }
            }
        });
    }

    //hide system bars
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //---

    private void playGame() {
        //Solution
        //right();->right();->up();->right();->up();->right();->right();->down();->down();->right();->right();


        animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    if (xCar!=-1)
                        reset();
                    right();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    right();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    up();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    right();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    up();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    right();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    right();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    down();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    down();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    right();
                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                    right();
                    moveAndWait(1000);
                    isRunning = !isRunning;
                }
            }
        });
        animationThread.start();
    }

    private void moveAndWait(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void right() {
        xCar++;
        gameView.redraw(xCar, yCar);
    }

    private void left() {
        xCar--;
        gameView.redraw(xCar, yCar);
    }

    private void down() {
        yCar++;
        gameView.redraw(xCar, yCar);
    }

    private void up() {
        yCar--;
        gameView.redraw(xCar, yCar);
    }

    private void reset(){
        gameView.redraw(-1, -1);
        moveAndWait(1000);
    }
}