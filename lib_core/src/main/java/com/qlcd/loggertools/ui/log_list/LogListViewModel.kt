package com.qlcd.loggertools.ui.log_list

import androidx.lifecycle.viewModelScope
import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.base.viewmodel.MutableBooleanLiveData
import com.qlcd.loggertools.base.viewmodel.MutableStringLiveData
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.livedata.SingleLiveData
import com.qlcd.loggertools.logger.LogKit
import com.qlcd.loggertools.manager.DatabaseManager
import kotlinx.coroutines.launch

class LogListViewModel : BaseViewModel() {

    companion object {
        const val DESC = "DESC"
        const val ASC = "ASC"
    }

    val keywords = MutableStringLiveData()
    var prevKeywords = ""

    val isDateFilter = MutableBooleanLiveData(false)

    val dateTextFilter = MutableStringLiveData("")

    // 默认倒序
    var prevSortType = DESC
    var prevDateFlag = false
    var prevDateText = ""

    val loggerListLivedata = SingleLiveData<List<LoggerEntity>>()
    fun cleanSearchContent() {
        if (keywords.value.isNotEmpty()) {
            keywords.value = ""
        }
    }

    // 获取展示筛选项的ui数据
    fun getFilterLabel(): List<String> {
        val list = mutableListOf<String>()
        list.add(if (prevSortType == DESC) "倒序" else "正序")
        list.add(if (prevDateFlag) prevDateText else "自启动后")
        return list
    }

    fun getData(sortType: String) {
        setLoading()
        if (isDateFilter.value) {
            //根据日期筛选数据
            viewModelScope.launch {
                val query = DatabaseManager.db.loggerDao.query(time = dateTextFilter.value, sort = sortType)
                loggerListLivedata.value = query
            }
        } else {
            //获取自启动数据
            var logData = LogKit.getLogData()
            logData = if (sortType == "DESC") {
                logData.sortedByDescending { it.time }
            } else {
                logData.reversed()
            }
            loggerListLivedata.value = logData
        }
        setNormal()
    }
}