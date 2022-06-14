package com.qlcd.loggertools.ui.home

import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.base.viewmodel.MutableStringLiveData
import com.qlcd.loggertools.database.entity.LoggerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
) : BaseViewModel() {

    val keywords = MutableStringLiveData()

    fun cleanSearchContent() {
        if (keywords.value.isNotEmpty()) {
            keywords.value = ""
        }
    }

    fun getData(): List<LoggerEntity> {
        return listOf(LoggerEntity(), LoggerEntity(), LoggerEntity(), LoggerEntity(), LoggerEntity())
    }

}