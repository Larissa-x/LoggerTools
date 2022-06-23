package com.qlcd.loggertools.utils

import android.text.*
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.*
import com.qlcd.loggertools.LoggerTools.context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

var Int.toDp: Int
    inline get() = toFloat().toDp
    private set(_) {}

var Float.toDp: Int
    inline get() = (this / (context.resources.displayMetrics.density) + 0.5).toInt()
    private set(_) {}

var Int.dpToPx: Float
    inline get() = toFloat().dpToPx
    private set(_) {}

var Float.dpToPx: Float
    inline get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
    private set(_) {}

var Int.spToPx: Float
    inline get() = toFloat().spToPx
    private set(_) {}

var Float.spToPx: Float
    inline get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics)
    private set(_) {}

/**点击节流*/
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

/**判断字符串是否是json串*/
fun isJson(content: String): Boolean {
    return try {
        if (content.contains("[") && content.contains("]")) {
            JSONArray(content)
            true
        } else {
            JSONObject(content)
            true
        }
    } catch (e: JSONException) {
        false
    }
}