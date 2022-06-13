package com.qlcd.android.ui.viewstate

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.qlcd.android.ui.logger.LogKit

class ViewStateLayout(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) :
    FrameLayout(context, attrs, defStyleAttr) {
    val mInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var mContentView: View? = null
        set(value) {
            field?.apply {
                LogKit.w("已经设置了ContentView")
            }
            removeView(field)
            value?.apply {
                val layoutParam = ViewGroup.LayoutParams(layoutParams)
                layoutParam.width = MATCH_PARENT
                layoutParam.height = MATCH_PARENT
                layoutParams = layoutParam
                addView(value, 0, value.layoutParams)
            }
            field = value
        }

    var mEmptyView: View? = null
        set(value) {
            field?.apply { LogKit.w("已经设置了EmptyView") }
            removeView(field)
            value?.apply { addView(value, 0, mContentView?.layoutParams) }
            field = value
        }
    var mRetryView: View? = null
        set(value) {
            field?.apply { LogKit.w("已经设置了RetryView") }
            removeView(field)
            value?.apply { addView(value, 0, mContentView?.layoutParams) }
            field = value
        }

    fun setEmptyView(@LayoutRes layoutId: Int) {
        if (layoutId == View.NO_ID) {
            return
        }
        mEmptyView = mInflater.inflate(layoutId, this, false)
    }

    fun setContentView(@LayoutRes layoutId: Int) {
        if (layoutId == View.NO_ID) {
            return
        }
        mContentView = mInflater.inflate(layoutId, this, false)
    }

    fun setRetryView(@LayoutRes layoutId: Int) {
        if (layoutId == View.NO_ID) {
            return
        }
        mRetryView = mInflater.inflate(layoutId, this, false)
    }

    private fun showView(view: View?) {
        view?.apply {
            view.visibility = View.VISIBLE
            if (view != mContentView) {
                mContentView?.visibility = GONE
            }
            if (view != mEmptyView) {
                mEmptyView?.visibility = GONE
            }
            if (view != mRetryView) {
                mRetryView?.visibility = GONE
            }

        }
    }

    fun showContent() {
        showView(mContentView)
    }

    fun showEmpty() {
        showView(mEmptyView)
    }

    fun showRetry() {
        showView(mRetryView)
    }

}