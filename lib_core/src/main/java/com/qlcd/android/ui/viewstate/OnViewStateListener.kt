package com.qlcd.android.ui.viewstate

import android.view.View
import android.view.View.NO_ID
import androidx.annotation.LayoutRes

interface OnViewStateListener {
    @LayoutRes
    fun bindRetryView():Int

    @LayoutRes
    fun bindEmptyView():Int

    /**
     * 返回true则拦截默认的Loading框效果
     */
    fun onLoadingState():Boolean{
        return false
    }
    
    fun onRetryInit(retryView: View?) {

    }

    fun onEmptyInit(emptyView: View?) {

    }
}