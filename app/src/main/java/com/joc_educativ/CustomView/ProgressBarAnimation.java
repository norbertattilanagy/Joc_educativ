package com.joc_educativ.CustomView;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.joc_educativ.NetworkConnection;

public class ProgressBarAnimation extends Animation {
    private Context context;
    private ProgressBar progressBar;
    private TextView percentageText;
    private float from;
    private float to;
    private Button playButton, logInButton;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView percentageText, float from, float to, Button playButton, Button logInButton) {
        this.context = context;
        this.progressBar = progressBar;
        this.percentageText = percentageText;
        this.from = from;
        this.to = to;
        this.playButton = playButton;
        this.logInButton = logInButton;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
        percentageText.setText((int) value + "%");

        if (value == to) {
            playButton.setVisibility(View.VISIBLE);
            if (NetworkConnection.isNetworkAvailable(context) && FirebaseAuth.getInstance().getCurrentUser() == null)
                logInButton.setVisibility(View.VISIBLE);
        }
    }
}
