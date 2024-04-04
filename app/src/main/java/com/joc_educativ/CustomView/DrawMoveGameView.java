package com.joc_educativ.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.joc_educativ.Database.Category;
import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.Level;
import com.joc_educativ.Game.MoveGameActivity;
import com.joc_educativ.R;

public class DrawMoveGameView extends View {

    private Paint paint = new Paint();
    private View view;
    private Canvas canvas;
    private int levelId;
    private int wField, hField;
    private String[][] map;
    private int xFieldNr, yFieldNr;
    int x = -1, y = -1;
    Boolean spotJump = false;
    Context context;

    public DrawMoveGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        view = findViewById(R.id.gameView);
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    @Override
    public void onDraw(Canvas canv) {

        DatabaseHelper db = new DatabaseHelper(context);
        Level level = db.selectLevelById(levelId);//get level data
        Category category = db.selectCategoryById(level.getCategoryId());//get category data

        xFieldNr = level.getMapXSize();
        yFieldNr = level.getMapYSize();

        wField = view.getWidth() / xFieldNr;
        hField = view.getHeight() / yFieldNr;

        super.onDraw(canvas);
        /*paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);*/
        paint.setStyle(Paint.Style.STROKE);

        map = level.getMap();

        canvas = canv;
        if (category.getCategory().equals("cars"))
            drawCarMap(x, y);
        else if (category.getCategory().equals("people")) {
            drawPeopleMap(x, y);
        }
    }

    public synchronized void redraw(int x, int y) {
        this.x = x;
        this.y = y;
        invalidate();
    }
    public synchronized void redraw(int x, int y, Boolean spotJump) {//jumps on the spot
        this.x = x;
        this.y = y;
        this.spotJump = spotJump;
        invalidate();
    }

    public void drawCarMap(int x, int y) {
        Drawable house = getResources().getDrawable(R.drawable.house);
        Drawable car = getResources().getDrawable(R.drawable.car);
        Drawable rock = getResources().getDrawable(R.drawable.rock);
        Drawable tree = getResources().getDrawable(R.drawable.tree);

        //GameActivity gameActivity = new GameActivity();
        for (int i = 0; i < xFieldNr; i++) {
            for (int j = 0; j < yFieldNr; j++) {
                canvas.drawRect(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField, paint);

                if (map[j][i].equals("H")) {
                    house.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    house.draw(canvas);
                }

                if (map[j][i].equals("C") && x == -1) {
                    car.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    car.draw(canvas);
                    MoveGameActivity.x = i;//save car start point
                    MoveGameActivity.y = j;
                }

                if (map[j][i].equals("R")) {
                    rock.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    rock.draw(canvas);
                }

                if (map[j][i].equals("T")) {
                    tree.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    tree.draw(canvas);
                }
            }
        }

        if (x != -1) {
            car.setBounds(x * wField, y * hField, (x + 1) * wField, (y + 1) * hField);
            car.draw(canvas);
        }
    }

    public void drawPeopleMap(int x, int y) {
        Drawable people = getResources().getDrawable(R.drawable.man);
        Drawable house = getResources().getDrawable(R.drawable.house);
        Drawable rock = getResources().getDrawable(R.drawable.rock);
        Drawable tree = getResources().getDrawable(R.drawable.tree);
        Drawable log = getResources().getDrawable(R.drawable.log_jump);
        Drawable people_run = getResources().getDrawable(R.drawable.run_man);
        Drawable peopleJumpLog = getResources().getDrawable(R.drawable.jump_man);
        Drawable key = getResources().getDrawable(R.drawable.key);


        for (int i = 0; i < xFieldNr; i++) {
            for (int j = 0; j < yFieldNr; j++) {
                canvas.drawRect(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField, paint);

                if (map[j][i].equals("H")) {
                    house.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    house.draw(canvas);
                }

                if (map[j][i].equals("P") && x == -1) {
                    people.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    people.draw(canvas);
                    MoveGameActivity.x = i;//save start point
                    MoveGameActivity.y = j;
                }

                if (map[j][i].equals("R")) {
                    rock.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    rock.draw(canvas);
                }

                if (map[j][i].equals("T")) {
                    tree.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    tree.draw(canvas);
                }
                if (map[j][i].equals("L") && (x != i || y != j)) {//and  people is not on log
                    log.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    log.draw(canvas);
                }

                if (map[j][i].equals("K")) {
                    key.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    key.draw(canvas);
                }
            }
        }

        if (x != -1) {
            if (map[y][x].equals("L")) {//jump on log
                peopleJumpLog.setBounds(x * wField, y * hField, (x + 1) * wField, (y + 1) * hField);
                peopleJumpLog.draw(canvas);
            } else if (spotJump) {//jumps on the spot
                people.setBounds(x * wField, y * hField, (x + 1) * wField, (y + 1) * hField);
                people.draw(canvas);
            } else {
                people_run.setBounds(x * wField, y * hField, (x + 1) * wField, (y + 1) * hField);
                people_run.draw(canvas);
            }
        }
    }
}
