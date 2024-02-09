package com.joc_educativ.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.joc_educativ.GameActivity;
import com.joc_educativ.R;

public class DrawGameView extends View {

    private Paint paint = new Paint();
    private View view;
    private Canvas canvas;
    private int wField, hField;
    private String[][] map;
    int xCar = -1, yCar = -1;

    public DrawGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        view = findViewById(R.id.gameView);

    }

    @Override
    public void onDraw(Canvas canv) {

        wField = view.getWidth() / 8;
        hField = view.getHeight() / 5;

        Log.d("drawMap Redraw", "xCar: " + xCar + ", yCar: " + yCar);
        super.onDraw(canvas);
        /*paint.setColor(Color.BLACK);
        paint.setStrokeWidth(20);*/
        paint.setStyle(Paint.Style.STROKE);

        map = new String[][]{
                {"T", "R", "R", "X", "X", "X", "T", "T"},
                {"R", "R", "X", "X", "R", "X", "T", "T"},
                {"C", "X", "X", "T", "T", "X", "X", "H"},
                {"R", "R", "R", "T", "T", "T", "T", "T"},
                {"R", "R", "T", "T", "T", "T", "T", "T"}};

        canvas = canv;
        System.out.println(xCar);

        drawMap(xCar, yCar);
    }

    public synchronized void redraw(int x, int y) {
        Log.d("Before Redraw", "xCar: " + xCar + ", yCar: " + yCar);
        xCar = x;
        yCar = y;
        //Log.d("After Redraw", "xCar: " + xCar + ", yCar: " + yCar);
        invalidate();
    }

    public void drawMap(int xCar, int yCar) {
        Drawable house = getResources().getDrawable(R.drawable.house);
        Drawable car = getResources().getDrawable(R.drawable.car);
        Drawable rock = getResources().getDrawable(R.drawable.rock);
        Drawable tree = getResources().getDrawable(R.drawable.tree);


        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 5; j++) {
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
