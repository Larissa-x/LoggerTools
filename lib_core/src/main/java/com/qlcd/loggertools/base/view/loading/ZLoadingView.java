package com.qlcd.loggertools.base.view.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;


public class ZLoadingView extends AppCompatImageView {
    private ZLoadingDrawable mZLoadingDrawable;
    protected ZLoadingBuilder mZLoadingBuilder;

    public ZLoadingView(Context context) {
        this(context, (AttributeSet)null);
    }

    public ZLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ZLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            this.setLoadingBuilder(DoubleCircleBuilder.class.newInstance());
            this.setColorFilter(getColorFilter());
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public void setLoadingBuilder(ZLoadingBuilder builder) throws InstantiationException, IllegalAccessException {
        this.mZLoadingBuilder = builder;
        this.mZLoadingDrawable = new ZLoadingDrawable(this.mZLoadingBuilder);
        this.mZLoadingDrawable.initParams(this.getContext());
        this.setImageDrawable(this.mZLoadingDrawable);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.startAnimation();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.stopAnimation();
    }

    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        boolean visible = visibility == View.VISIBLE && this.getVisibility() == View.VISIBLE;
        if (visible) {
            this.startAnimation();
        } else {
            this.stopAnimation();
        }

    }

    private void startAnimation() {
        if (this.mZLoadingDrawable != null) {
            this.mZLoadingDrawable.start();
        }

    }

    private void stopAnimation() {
        if (this.mZLoadingDrawable != null) {
            this.mZLoadingDrawable.stop();
        }

    }
}