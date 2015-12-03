package com.github.kb36.tumblrdash.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * create squared image view programmatically
 */
public final class SquaredImageView extends ImageView {
    private static final String TAG = "SquaredImageView";

    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        setMeasuredDimension(600,600);
    }
}