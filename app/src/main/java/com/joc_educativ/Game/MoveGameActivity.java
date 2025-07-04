package com.joc_educativ.Game;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.joc_educativ.CustomButton;
import com.joc_educativ.CustomView.DrawMoveGameView;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.FirebaseDB;
import com.joc_educativ.Database.Level;
import com.joc_educativ.LevelMenuActivity;
import com.joc_educativ.R;
import com.joc_educativ.SetingsPreferencis;

import java.util.ArrayList;
import java.util.List;

public class MoveGameActivity extends AppCompatActivity {

    private View decorView;
    private DrawMoveGameView gameView;
    public LinearLayout codeView;
    ScrollView codeScrollView;
    HorizontalScrollView codeElementScroll, nrElementScroll, conditionElementScroll;
    ImageButton scrollLeftButton, scrollRightButton;
    ImageButton homeButton, replayButton, playButton, stopButton;
    CustomButton rightButton, leftButton, upButton, downButton, jumpButton, repeatButton, endRepeatButton, ifButton, endIfButton;
    CustomButton nr1Button, nr2Button, nr3Button, nr4Button, nr5Button, nr6Button, nr7Button, nr8Button, nr9Button;

    CustomButton logRightButton, logLeftButton, logUpButton, logDownButton;
    private int btnOrderInList = -1, btnOrderInCode = -1;
    private static int levelId;
    public static int x, y;
    public static Thread animationThread;
    public boolean isRunning = true;
    boolean gameOver = false;
    boolean isScrolling = false;
    private Handler scrollHandler = new Handler();
    List<String> executeCodeList = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove notch area
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }

        setContentView(R.layout.activity_move_game);

        decorView = getWindow().getDecorView();//hide system bars
        decorView.setSystemUiVisibility(hideSystemBars());

        Intent intent = getIntent();//get submitted level id
        levelId = intent.getIntExtra("levelId", -1);

        gameView = findViewById(R.id.gameView);
        codeView = findViewById(R.id.codeView);
        codeScrollView = findViewById(R.id.codeScrollView);
        codeElementScroll = findViewById(R.id.codeElementScroll);
        nrElementScroll = findViewById(R.id.nrElementScroll);
        conditionElementScroll = findViewById(R.id.conditionElementScroll);

        scrollLeftButton = findViewById(R.id.scrollLeftButton);
        scrollRightButton = findViewById(R.id.scrollRightButton);

        homeButton = findViewById(R.id.homeButton);
        replayButton = findViewById(R.id.replayButton);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        rightButton = findViewById(R.id.rightButton);
        leftButton = findViewById(R.id.leftButton);
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        jumpButton = findViewById(R.id.jumpButton);
        repeatButton = findViewById(R.id.repeatButton);
        endRepeatButton = findViewById(R.id.endRepeatButton);
        ifButton = findViewById(R.id.ifButton);
        endIfButton = findViewById(R.id.endIfButton);

        nr1Button = findViewById(R.id.nr1Button);
        nr2Button = findViewById(R.id.nr2Button);
        nr3Button = findViewById(R.id.nr3Button);
        nr4Button = findViewById(R.id.nr4Button);
        nr5Button = findViewById(R.id.nr5Button);
        nr6Button = findViewById(R.id.nr6Button);
        nr7Button = findViewById(R.id.nr7Button);
        nr8Button = findViewById(R.id.nr8Button);
        nr9Button = findViewById(R.id.nr9Button);

        logRightButton = findViewById(R.id.logRightButton);
        logLeftButton = findViewById(R.id.logLeftButton);
        logUpButton = findViewById(R.id.logUpButton);
        logDownButton = findViewById(R.id.logDownButton);

        gameView.setLevelId(levelId);

        setCodeElementVisibility(levelId);

        LinearLayout codeOutlineView = findViewById(R.id.codeOutlineView);
        codeOutlineView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {//get the real height of the LinearLayout
            @Override
            public void onGlobalLayout() {
                codeOutlineView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int realHeight = codeOutlineView.getHeight();
                codeView.setMinimumHeight(realHeight - 50);
            }
        });


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(MoveGameActivity.this);
                openLevelActivity();
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(MoveGameActivity.this);
                refreshActivity();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(MoveGameActivity.this);
                if (animationThread == null) {
                    isRunning = true;
                    playGame();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetingsPreferencis.playClickSound(MoveGameActivity.this);
                if (animationThread != null) {
                    isRunning = false;
                    animationThread = null;
                }
            }
        });

        scrollLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isScrolling = true;
                        scrollLeft();
                        break;
                    case MotionEvent.ACTION_UP:
                        isScrolling = false;
                        break;
                }
                return true;
            }
        });
        scrollRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("ACTION_DOWN=");
                        isScrolling = true;
                        scrollRight();
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("ACTION_UP=");
                        isScrolling = false;
                        break;
                }
                return true;
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

        jumpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        repeatButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        endRepeatButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        ifButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        endIfButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr1Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr2Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr3Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr4Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr5Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr6Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr7Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr8Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        nr9Button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });


        logRightButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });
        logLeftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });
        logUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });
        logDownButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        codeView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                CustomButton draggedButton = (CustomButton) dragEvent.getLocalState();

                //if draggedButton.getTag() != null then is from codeView
                if (dragEvent.getAction() == DragEvent.ACTION_DROP && draggedButton.getTag() == null) {
                    addNewElementInCode(view, dragEvent);
                }
                //remove element from code
                if (dragEvent.getAction() == DragEvent.ACTION_DRAG_EXITED && draggedButton.getTag() != null && codeView.getChildCount() > 0
                        && (nrElementScroll.getVisibility() == View.GONE || conditionElementScroll.getVisibility() == View.GONE ||
                        draggedButton.getText().equals(getString(R.string.repeat)) || draggedButton.getText().equals(getString(R.string.condition)))) {
                    if (draggedButton.getParent() instanceof ConstraintLayout) {
                        int locCodeView = codeView.indexOfChild((ConstraintLayout) draggedButton.getParent());
                        int loc = getRepeatLocationInCode(codeView.indexOfChild((ConstraintLayout) draggedButton.getParent()));//get repeat location in executeCodeList
                        if (draggedButton.getText().equals(getString(R.string.repeat)) || draggedButton.getText().equals(getString(R.string.condition))) {

                            //remove repeat/if button
                            ConstraintLayout constraintLayout = (ConstraintLayout) draggedButton.getParent();
                            if (constraintLayout.getChildCount() > 1) {
                                executeCodeList.remove(codeView.indexOfChild((ConstraintLayout) draggedButton.getParent()) + loc + 1);//remove the nr from for
                            }
                            executeCodeList.remove(codeView.indexOfChild((ConstraintLayout) draggedButton.getParent()) + loc);//remove for
                            codeView.removeView((ConstraintLayout) draggedButton.getParent());//remove for parent, ConstraintLayout, from code View

                            //remove end button
                            if (getEndIndex(0, 1) != -1) {//if exist end button
                                int repeatIndex = getEndIndex(0, 1);
                                codeView.removeView(codeView.getChildAt(getCodeViewIndex(repeatIndex)));
                                executeCodeList.remove(repeatIndex);
                            }

                            realign();//realign the button

                        } else if (draggedButton.getText().equals("1") || draggedButton.getText().equals("2") || draggedButton.getText().equals("3")
                                || draggedButton.getText().equals("4") || draggedButton.getText().equals("5") || draggedButton.getText().equals("6")
                                || draggedButton.getText().equals("7") || draggedButton.getText().equals("8") || draggedButton.getText().equals("9")
                                || "logRight".equals(draggedButton.getHideText()) || "logLeft".equals(draggedButton.getHideText())
                                || "logUp".equals(draggedButton.getHideText()) || "logDown".equals(draggedButton.getHideText())) {

                            btnOrderInList = codeView.indexOfChild((ConstraintLayout) draggedButton.getParent()) + loc + 1;
                            executeCodeList.remove(btnOrderInList);//remove the nr from executeCodeList
                            btnOrderInCode = codeView.indexOfChild((View) draggedButton.getParent());
                            ((ConstraintLayout) draggedButton.getParent()).removeView(draggedButton);//remove nr from codeView

                        }
                    } else if (draggedButton.getText().equals(getString(R.string.end_repeat))) {
                        removeAfter(draggedButton);//remove all element after dragged button
                        endRepeatButton.setVisibility(View.VISIBLE);
                    } else if (draggedButton.getText().equals(getString(R.string.end_condition))) {
                        removeAfter(draggedButton);//remove all element after dragged button
                        endIfButton.setVisibility(View.VISIBLE);
                    } else {
                        executeCodeList.remove(getExecuteCodeListIndex(codeView.indexOfChild(draggedButton)));
                        codeView.removeView(draggedButton);
                    }
                    putObjectSound();

                    if (draggedButton.getText().equals(getString(R.string.repeat))) {
                        if (getEndIndex(0, 0) != -1)
                            endRepeatButton.setVisibility(View.GONE);
                    } else if (draggedButton.getText().equals(getString(R.string.condition))) {
                        if (getEndIndex(0, 0) != -1)
                            endIfButton.setVisibility(View.GONE);
                    }

                    //if remove nr button
                    if (draggedButton.getText().equals("1") || draggedButton.getText().equals("2") || draggedButton.getText().equals("3")
                            || draggedButton.getText().equals("4") || draggedButton.getText().equals("5") || draggedButton.getText().equals("6")
                            || draggedButton.getText().equals("7") || draggedButton.getText().equals("8") || draggedButton.getText().equals("9")) {
                        changeScrollVisibility(1);
                    } else if ("logRight".equals(draggedButton.getHideText()) || "logLeft".equals(draggedButton.getHideText())
                            || "logUp".equals(draggedButton.getHideText()) || "logDown".equals(draggedButton.getHideText())) {
                        changeScrollVisibility(2);
                    } else {
                        changeScrollVisibility(0);
                    }
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

        animationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (x != -1)
                    reset();

                int j = 0;//code view
                final ScrollView scrollview = ((ScrollView) findViewById(R.id.codeScrollView));
                for (int i = 0; i < executeCodeList.size(); i++) {
                    final int currentIndex = i;
                    final CustomButton button;
                    final int yButton;
                    if (codeView.getChildAt(j) == null) {
                        break;
                    }

                    if (codeView.getChildAt(j) instanceof CustomButton) {
                        button = (CustomButton) codeView.getChildAt(j);//get  current code element
                        yButton = button.getTop() - button.getHeight();
                    } else {
                        ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(j);
                        button = (CustomButton) constraintLayout.getChildAt(0);
                        yButton = constraintLayout.getTop() - constraintLayout.getHeight();
                    }

                    //scroll in selected element
                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollview.smoothScrollTo(0, yButton);//view including the previous button
                        }
                    });

                    //set select background for code element
                    final int finalJ = j;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            codeElementSelect(button, executeCodeList.get(currentIndex));
                            int btnNr = 0;
                            int repeatNr = 0;//number from repeat
                            while (finalJ + btnNr < codeView.getChildCount() && (codeView.getChildAt(finalJ + btnNr).getLeft() > 0
                                    || codeView.getChildAt(finalJ + btnNr) instanceof ConstraintLayout)) {//if repeat button

                                if (codeView.getChildAt(finalJ + btnNr) instanceof ConstraintLayout) {
                                    ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(finalJ + btnNr);
                                    CustomButton btn = (CustomButton) constraintLayout.getChildAt(0);
                                    codeElementSelect(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                                    repeatNr++;
                                } else {
                                    CustomButton btn = (CustomButton) codeView.getChildAt(finalJ + btnNr);
                                    codeElementSelect(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                                }
                                btnNr++;
                            }
                            if (codeView.getChildAt(finalJ + btnNr) instanceof ConstraintLayout) {
                                ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(finalJ + btnNr);
                                CustomButton btn = (CustomButton) constraintLayout.getChildAt(0);
                                codeElementSelect(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                            } else if (currentIndex + btnNr + repeatNr < executeCodeList.size()) {//if not OutOfBandsException
                                CustomButton btn = (CustomButton) codeView.getChildAt(finalJ + btnNr);
                                codeElementSelect(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                            }
                        }
                    });

                    //go to the car
                    switch (executeCodeList.get(i)) {
                        case "right":
                            right(level.getMap());
                            break;
                        case "left":
                            left();
                            break;
                        case "up":
                            up();
                            break;
                        case "down":
                            down(level.getMap());
                            break;
                        case "jump":
                            jump(level.getMap());
                            break;
                        case "repeat":
                            gameOver = repeat(Integer.parseInt(executeCodeList.get(i + 1)), i, level);
                            if (getEndIndex(i, 0) > 0)
                                i = getEndIndex(i, 0);
                            else
                                i = executeCodeList.size() - 1;
                            j = getCodeViewIndex(i);
                            break;
                        case "if":
                            gameOver = condition(i, level);
                            if (getEndIndex(i, 0) > 0)
                                i = getEndIndex(i, 0);
                            else
                                i = executeCodeList.size() - 1;
                            j = getCodeViewIndex(i);
                            break;
                    }

                    moveAndWait(500);

                    if (gameOver || ifGameOver(level, i)) {//game over
                        isRunning = false;
                        animationThread = null;
                        gameOver(MoveGameActivity.this);
                    } else if (level.getMap()[y][x].equals("H")) {//completed
                        isRunning = false;
                        animationThread = null;
                        completed(MoveGameActivity.this);
                    }

                    //set default background for code element
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            codeElementDefault(button, executeCodeList.get(currentIndex));
                            int btnNr = 0;
                            int repeatNr = 0;//number from repeat
                            while (finalJ + btnNr < codeView.getChildCount() && (codeView.getChildAt(finalJ + btnNr).getLeft() > 0 || codeView.getChildAt(finalJ + btnNr) instanceof ConstraintLayout)) {//if repeat button
                                if (codeView.getChildAt(finalJ + btnNr) instanceof ConstraintLayout) {
                                    ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(finalJ + btnNr);
                                    CustomButton btn = (CustomButton) constraintLayout.getChildAt(0);
                                    codeElementDefault(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                                    repeatNr++;
                                } else {
                                    CustomButton btn = (CustomButton) codeView.getChildAt(finalJ + btnNr);
                                    codeElementDefault(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                                }
                                btnNr++;
                            }
                            if (codeView.getChildAt(finalJ + btnNr) instanceof ConstraintLayout) {
                                ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(finalJ + btnNr);
                                CustomButton btn = (CustomButton) constraintLayout.getChildAt(0);
                                codeElementDefault(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                            } else if (currentIndex + btnNr + repeatNr < executeCodeList.size()) {//if not OutOfBandsException
                                CustomButton btn = (CustomButton) codeView.getChildAt(finalJ + btnNr);
                                codeElementDefault(btn, executeCodeList.get(currentIndex + btnNr + repeatNr));
                            }

                        }
                    });

                    if (!isRunning) {
                        break;
                    }
                    j++;//increment the codeView
                }
                isRunning = !isRunning;
                runOnUiThread(new Runnable() {//reset thread
                    @Override
                    public void run() {
                        stopButton.performClick();
                    }
                });
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

    private void right(String[][] map) {
        if (map[0].length > x + 1) {
            x++;
            gameView.redraw(x, y);
        }
    }

    private void left() {
        if (x > 0) {
            x--;
            gameView.redraw(x, y);
        }
    }

    private void down(String[][] map) {
        if (map.length > y + 1) {
            y++;
            gameView.redraw(x, y);
        }
    }

    private void up() {
        if (y > 0) {
            y--;
            gameView.redraw(x, y);
        }
    }

    private void jump(String[][] map) {
        Boolean jump = false;
        if (x + 1 < map[y].length && map[y][x + 1].equals("L")) {
            x++;
        } else if (x > 0 && map[y][x - 1].equals("L")) {
            x--;
        } else if (y + 1 < map.length && map[y + 1][x].equals("L")) {
            y++;
        } else if (y > 0 && map[y - 1][x].equals("L")) {
            y--;
        } else {//jumps on the spot
            jump = true;
        }
        gameView.redraw(x, y, jump);
    }

    private boolean verifyIfLog(String[][] map, String condition) {
        if (x + 1 < map[y].length && map[y][x + 1].equals("L") && condition.equals("logRight")) {
            return true;
        } else if (x > 0 && map[y][x - 1].equals("L") && condition.equals("logLeft")) {
            return true;
        } else if (y + 1 < map.length && map[y + 1][x].equals("L") && condition.equals("logDown")) {
            return true;
        } else if (y > 0 && map[y - 1][x].equals("L") && condition.equals("logUp")) {
            return true;
        }
        return false;
    }

    private boolean repeat(int n, int startIndex, Level level) {
        startIndex += 2;
        for (int j = 0; j < n; j++) {
            int index = startIndex;
            while (index < executeCodeList.size() && !executeCodeList.get(index).equals("endRepeat")) {
                switch (executeCodeList.get(index)) {
                    case "right":
                        right(level.getMap());
                        break;
                    case "left":
                        left();
                        break;
                    case "up":
                        up();
                        break;
                    case "down":
                        down(level.getMap());
                        break;
                    case "jump":
                        jump(level.getMap());
                        break;
                    case "repeat":
                        if (repeat(Integer.parseInt(executeCodeList.get(index + 1)), index, level))
                            return true;
                        index = getEndIndex(index, 0);
                        if (index == -1)
                            index = executeCodeList.size() - 1;
                        break;
                    case "if":
                        if (condition(index, level)) {
                            return true;
                        }
                        index = getEndIndex(index, 0);
                        break;
                }
                moveAndWait(500);
                if (ifGameOver(level, index)) {
                    return true;
                }
                index++;
                if (!isRunning)//press stop button
                    return false;
            }
        }
        return false;
    }

    private boolean condition(int startIndex, Level level) {
        String cond = executeCodeList.get(startIndex + 1);
        startIndex += 2;
        Boolean addedEndIf = false;

        if (verifyIfLog(level.getMap(), cond)) {//verify condition
            while (startIndex < executeCodeList.size() && !executeCodeList.get(startIndex).equals("endIf")) {
                switch (executeCodeList.get(startIndex)) {
                    case "right":
                        right(level.getMap());
                        break;
                    case "left":
                        left();
                        break;
                    case "up":
                        up();
                        break;
                    case "down":
                        down(level.getMap());
                        break;
                    case "jump":
                        jump(level.getMap());
                        break;
                    case "repeat":
                        if (repeat(Integer.parseInt(executeCodeList.get(startIndex + 1)), startIndex, level))
                            return true;
                        startIndex = getEndIndex(startIndex, 0);
                        if (startIndex == -1)
                            startIndex = executeCodeList.size() - 1;
                        break;
                    case "if":
                        if (condition(startIndex, level))
                            return true;
                        startIndex = getEndIndex(startIndex, 0);
                        break;
                }
                moveAndWait(500);
                if (ifGameOver(level, startIndex)) {
                    return true;
                }

                if (startIndex + 1 >= executeCodeList.size()) {//not add endIf button
                    executeCodeList.add("endIf");
                    addedEndIf = true;
                }
                startIndex++;

                if (!isRunning)//press stop button
                    return false;
            }
            if (addedEndIf){
                executeCodeList.remove(executeCodeList.size()-1);
            }
        }
        return false;
    }

    private void reset() {
        gameView.redraw(-1, -1);
        moveAndWait(1000);
    }

    //set select background for code element
    private void codeElementSelect(CustomButton button, String action) {
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
            case "jump":
                button.setBackgroundResource(R.drawable.code_jump_element_selected);
                break;
            case "repeat":
                button.setBackgroundResource(R.drawable.code_for_element_selected);
                break;
            case "endRepeat":
                button.setBackgroundResource(R.drawable.code_for_element_selected);
                break;
            case "if":
                button.setBackgroundResource(R.drawable.code_if_element_selected);
                break;
            case "endIf":
                button.setBackgroundResource(R.drawable.code_if_element_selected);
        }
    }

    //set default background for code element
    private void codeElementDefault(CustomButton button, String action) {
        /*CustomButton btn = (CustomButton) codeView.getChildAt(codeView.getChildCount() - 1);
        System.out.println("exx="+btn.getText());
        if (btn.getText().equals(getString(R.string.end_condition)) || !action.equals("endIf")) {//not add endIf button in codeView*/
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
            case "jump":
                button.setBackgroundResource(R.drawable.code_jump_element_default);
                break;
            case "repeat":
                button.setBackgroundResource(R.drawable.code_for_element_default);
                break;
            case "endRepeat":
                button.setBackgroundResource(R.drawable.code_for_element_default);
                break;
            case "if":
                button.setBackgroundResource(R.drawable.code_if_element_default);
                break;
            case "endIf":
                button.setBackgroundResource(R.drawable.code_if_element_default);
        }
        /*} else {
            executeCodeList.remove(executeCodeList.size()-1);
            System.out.println("exx="+executeCodeList);
        }*/
    }

    private void setCodeElementVisibility(int levelId) {//set visible the code button
        DatabaseHelper db = new DatabaseHelper(this);
        Level level = db.selectLevelById(levelId);//get level data
        String codeElement = level.getCodeElement();
        String code;

        while (!codeElement.equals("")) {

            if (codeElement.contains(";"))//select code substring
                code = codeElement.substring(0, codeElement.indexOf(';'));
            else
                code = codeElement;

            codeElement = codeElement.substring(codeElement.indexOf(';') + 1);//remove code substring

            switch (code) {
                case "right":
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case "left":
                    leftButton.setVisibility(View.VISIBLE);
                    break;
                case "up":
                    upButton.setVisibility(View.VISIBLE);
                    break;
                case "down":
                    downButton.setVisibility(View.VISIBLE);
                    break;
                case "jump":
                    jumpButton.setVisibility(View.VISIBLE);
                    break;
                case "repeat":
                    repeatButton.setVisibility(View.VISIBLE);
                    break;
                case "if":
                    ifButton.setVisibility(View.VISIBLE);
                    break;
                case "nr1":
                    nr1Button.setVisibility(View.VISIBLE);
                    break;
                case "nr2":
                    nr2Button.setVisibility(View.VISIBLE);
                    break;
                case "nr3":
                    nr3Button.setVisibility(View.VISIBLE);
                    break;
                case "nr4":
                    nr4Button.setVisibility(View.VISIBLE);
                    break;
                case "nr5":
                    nr5Button.setVisibility(View.VISIBLE);
                    break;
                case "nr6":
                    nr6Button.setVisibility(View.VISIBLE);
                    break;
                case "nr7":
                    nr7Button.setVisibility(View.VISIBLE);
                    break;
                case "nr8":
                    nr8Button.setVisibility(View.VISIBLE);
                    break;
                case "nr9":
                    nr9Button.setVisibility(View.VISIBLE);
                    break;
                case "logRight":
                    logRightButton.setVisibility(View.VISIBLE);
                    break;
                case "logLeft":
                    logLeftButton.setVisibility(View.VISIBLE);
                    break;
                case "logUp":
                    logUpButton.setVisibility(View.VISIBLE);
                    break;
                case "logDown":
                    logDownButton.setVisibility(View.VISIBLE);
            }
        }

        changeScrollVisibility(0);
    }

    private void changeScrollVisibility(int codeListVisibility) {
        codeElementScroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener so it doesn't get called multiple times
                codeElementScroll.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                if (codeListVisibility == 0) {
                    nrElementScroll.setVisibility(View.GONE);
                    conditionElementScroll.setVisibility(View.GONE);
                    codeElementScroll.setVisibility(View.VISIBLE);

                    if (codeElementScroll.getChildAt(0).getWidth() > codeElementScroll.getWidth()) {//scroll button visibility
                        scrollLeftButton.setVisibility(View.VISIBLE);
                        scrollRightButton.setVisibility(View.VISIBLE);
                    } else {
                        scrollLeftButton.setVisibility(View.GONE);
                        scrollRightButton.setVisibility(View.GONE);
                    }
                } else if (codeListVisibility == 1) {

                    nrElementScroll.setVisibility(View.VISIBLE);
                    conditionElementScroll.setVisibility(View.GONE);
                    codeElementScroll.setVisibility(View.INVISIBLE);

                    if (nrElementScroll.getChildAt(0).getWidth() > nrElementScroll.getWidth()) {//scroll button visibility
                        scrollLeftButton.setVisibility(View.VISIBLE);
                        scrollRightButton.setVisibility(View.VISIBLE);
                    } else {
                        scrollLeftButton.setVisibility(View.GONE);
                        scrollRightButton.setVisibility(View.GONE);
                    }
                } else if (codeListVisibility == 2) {
                    nrElementScroll.setVisibility(View.GONE);
                    conditionElementScroll.setVisibility(View.VISIBLE);
                    codeElementScroll.setVisibility(View.INVISIBLE);

                    if (conditionElementScroll.getChildAt(0).getWidth() > conditionElementScroll.getWidth()) {//scroll button visibility
                        scrollLeftButton.setVisibility(View.VISIBLE);
                        scrollRightButton.setVisibility(View.VISIBLE);
                    } else {
                        scrollLeftButton.setVisibility(View.GONE);
                        scrollRightButton.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private boolean ifGameOver(Level level, int index) {
        if (level.getMap()[y][x].equals("R")
                || level.getMap()[y][x].equals("T")
                || (level.getMap()[y][x].equals("L") && !executeCodeList.get(index).equals("jump")
                && !executeCodeList.get(index).equals("if") && !executeCodeList.get(index).equals("endIf")
                && !executeCodeList.get(index).equals("repeat") && !executeCodeList.get(index).equals("endRepeat"))) {
            return true;
        }
        return false;
    }

    public void gameOver(Context context) {

        if (SetingsPreferencis.getVibration(context)) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

        /*if (SetingsPreferencis.getSound(context)) {
            DatabaseHelper db = new DatabaseHelper(context);
            Level level = db.selectLevelById(levelId);//get level data
            Category category = db.selectCategoryById(level.getCategoryId());

            if (category.getCategory().equals("cars")) {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.crash);
                mediaPlayer.seekTo(0); // Rewind sound to beginning
                mediaPlayer.start();
            }
        }*/

        runOnUiThread(new Runnable() {//verify game over / completed game
            @Override
            public void run() {
                Dialog dialog = new Dialog(context, R.style.CustomDialog);
                dialog.setContentView(R.layout.game_over_dialog);
                dialog.setCancelable(false);

                if (dialog.getWindow() != null) {//hide system bars
                    dialog.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }

                Button retryButton = dialog.findViewById(R.id.yesButton);
                Button homeButton = dialog.findViewById(R.id.homeButton);

                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        //refreshActivity();
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
        });
    }

    private void completed(Context context) {
        runOnUiThread(new Runnable() {//verify game over / completed game
            @Override
            public void run() {
                Dialog dialog = new Dialog(context, R.style.CustomDialog);
                dialog.setContentView(R.layout.completed_dialog);
                dialog.setCancelable(false);

                if (dialog.getWindow() != null) {//hide system bars
                    dialog.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }

                Button retryButton = dialog.findViewById(R.id.replayButton);
                Button nextLeveButton = dialog.findViewById(R.id.nextLevelButton);
                Button homeButton = dialog.findViewById(R.id.homeButton);

                if (verifyNextLevel() == -1)//if exist next level
                    nextLeveButton.setVisibility(View.GONE);
                else {
                    DatabaseHelper db = new DatabaseHelper(MoveGameActivity.this);//save next unlocked level
                    Level level = db.selectLevelById(levelId);
                    int unlockedLevel = db.selectUnlockedLevel(level.getCategoryId());//select unlocked level nr

                    if (verifyNextLevel() > unlockedLevel) {
                        db.updateUnlockedLevel(level.getCategoryId(), unlockedLevel + 1);
                        saveLevelInFirebase(level.getCategoryId(), unlockedLevel + 1);
                    }
                }

                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        refreshActivity();
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
        });

    }

    private int verifyNextLevel() {
        DatabaseHelper db = new DatabaseHelper(this);
        Level level = db.selectLevelById(levelId);//get level data
        return db.selectNextLevelId(level.getCategoryId(), level.getLevel() + 1);//return next level Id
    }

    public void refreshActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void openNextLevel() {
        Intent intent = new Intent(this, MoveGameActivity.class);
        intent.putExtra("levelId", verifyNextLevel());//pass the category id in LevelActivity class
        this.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void openLevelActivity() {
        DatabaseHelper db = new DatabaseHelper(this);
        Level level = db.selectLevelById(levelId);
        int categoryId = level.getCategoryId();

        Intent intent = new Intent(this, LevelMenuActivity.class);
        intent.putExtra("categoryId", categoryId);//pass the category id in LevelActivity class
        startActivity(intent);
        overridePendingTransition(0, 0);
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

    private void addNewElementInCode(View view, DragEvent dragEvent) {//code view

        putObjectSound();//sound

        CustomButton draggedButton = (CustomButton) dragEvent.getLocalState();
        CustomButton copiedButton = new CustomButton(MoveGameActivity.this);

        Drawable background = draggedButton.getBackground().getConstantState().newDrawable();//get dragged button background
        //set new button parameter
        copiedButton.setLayoutParams(new ViewGroup.LayoutParams(draggedButton.getWidth(), draggedButton.getHeight()));//layout
        copiedButton.setCompoundDrawablesWithIntrinsicBounds(draggedButton.getCompoundDrawables()[0], null, draggedButton.getCompoundDrawables()[2], null);//drawableLeft
        copiedButton.setPadding(0, 0, draggedButton.getPaddingRight(), 0);//padding
        copiedButton.setBackground(background);//background
        copiedButton.setText(draggedButton.getText());//text
        copiedButton.setTransformationMethod(draggedButton.getTransformationMethod());//textAllCaps
        copiedButton.setTextColor(draggedButton.getTextColors());//textColor
        copiedButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, draggedButton.getTextSize());//textSize
        copiedButton.setHideText(draggedButton.getHideText());
        copiedButton.setTag(true);//is from codeView

        if (draggedButton.getText().equals("1") || draggedButton.getText().equals("2") || draggedButton.getText().equals("3")
                || draggedButton.getText().equals("4") || draggedButton.getText().equals("5") || draggedButton.getText().equals("6")
                || draggedButton.getText().equals("7") || draggedButton.getText().equals("8") || draggedButton.getText().equals("9")) {
            CustomButton repeatButton;
            if (btnOrderInList == -1) {
                repeatButton = getLastButton(codeView);
            } else {
                ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(btnOrderInCode);
                repeatButton = (CustomButton) constraintLayout.getChildAt(0);
            }
            placeNrButtonOnRepeatButton(repeatButton, copiedButton);
        } else if ("logRight".equals(draggedButton.getHideText()) || "logLeft".equals(draggedButton.getHideText())
                || "logUp".equals(draggedButton.getHideText()) || "logDown".equals(draggedButton.getHideText())) {
            CustomButton ifButton;
            if (btnOrderInList == -1) {
                ifButton = getLastButton(codeView);
            } else {
                ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(btnOrderInCode);
                ifButton = (CustomButton) constraintLayout.getChildAt(0);
            }
            placeButtonOnIfButton(ifButton, copiedButton);
        } else {
            codeView.addView(copiedButton);
        }

        realign();

        if (btnOrderInList != -1) {
            if (draggedButton.getText().equals("1") || draggedButton.getText().equals("2") || draggedButton.getText().equals("3")
                    || draggedButton.getText().equals("4") || draggedButton.getText().equals("5") || draggedButton.getText().equals("6")
                    || draggedButton.getText().equals("7") || draggedButton.getText().equals("8") || draggedButton.getText().equals("9")) {
                addNr(draggedButton, btnOrderInList);
            } else {//if button
                addCondition(draggedButton, btnOrderInList);
            }
        } else {
            draggedButton.performClick();//add command in executeCodeList
        }

        copiedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return moveCodeElement(view, motionEvent);
            }
        });

        if (draggedButton.getText().equals(getString(R.string.repeat))) {
            endRepeatButton.setVisibility(View.VISIBLE);
        } else if (draggedButton.getText().equals(getString(R.string.end_repeat))) {
            if (getEndIndex(0, 0) != -1)
                endRepeatButton.setVisibility(View.GONE);
        } else if (draggedButton.getText().equals(getString(R.string.condition))) {
            endIfButton.setVisibility(View.VISIBLE);
        } else if (draggedButton.getText().equals(getString(R.string.end_condition))) {
            if (getEndIndex(0, 0) != -1)
                endIfButton.setVisibility(View.GONE);
        }

        if (draggedButton.getText().equals(getString(R.string.repeat))) {
            changeScrollVisibility(1);
        } else if (draggedButton.getText().equals(getString(R.string.condition))) {
            changeScrollVisibility(2);
        } else {
            changeScrollVisibility(0);
        }

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

    public void addJump(View view) {
        executeCodeList.add("jump");
    }

    public void addRepeat(View view) {
        executeCodeList.add("repeat");
    }

    public void addEndRepeat(View view) {
        executeCodeList.add("endRepeat");
    }

    public void addNr(View view) {
        CustomButton button = (CustomButton) view;
        executeCodeList.add(button.getText().toString());
    }

    public void addIf(View view) {
        executeCodeList.add("if");
    }

    public void addEndIf(View view) {
        executeCodeList.add("endIf");
    }

    public void addLogRight(View view) {
        executeCodeList.add("logRight");
    }

    public void addLogLeft(View view) {
        executeCodeList.add("logLeft");
    }

    public void addLogUp(View view) {
        executeCodeList.add("logUp");
    }

    public void addLogDown(View view) {
        executeCodeList.add("logDown");
    }

    public void addNr(View view, int order) {
        CustomButton button = (CustomButton) view;
        if (order < executeCodeList.size()) {//if put in last position
            executeCodeList.add(executeCodeList.get(executeCodeList.size() - 1));//set last position
            for (int i = executeCodeList.size() - 2; i >= order; i--)
                executeCodeList.set(i + 1, executeCodeList.get(i));//move 1 position
            executeCodeList.set(order, button.getText().toString());//add nr
        } else {
            executeCodeList.add(button.getText().toString());
        }
        btnOrderInList = -1;
        btnOrderInCode = -1;
    }

    public void addCondition(View view, int order) {
        CustomButton button = (CustomButton) view;
        if (order < executeCodeList.size()) {//if put in last position
            executeCodeList.add(executeCodeList.get(executeCodeList.size() - 1));//set last position
            for (int i = executeCodeList.size() - 2; i >= order; i--)
                executeCodeList.set(i + 1, executeCodeList.get(i));//move 1 position

            executeCodeList.set(order, getExecuteCodeFromButton(button));
        } else {
            executeCodeList.add(getExecuteCodeFromButton(button));
        }
        btnOrderInList = -1;
        btnOrderInCode = -1;
    }

    private String getExecuteCodeFromButton(CustomButton button) {//return execute code list text
        String text = "";
        if (button.getHideText().equals("logRight")) {
            text = "logRight";
        } else if (button.getHideText().equals("logLeft")) {
            text = "logLeft";
        } else if (button.getHideText().equals("logUp")) {
            text = "logUp";
        } else if (button.getHideText().equals("logDown")) {
            text = "logDown";
        }
        return text;
    }

    private CustomButton getLastButton(LinearLayout view) {
        CustomButton lastButton = null;
        int childCount = view.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = view.getChildAt(i);
            if (childView instanceof CustomButton) {
                CustomButton button = (CustomButton) childView;
                if (button.getTag() != null && (boolean) button.getTag()) {
                    lastButton = button;
                    break;
                }
            }
        }
        return lastButton;
    }

    private int getRepeatLocationInCode(int btnOrder) {
        int nr = 0;
        for (int i = 0; i < btnOrder; i++) {
            if (codeView.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout constraintLayout = (ConstraintLayout) codeView.getChildAt(i);
                CustomButton btn = (CustomButton) constraintLayout.getChildAt(0);
                if (btn.getText().equals(getString(R.string.repeat))) {
                    nr++;
                }
            }
        }
        return nr;
    }

    private int getEndIndex(int start, int repeatNr) {
        int index = -1;
        for (int i = start; i < executeCodeList.size(); i++) {
            if (executeCodeList.get(i).equals("repeat") || executeCodeList.get(i).equals("if"))
                repeatNr++;
            else if (executeCodeList.get(i).equals("endRepeat") || executeCodeList.get(i).equals("endIf")) {
                repeatNr--;
            }
            if (repeatNr == 0) {
                index = i;
                break;
            }
        }
        if (repeatNr == 0 && index == -1)//-2 not exist repeat; -1 not close repeat
            index = -2;
        else if (index == -1)
            index = executeCodeList.size()-1;//last position

        return index;
    }

    private int getCodeViewIndex(int i) {
        for (int j = i; j >= 0; j--) {
            if (executeCodeList.get(j).equals("repeat") || executeCodeList.get(j).equals("if")) {
                j--;
                i--;
            }
        }
        return i;
    }

    private int getExecuteCodeListIndex(int n) {
        int nr = 0;
        for (int i = 0; i < n; i++) {
            if (executeCodeList.get(i).equals("repeat") || executeCodeList.get(i).equals("if"))
                nr++;
        }
        return nr + n;
    }

    private void realign() {
        int nrSpace = 0;
        for (int j = 0; j < codeView.getChildCount(); j++) {
            if (codeView.getChildAt(j) instanceof CustomButton) {
                CustomButton btn = (CustomButton) codeView.getChildAt(j);
                if (btn.getText().equals(getString(R.string.end_repeat)) || btn.getText().equals(getString(R.string.end_condition))) {
                    nrSpace--;
                }
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) codeView.getChildAt(j).getLayoutParams();
            layoutParams.leftMargin = (int) (nrSpace * 40 * getResources().getDisplayMetrics().density + 0.5f);
            codeView.getChildAt(j).setLayoutParams(layoutParams);

            if (codeView.getChildAt(j) instanceof ConstraintLayout) {//start repeat/if
                nrSpace++;
            }
        }
    }

    private void removeAfter(CustomButton btn) {
        int start = codeView.indexOfChild(btn);
        for (int i = codeView.getChildCount() - 1; i >= start; i--) {
            if (codeView.getChildAt(i) instanceof CustomButton) {//if remove end button
                CustomButton button = (CustomButton) codeView.getChildAt(i);
                if (button.getText().equals(getString(R.string.end_repeat))) {
                    endRepeatButton.setVisibility(View.VISIBLE);
                } else if (button.getText().equals(getString(R.string.end_condition))) {
                    endIfButton.setVisibility(View.VISIBLE);
                }
            }

            if (executeCodeList.get(i).equals("repeat") || executeCodeList.get(i).equals("if"))
                executeCodeList.remove(getExecuteCodeListIndex(i));//remove nr or condition

            executeCodeList.remove(getExecuteCodeListIndex(i));
            codeView.removeViewAt(i);
        }
    }

    private void placeNrButtonOnRepeatButton(CustomButton repeatButton, CustomButton nrButton) {

        float dp = getResources().getDisplayMetrics().density;

        ConstraintLayout constraintLayout;//create ConstraintLayout

        if (btnOrderInList == -1) {
            constraintLayout = new ConstraintLayout(this);
            constraintLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            codeView.removeView(repeatButton);//remove from linearLayout
            codeView.addView(constraintLayout);//add in constraintLayout

            repeatButton.setId(ViewCompat.generateViewId());//set random id for ConstraintSet
            constraintLayout.addView(repeatButton);//add buttons in constraintLayout

            // set left margin +40dp than the previous
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = (int) repeatButton.getLeft();
            constraintLayout.setLayoutParams(layoutParams);
        } else {
            constraintLayout = (ConstraintLayout) repeatButton.getParent();
        }

        nrButton.setId(ViewCompat.generateViewId());//set random id for ConstraintSet
        constraintLayout.addView(nrButton);//add buttons in constraintLayout

        //set nrButton size and margins end
        ConstraintLayout.LayoutParams nrParams = new ConstraintLayout.LayoutParams(Math.round(36 * dp), Math.round(36 * dp));
        nrParams.setMarginEnd(Math.round(8 * dp));
        nrButton.setLayoutParams(nrParams);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        //nrButton constraints
        constraintSet.connect(nrButton.getId(), ConstraintSet.END, repeatButton.getId(), ConstraintSet.END);
        constraintSet.connect(nrButton.getId(), ConstraintSet.TOP, repeatButton.getId(), ConstraintSet.TOP);
        constraintSet.connect(nrButton.getId(), ConstraintSet.BOTTOM, repeatButton.getId(), ConstraintSet.BOTTOM);
        constraintSet.setVerticalBias(nrButton.getId(), 0.5f);//centered vertically
        constraintSet.applyTo(constraintLayout);
    }

    private void placeButtonOnIfButton(CustomButton ifButton, CustomButton conditionButton) {
        float dp = getResources().getDisplayMetrics().density;
        ConstraintLayout constraintLayout;//create ConstraintLayout

        if (btnOrderInList == -1) {//add ifButton in ConstraintLayout
            constraintLayout = new ConstraintLayout(this);
            constraintLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            codeView.removeView(ifButton);//remove from linearLayout
            codeView.addView(constraintLayout);//add in constraintLayout

            ifButton.setId(ViewCompat.generateViewId());//set random id for ConstraintSet
            constraintLayout.addView(ifButton);//add buttons in constraintLayout

            // set left margin +40dp than the previous
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = (int) ifButton.getLeft();
            constraintLayout.setLayoutParams(layoutParams);
        } else { //get ConstraintLayout
            constraintLayout = (ConstraintLayout) ifButton.getParent();
        }

        conditionButton.setId(ViewCompat.generateViewId());//set random id for ConstraintSet

        //resize icon from condition button
        Drawable drawable;
        if (conditionButton.getHideText().equals("logRight")) {
            drawable = ContextCompat.getDrawable(this, R.drawable.log_right_icon_48);
        } else if (conditionButton.getHideText().equals("logLeft")) {
            drawable = ContextCompat.getDrawable(this, R.drawable.log_left_icon_48);
        } else if (conditionButton.getHideText().equals("logUp")) {
            drawable = ContextCompat.getDrawable(this, R.drawable.log_up_icon_48);
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.log_down_icon_48);
        }
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics()); // resize 36dp
        drawable.setBounds(0, 0, 2 * size, size);
        conditionButton.setCompoundDrawables(drawable, null, null, null);

        constraintLayout.addView(conditionButton);//add buttons in constraintLayout

        //set conditionButton size and margins end
        ConstraintLayout.LayoutParams conditionParams = new ConstraintLayout.LayoutParams(Math.round(72 * dp), Math.round(36 * dp));
        conditionParams.setMarginEnd(Math.round(8 * dp));
        conditionButton.setLayoutParams(conditionParams);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        //conditionButton constraints
        constraintSet.connect(conditionButton.getId(), ConstraintSet.END, ifButton.getId(), ConstraintSet.END);
        constraintSet.connect(conditionButton.getId(), ConstraintSet.TOP, ifButton.getId(), ConstraintSet.TOP);
        constraintSet.connect(conditionButton.getId(), ConstraintSet.BOTTOM, ifButton.getId(), ConstraintSet.BOTTOM);
        constraintSet.setVerticalBias(conditionButton.getId(), 0.5f);//centered vertically
        constraintSet.applyTo(constraintLayout);
    }

    private void putObjectSound() {
        if (SetingsPreferencis.getSound(this)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.put_object);//sound
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    private void saveLevelInFirebase(int CategoryId, int unlockedLevel) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseDB fdb = new FirebaseDB();
            fdb.saveUserLevel(CategoryId, unlockedLevel);
        }
    }

    private void scrollLeft() {
        scrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScrolling) {
                    if (codeElementScroll.getVisibility() == View.VISIBLE) {
                        codeElementScroll.scrollBy(-10, 0);
                    } else if (nrElementScroll.getVisibility() == View.VISIBLE) {
                        nrElementScroll.scrollBy(-10, 0);
                    } else if (codeElementScroll.getVisibility() == View.VISIBLE) {
                        codeElementScroll.scrollBy(-10, 0);
                    }
                    scrollHandler.postDelayed(this, 5);//repeat scrolling
                }
            }
        }, 10);//delay the initial scrolling
    }

    private void scrollRight() {
        scrollHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isScrolling) {
                    if (codeElementScroll.getVisibility() == View.VISIBLE) {
                        codeElementScroll.scrollBy(10, 0);
                    } else if (nrElementScroll.getVisibility() == View.VISIBLE) {
                        nrElementScroll.scrollBy(10, 0);
                    } else if (codeElementScroll.getVisibility() == View.VISIBLE) {
                        codeElementScroll.scrollBy(10, 0);
                    }
                    scrollHandler.postDelayed(this, 5);//repeat scrolling
                }
            }
        }, 10);//delay the initial scrolling
    }
}
