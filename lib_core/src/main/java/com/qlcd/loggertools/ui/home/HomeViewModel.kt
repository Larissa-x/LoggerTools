package com.qlcd.loggertools.ui.home

import androidx.lifecycle.viewModelScope
import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.base.viewmodel.MutableStringLiveData
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.livedata.SingleLiveData
import kotlinx.coroutines.launch

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
class HomeViewModel : BaseViewModel() {
    private val repository: HomeRepository = HomeRepository()

    val listLiveData = SingleLiveData<List<LoggerEntity>>()
    val keywords = MutableStringLiveData()


    fun cleanSearchContent() {
        if (keywords.value.isNotEmpty()) {
            keywords.value = ""
        }
    }

    fun getData() {
        viewModelScope.launch {
            val requestLoggerList = repository.requestLoggerList()
            listLiveData.value = requestLoggerList
        }
    }

}