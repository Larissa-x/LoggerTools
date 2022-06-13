package com.qlcd.android.loggertools

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.qlcd.android.ui.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
) : BaseViewModel() {

    fun requestQuery(){
        viewModelScope.launch {
//            val requestQueryLevel = repository.requestQueryLevel()
//            val requestQueryAll = repository.requestQueryAll()
//            val requestQueryAll = repository.requestQueryAll()
            val requestQuery = repository.requestQuery("json")

            LogUtils.json(requestQuery)
        }
    }

}