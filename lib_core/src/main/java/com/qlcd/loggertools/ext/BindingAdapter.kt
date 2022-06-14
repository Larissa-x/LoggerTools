package com.qlcd.loggertools.ext

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("isInVisible")
fun isInVisible(view: View, isInVisible: Boolean) {
    view.isInvisible = isInVisible
}

@BindingAdapter("isGone")
fun isGone(view: View, isGone: Boolean) {
    view.isGone = isGone
}

@BindingAdapter("isTextBold")
fun isTextBold(view: TextView, isBold: Boolean) {
    view.typeface = if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
}