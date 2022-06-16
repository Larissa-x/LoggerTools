package com.qlcd.loggertools.ui.home

import androidx.lifecycle.viewModelScope
import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.base.viewmodel.MutableStringLiveData
import com.qlcd.loggertools.database.entity.LabelEntity
import com.qlcd.loggertools.database.entity.LoggerEntity
import kotlinx.coroutines.launch

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
class LogHomeViewModel : BaseViewModel() {

    companion object {
        const val DESC = "DESC"
        const val ASC = "ASC"
    }

    val sortList = mutableListOf(
        LabelEntity(ASC, "正序", false),
        LabelEntity(DESC, "倒序", true)
    )
    val dateList = mutableListOf(
        LabelEntity("", "自启动后", true),
        LabelEntity(System.currentTimeMillis().toString(), "XXXX年XX月XX日", false)
    )


    var sort = DESC
    var time = ""
    val keywords = MutableStringLiveData()
    var prevKeywords = ""

    val repository = LogHomeRepository()

    fun cleanSearchContent() {
        if (keywords.value.isNotEmpty()) {
            keywords.value = ""
        }
    }

    fun getShowFilterData(): MutableList<LabelEntity> {
        val list = mutableListOf<LabelEntity>()
        list.addAll(sortList)
        list.addAll(dateList)
        return list.filter { it.checked }.toMutableList()
    }

    fun getLogListData(): List<LoggerEntity> {
        var list = listOf<LoggerEntity>()
        viewModelScope.launch {
            list = if (time.isNotEmpty()) {
                repository.queryFromDB(time = time, sort = sort)
            } else {
                repository.queryFromLocal(sort)
            }
        }
        return list
    }
}