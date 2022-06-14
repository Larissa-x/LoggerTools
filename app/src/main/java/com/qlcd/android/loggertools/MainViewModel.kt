package com.qlcd.android.loggertools

import androidx.lifecycle.viewModelScope
import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel  : BaseViewModel() {
    var repository = MainRepository()
    fun requestQuery(){
        val launch = viewModelScope.launch {
//            val requestQueryLevel = repository.requestQueryLevel()
//            val requestQueryAll = repository.requestQueryAll()
//            val requestQuery = repository.requestQuery(time = "2022-6-13",level = "debug",fileName = "MainActivity.kt")
//
//            LogUtils.json(requestQuery)

            repository.requestNetwork()
        }
    }

}