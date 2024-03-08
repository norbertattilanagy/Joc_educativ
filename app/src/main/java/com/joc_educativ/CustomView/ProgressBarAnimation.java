package com.joc_educativ.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {
    private Context context;
    private ProgressBar progressBar;
    private TextView percentageText;
    private float from;
    private float to;
    private Button playButton;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView percentageText, float from, float to, Button playButton) {
        this.context = context;
        this.progressBar = progressBar;
        this.percentageText = percentageText;
        this.from = from;
        this.to = to;
        this.playButton = playButton;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to-from) * interpolatedTime;
        progressBar.setProgress((int) value);
        percentageText.setText((int) value + "%");

        if (value == to){
            playButton.setVisibility(View.VISIBLE);
        }
    }
}
