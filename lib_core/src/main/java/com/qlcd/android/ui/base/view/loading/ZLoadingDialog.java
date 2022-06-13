package com.qlcd.android.ui.base.view.loading;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;


import com.qlcd.android.ui.R;

import java.lang.ref.WeakReference;
/**
 * Created by zyao89 on 2017/3/19.
 * Contact me at 305161066@qq.com or zyao89@gmail.com
 * For more projects: https://github.com/zyao89
 * My Blog: http://zyao89.me
 */
public class ZLoadingDialog {
    private final WeakReference<Context> mContext;
    private final int mThemeResId;
    private int mLoadingBuilderColor;
    private String mHintText;
    private float mHintTextSize;
    private int mHintTextColor;
    private boolean mCancelable;
    private boolean mCanceledOnTouchOutside;
    private Dialog mZLoadingDialog;

    public ZLoadingDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ZLoadingDialog(@NonNull Context context, int themeResId) {
        this.mHintTextSize = -1.0F;
        this.mHintTextColor = -1;
        this.mCancelable = true;
        this.mCanceledOnTouchOutside = false;
        this.mContext = new WeakReference(context);
        this.mThemeResId = themeResId;
    }

    public ZLoadingDialog setLoadingColor(int color) {
        this.mLoadingBuilderColor = color;
        return this;
    }

    public ZLoadingDialog setHintText(String text) {
        this.mHintText = text;
        return this;
    }

    public ZLoadingDialog setHintTextSize(float size) {
        this.mHintTextSize = size;
        return this;
    }

    public ZLoadingDialog setHintTextColor(int color) {
        this.mHintTextColor = color;
        return this;
    }

    public ZLoadingDialog setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        return this;
    }

    public ZLoadingDialog setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        this.mCanceledOnTouchOutside = canceledOnTouchOutside;
        return this;
    }

    @NonNull
    private View createContentView() {
        if (this.isContextNotExist()) {
            throw new RuntimeException("Context is null...");
        } else {
            return View.inflate((Context)this.mContext.get(), R.layout.z_loading_dialog, (ViewGroup)null);
        }
    }

    public Dialog create() {
        if (this.isContextNotExist()) {
            throw new RuntimeException("Context is null...");
        } else {
            if (this.mZLoadingDialog != null) {
                this.cancel();
            }

            this.mZLoadingDialog = new Dialog((Context)this.mContext.get(), this.mThemeResId);
            View contentView = this.createContentView();
            ZLoadingView zLoadingView = (ZLoadingView)contentView.findViewById(R.id.z_loading_view);
            ZLoadingTextView zTextView = (ZLoadingTextView)contentView.findViewById(R.id.z_text_view);
            TextView zCustomTextView = (TextView)contentView.findViewById(R.id.z_custom_text_view);
            if (this.mHintTextSize > 0.0F && !TextUtils.isEmpty(this.mHintText)) {
                zCustomTextView.setVisibility(View.VISIBLE);
                zCustomTextView.setText(this.mHintText);
                zCustomTextView.setTextSize(this.mHintTextSize);
                zCustomTextView.setTextColor(this.mHintTextColor == -1 ? this.mLoadingBuilderColor : this.mHintTextColor);
            } else if (!TextUtils.isEmpty(this.mHintText)) {
                zTextView.setVisibility(View.VISIBLE);
                zTextView.setText(this.mHintText);
                zTextView.setColorFilter(this.mHintTextColor == -1 ? this.mLoadingBuilderColor : this.mHintTextColor);
            }

            try {
                zLoadingView.setLoadingBuilder(DoubleCircleBuilder.class.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            zLoadingView.setColorFilter(this.mLoadingBuilderColor);
            this.mZLoadingDialog.setContentView(contentView);
            this.mZLoadingDialog.setCancelable(this.mCancelable);
            this.mZLoadingDialog.setCanceledOnTouchOutside(this.mCanceledOnTouchOutside);
            this.mZLoadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.mZLoadingDialog.getWindow().setDimAmount(0f);
            return this.mZLoadingDialog;
        }
    }

    public void show() {
        if (this.mZLoadingDialog != null) {
            this.mZLoadingDialog.show();
        } else {
            Dialog zLoadingDialog = this.create();
            zLoadingDialog.show();
        }
    }

    public void cancel() {
        if (this.mZLoadingDialog != null) {
            this.mZLoadingDialog.cancel();
        }

        this.mZLoadingDialog = null;
    }

    public void dismiss() {
        if (this.mZLoadingDialog != null) {
            this.mZLoadingDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return this.mZLoadingDialog.isShowing();
    }

    private boolean isContextNotExist() {
        Context context = (Context)this.mContext.get();
        return context == null;
    }
}