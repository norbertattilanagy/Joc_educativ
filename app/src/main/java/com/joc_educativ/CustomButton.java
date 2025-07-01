package com.joc_educativ;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.joc_educativ.Game.MoveGameActivity;

public class CustomButton extends androidx.appcompat.widget.AppCompatButton{

    private String hideText;
    public CustomButton(Context context, AttributeSet attrs) {
        super(context,attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);

        hideText = a.getString(R.styleable.CustomButton_hideText);

        a.recycle();
    }

    public CustomButton(MoveGameActivity context) {
        super(context);
    }

    public String getHideText(){
        return hideText;
    }

    public void setHideText(String hideText) {
        this.hideText = hideText;
    }
}
