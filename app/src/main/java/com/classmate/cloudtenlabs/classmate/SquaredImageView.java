package com.classmate.cloudtenlabs.classmate;

import android.support.v7.widget.AppCompatImageView;
import android.widget.ImageView;
import android.content.Context;
import android.util.AttributeSet;

public class SquaredImageView extends AppCompatImageView {

    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}