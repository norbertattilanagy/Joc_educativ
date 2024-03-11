package com.joc_educativ;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.joc_educativ.CustomView.DrawGameView;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.FirebaseDB;
import com.joc_educativ.Database.Level;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private View decorView;
    private DrawGameView gameView;
    public LinearLayout codeView;
    ScrollView codeScrollView;
    ImageButton homeButton, playButton, stopButton;
    Button rightButton, leftButton, upButton, downButton;
    private static int levelId;
    public static int xCar, yCar;
    public static Thread animationThread;
    public boolean isRunning = true;
    List<String> executeCodeList = new ArrayList<>();
    Context context = this;

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
        codeScrollView = findViewById(R.id.codeScrollView);

        homeButton = findViewById(R.id.homeButton);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        rightButton = findViewById(R.id.rightButton);
        leftButton = findViewById(R.id.leftButton);
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);

        gameView.setLevelId(levelId);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                openLevelActivity();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                if (animationThread == null) {
                    isRunning = true;
                    playGame();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(context);
                if (animationThread != null) {
                    isRunning = false;
                    animationThread = null;
                }
            }
        });

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        codeView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                Button draggedButton = (Button) dragEvent.getLocalState();

                //if draggedButton.getTag() != null then is from codeView
                if (dragEvent.getAction() == DragEvent.ACTION_DROP && draggedButton.getTag() == null) {
                    addNewElementInCode(view, dragEvent);
                }

                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED && draggedButton.getTag() != null && codeView.getChildCount() > 0) {
                    executeCodeList.remove(codeView.indexOfChild(draggedButton));
                    codeView.removeView(draggedButton);
                    putObjectSound();
                }
                return true;
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
        Level level = db.selectLevelById(levelId);//get level data
        Context context = this;//save context

        animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (xCar != -1)
                    reset();

                final ScrollView scrollview = ((ScrollView) findViewById(R.id.codeScrollView));
                for (int i = 0; i < executeCodeList.size(); i++) {
                    final int currentIndex = i;
                    final Button button = (Button) codeView.getChildAt(i);//get  current code element

                    //scroll in selected element
                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollview.smoothScrollTo(0, button.getTop() - button.getHeight());//view including the previous button
                        }
                    });

                    //set default background for code element
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("background == " + button.getBackground());
                            codeElementSelect(button, executeCodeList.get(currentIndex));
                            //button.setBackgroundResource(R.drawable.code_red_element_selected);
                            button.setSelected(true);
                        }
                    });

                    //go to the car
                    switch (executeCodeList.get(i)) {
                        case "right":
                            right();
                            break;
                        case "left":
                            left();
                            break;
                        case "up":
                            up();
                            break;
                        case "down":
                            down();
                            break;
                    }

                    //run operations on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (level.getMap()[yCar][xCar].equals("R") || level.getMap()[yCar][xCar].equals("T")) {
                                isRunning = false;
                                animationThread = null;
                                gameOver(context);
                            } else if (level.getMap()[yCar][xCar].equals("H")) {
                                isRunning = false;
                                animationThread = null;
                                completed(context);
                            }

                        }
                    });

                    moveAndWait(1000);

                    //set default background for code element
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            codeElementDefault(button, executeCodeList.get(currentIndex));
                        }
                    });

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

    //set select background for code element
    private void codeElementSelect(Button button, String action) {
        switch (action) {
            case "right":
                button.setBackgroundResource(R.drawable.code_right_element_selected);
                break;
            case "left":
                button.setBackgroundResource(R.drawable.code_left_element_selected);
                break;
            case "up":
                button.setBackgroundResource(R.drawable.code_up_element_selected);
                break;
            case "down":
                button.setBackgroundResource(R.drawable.code_down_element_selected);
                break;
        }
    }

    //set default background for code element
    private void codeElementDefault(Button button, String action) {
        switch (action) {
            case "right":
                button.setBackgroundResource(R.drawable.code_right_element_default);
                break;
            case "left":
                button.setBackgroundResource(R.drawable.code_left_element_default);
                break;
            case "up":
                button.setBackgroundResource(R.drawable.code_up_element_default);
                break;
            case "down":
                button.setBackgroundResource(R.drawable.code_down_element_default);
                break;
        }
    }

    public void gameOver(Context context) {

        if (SetingsPreferencis.getVibration(context)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

        if (SetingsPreferencis.getSound(context)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.crash);
            mediaPlayer.seekTo(0); // Rewind sound to beginning
            mediaPlayer.start();
        }

        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(R.layout.game_over_dialog);
        dialog.setCancelable(false);

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

    private void completed(Context context) {
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        dialog.setContentView(R.layout.completed_dialog);
        dialog.setCancelable(false);

        Button retryButton = dialog.findViewById(R.id.retryButton);
        Button nextLeveButton = dialog.findViewById(R.id.nextLevelButton);
        Button homeButton = dialog.findViewById(R.id.homeButton);

        if (verifyNextLevel() == -1)//if exist next level
            nextLeveButton.setVisibility(View.GONE);
        else {
            DatabaseHelper db = new DatabaseHelper(this);//save next unlocked level
            Level level = db.selectLevelById(levelId);
            int unlockedLevel = db.selectUnlockedLevel(level.getCategoryId());//select unlocked level nr

            if (verifyNextLevel() > unlockedLevel) {
                db.updateUnlockedLevel(level.getCategoryId(), verifyNextLevel());
                saveLevelInFirebase(level.getCategoryId(), verifyNextLevel());
            }
        }

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        nextLeveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNextLevel();
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

    private int verifyNextLevel() {
        DatabaseHelper db = new DatabaseHelper(this);
        Level level = db.selectLevelById(levelId);//get level data
        return db.selectNextLevelId(level.getCategoryId(), level.getLevel() + 1);//return next level Id
    }

    private void openNextLevel() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("levelId", verifyNextLevel());//pass the category id in LevelActivity class
        this.startActivity(intent);
    }

    private void openLevelActivity() {
        DatabaseHelper db = new DatabaseHelper(this);
        Level level = db.selectLevelById(levelId);
        int categoryId = level.getCategoryId();

        Intent intent = new Intent(this, LevelMenuActivity.class);
        intent.putExtra("categoryId", categoryId);//pass the category id in LevelActivity class
        startActivity(intent);
    }

    private Boolean moveCodeElement(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(null, shadowBuilder, view, 0);
            return true;
        } else {
            return false;
        }
    }

    private void addNewElementInCode(View view, DragEvent dragEvent) {

        putObjectSound();//sound

        Button draggedButton = (Button) dragEvent.getLocalState();
        Button copiedButton = new Button(GameActivity.this);

        //set new button parameter
        copiedButton.setLayoutParams(new ViewGroup.LayoutParams(draggedButton.getWidth(), draggedButton.getHeight()));//layout
        copiedButton.setCompoundDrawablesWithIntrinsicBounds(draggedButton.getCompoundDrawables()[0], null, null, null);//drawableLeft
        copiedButton.setBackground(draggedButton.getBackground());//background
        copiedButton.setText(draggedButton.getText());//text
        copiedButton.setTransformationMethod(draggedButton.getTransformationMethod());//textAllCaps
        copiedButton.setTextColor(draggedButton.getTextColors());//textColor
        copiedButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, draggedButton.getTextSize());//textSize
        copiedButton.setTag(true);//is from codeView

        draggedButton.performClick();//add command in executeCodeList
        codeView.addView(copiedButton);

        copiedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        //scroll down after add new element
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.codeScrollView));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //add string to executeCodeList when button is pressed
    public void addRight(View view) {
        executeCodeList.add("right");
    }

    public void addLeft(View view) {
        executeCodeList.add("left");
    }

    public void addUp(View view) {
        executeCodeList.add("up");
    }

    public void addDown(View view) {
        executeCodeList.add("down");
    }

    private void putObjectSound() {
        if (SetingsPreferencis.getSound(this)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.put_object);//sound
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    private void saveLevelInFirebase(int CategoryId,int unlockedLevel){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDB fdb = new FirebaseDB();
            fdb.saveUserLevel(CategoryId,unlockedLevel);
        }
    }
}
