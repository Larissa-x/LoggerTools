package com.qlcd.android.ui.viewstate

import android.content.Context
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.R
import androidx.fragment.app.Fragment
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class ViewStateManager constructor(
    val view: View,
    val onViewStateListener: OnViewStateListener,
    fragment: Fragment? = null
) {
    private val viewStateLayout: ViewStateLayout
    private val context: Context

    init {
        val contentParent: ViewGroup = view.parent as ViewGroup
        context = contentParent.context
        val childCount: Int = contentParent.childCount
        var viewCount: Int = 0
        for (i in 0..childCount) {
            if (contentParent[i] == view) {
                viewCount = i
                break
            }
        }
        contentParent.removeView(view)
        val viewStateLayout = ViewStateLayout(context)
        val layoutParam = view.layoutParams
        fragment?.let {
            viewStateLayout.setTag(R.id.fragment_container_view_tag, it)
        }
        if (contentParent is SmartRefreshLayout) {
            contentParent.addView(
                viewStateLayout,
                0,
                SmartRefreshLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }else{
            contentParent.addView(viewStateLayout, viewCount, layoutParam)
        }
        viewStateLayout.mContentView = view
        viewStateLayout.setEmptyView(onViewStateListener.bindEmptyView())
        viewStateLayout.setRetryView(onViewStateListener.bindRetryView())
        onViewStateListener.onEmptyInit(viewStateLayout.mEmptyView)
        onViewStateListener.onRetryInit(viewStateLayout.mRetryView)
        this.viewStateLayout = viewStateLayout
    }

    fun showContent() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            viewStateLayout.showContent()
        } else {
            viewStateLayout.post() {
                viewStateLayout.showContent()
            }
        }
    }

    fun showEmpty() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            viewStateLayout.showEmpty()
        } else {
            viewStateLayout.post() {
                viewStateLayout.showEmpty()
            }
        }
    }

    fun showRetry() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            viewStateLayout.showRetry()
        } else {
            viewStateLayout.post() {
                viewStateLayout.showRetry()
            }
        }
    }

    fun onDestroy() {
    }

    fun showLoading():Boolean {
        return onViewStateListener.onLoadingState()
    }
}