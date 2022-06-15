package com.qlcd.loggertools.ui.home

import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.base.viewmodel.MutableStringLiveData

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
class LogHomeViewModel : BaseViewModel() {
    val keywords = MutableStringLiveData()
    var prevKeywords = ""

    fun cleanSearchContent() {
        if (keywords.value.isNotEmpty()) {
            keywords.value = ""
        }
    }
}