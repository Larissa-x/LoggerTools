package com.qlcd.loggertools.widget;

import android.app.ActionBar;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;

public class BarView extends View {
    public BarView(Context context) {
        super(context);
    }

    public BarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(ActionBar.LayoutParams.MATCH_PARENT, BarUtils.getStatusBarHeight());
    }
}
