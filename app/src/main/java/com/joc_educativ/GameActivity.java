package com.joc_educativ;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.joc_educativ.CustomView.DrawGameView;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.LevelModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private View decorView;
    private DrawGameView gameView;
    public View codeView, codeElementView;
    public ImageButton backButton, playButton, stopButton;
    private static int levelId;
    public static int xCar, yCar;
    public static Thread animationThread;
    public boolean isRunning = true;

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

        gameView.setLevelId(levelId);

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

        DatabaseHelper db = new DatabaseHelper(this);
        LevelModel levelModel = db.selectLevelById(levelId);//get level data
        Context context = this;//save context

        List<String> executeCodeList = new ArrayList<>();
        executeCodeList.add("right");
        executeCodeList.add("right");
        executeCodeList.add("right");//game over
        //executeCodeList.add("up");
        executeCodeList.add("right");
        executeCodeList.add("up");
        executeCodeList.add("right");
        executeCodeList.add("right");
        executeCodeList.add("down");
        executeCodeList.add("down");
        executeCodeList.add("right");
        executeCodeList.add("right");

        animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (xCar != -1)
                    reset();
                for (String codeLine : executeCodeList) {

                    if (codeLine.equals("right")) {
                        right();
                    } else if (codeLine.equals("left")) {
                        left();
                    } else if (codeLine.equals("up")) {
                        up();
                    } else if (codeLine.equals("down")) {
                        down();
                    }

                    //run operations on the main/UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (levelModel.getMap()[yCar][xCar].equals("R") || levelModel.getMap()[yCar][xCar].equals("T")) {
                                isRunning = false;
                                animationThread = null;
                                System.out.println("GAME OVER");
                                gameOver(context);
                            }
                        }
                    });


                    moveAndWait(1000);
                    if (!isRunning) {
                        break;
                    }
                }
                isRunning = !isRunning;
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

    private void reset() {
        gameView.redraw(-1, -1);
        moveAndWait(1000);
    }

    public void gameOver(Context context) {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.game_over);

        Button retryButton = dialog.findViewById(R.id.retryButton);
        Button homeButton = dialog.findViewById(R.id.homeButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLevelActivity();
            }
        });
        dialog.show();//open dialog
    }

    private void openLevelActivity() {
        DatabaseHelper dbh = new DatabaseHelper(this);
        int categoryId = dbh.selectCategoryIdByLevel(levelId);

        Intent intent = new Intent(this, LevelMenuActivity.class);
        intent.putExtra("categoryId", categoryId);//pass the category id in LevelActivity class
        startActivity(intent);
    }
}