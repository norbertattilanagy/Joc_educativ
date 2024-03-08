package com.joc_educativ.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.joc_educativ.Database.DatabaseHelper;
import com.joc_educativ.Database.Level;
import com.joc_educativ.GameActivity;
import com.joc_educativ.R;

public class DrawGameView extends View {

    private Paint paint = new Paint();
    private View view;
    private Canvas canvas;
    private int levelId;
    private int wField, hField;
    private String[][] map;
    private int xFieldNr,yFieldNr;
    int xCar = -1, yCar = -1;
    Context context;

    public DrawGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        view = findViewById(R.id.gameView);
    }

    public void setLevelId(int levelId){
        this.levelId = levelId;
    }

    @Override
    public void onDraw(Canvas canv) {

        DatabaseHelper db = new DatabaseHelper(context);
        Level level = db.selectLevelById(levelId);//get level data

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
        drawMap(xCar, yCar);
    }

    public synchronized void redraw(int x, int y) {
        xCar = x;
        yCar = y;
        invalidate();
    }

    public void drawMap(int xCar, int yCar) {
        Drawable house = getResources().getDrawable(R.drawable.house);
        Drawable car = getResources().getDrawable(R.drawable.car);
        Drawable rock = getResources().getDrawable(R.drawable.rock);
        Drawable tree = getResources().getDrawable(R.drawable.tree);

        System.out.println("map="+map);
        //GameActivity gameActivity = new GameActivity();
        for (int i = 0; i < xFieldNr; i++) {
            for (int j = 0; j < yFieldNr; j++) {
                canvas.drawRect(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField, paint);

                if (map[j][i].equals("H")) {
                    house.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    house.draw(canvas);
                }

                if (map[j][i].equals("C") && xCar == -1) {
                    car.setBounds(i * wField, j * hField, (i + 1) * wField, (j + 1) * hField);
                    car.draw(canvas);
                    GameActivity.xCar = i;//save car start point
                    GameActivity.yCar = j;
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

        if (xCar != -1) {
            car.setBounds(xCar * wField, yCar * hField, (xCar + 1) * wField, (yCar + 1) * hField);
            car.draw(canvas);
        }
    }
}
