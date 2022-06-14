package com.qlcd.loggertools.ext

import android.graphics.drawable.Drawable
import android.text.*
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.qlcd.loggertools.BaseApplication

var Int.toDp: Int
    inline get() = toFloat().toDp
    private set(_) {}

var Float.toDp: Int
    inline get() = (this / (BaseApplication.context.resources.displayMetrics.density) + 0.5).toInt()
    private set(_) {}

var Int.dpToPx: Float
    inline get() = toFloat().dpToPx
    private set(_) {}

var Float.dpToPx: Float
    inline get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, BaseApplication.context.resources.displayMetrics)
    private set(_) {}

var Int.spToPx: Float
    inline get() = toFloat().spToPx
    private set(_) {}

var Float.spToPx: Float
    inline get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, BaseApplication.context.resources.displayMetrics)
    private set(_) {}

/**
 * 根据资源文件获取颜色int值
 */
val Int.toColorInt: Int
    inline get() = ContextCompat.getColor(BaseApplication.context, this)

val Int.toDrawable: Drawable?
    inline get() = ContextCompat.getDrawable(BaseApplication.context, this)


/**
 * 点击节流
 */
inline fun View.setThrottleClickListener(throttleTime: Long = 500, crossinline onClick: (v: View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        private var prevClickTime = 0L
        override fun onClick(v: View?) {
            val t = System.currentTimeMillis()
            if (t - prevClickTime > throttleTime) {
                prevClickTime = t
                onClick(this@setThrottleClickListener)
            }
        }
    })
}