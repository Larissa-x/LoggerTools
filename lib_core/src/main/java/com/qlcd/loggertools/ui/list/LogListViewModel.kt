package com.qlcd.loggertools.ui.list

import androidx.lifecycle.viewModelScope
import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.base.viewmodel.MutableBooleanLiveData
import com.qlcd.loggertools.base.viewmodel.MutableStringLiveData
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.livedata.SingleLiveData
import com.qlcd.loggertools.manager.DatabaseManager
import com.qlcd.loggertools.manager.LoggerDataManager
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

    val allLevelLiveData = SingleLiveData<List<FilterEntity>>()

    val allModuleLiveData = SingleLiveData<List<FilterEntity>>()

    // 默认倒序
    var prevSortType = DESC
    var prevDateFlag = false
    var prevDateText = ""
    var prevModule = mutableListOf("全部")
    var prevLevel = mutableListOf("全部")


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
        if (allModuleLiveData.value.isNullOrEmpty()) {
            list.add("全部")
        } else {
            allModuleLiveData.value?.filter { it.isChecked }?.map { it.name }
                ?.let { list.addAll(it);prevModule = (it.toMutableList()) }
        }

        if (allLevelLiveData.value.isNullOrEmpty()) {
            list.add("全部")
        } else {
            allLevelLiveData.value?.filter { it.isChecked }?.map { it.name }
                ?.let { list.addAll(it);prevLevel = (it.toMutableList()) }
        }

        return list
    }

    fun getData(sortType: String) {
        setLoading()
        if (isDateFilter.value) {
            //根据日期筛选数据
            viewModelScope.launch {
                var level = StringBuffer()
                var module = StringBuffer()
                prevLevel.forEach {
                    if (prevLevel.indexOf(it) == 0) {
                        level.append("'$it'")
                    } else {
                        level.append(",")
                        level.append("'$it'")
                    }
                }
                prevModule.forEach {
                    if (prevModule.indexOf(it) == 0) {
                        module.append("'$it'")
                    } else {
                        module.append(",")
                        module.append("'$it'")
                    }
                }

                val query =
                    DatabaseManager.db.loggerDao.query(level = level.toString(),
                        module = module.toString(),
                        time = dateTextFilter.value,
                        sort = sortType)
                loggerListLivedata.value = query
            }
        } else {
            //获取自启动数据
            var logData = LoggerDataManager.getCurrentLogData()
            logData = if (sortType == DESC) {
                logData.sortedByDescending { it.time }
            } else {
                logData.reversed()
            }
            loggerListLivedata.value = logData
        }
        setNormal()
    }

    fun cleanData() {
        LoggerDataManager.cleanCurrentData()
    }

    fun getLevelList() {
        viewModelScope.launch {
            val queryAllLevel = DatabaseManager.db.loggerDao.queryAllLevel()
            val levelList = mutableListOf<FilterEntity>()
            levelList.add(FilterEntity(true, "全部"))
            queryAllLevel.forEach {
                levelList.add(FilterEntity(false, it))
            }
            allLevelLiveData.value = levelList
        }

    }

    fun getModuleList() {
        viewModelScope.launch {
            val queryAllModule = DatabaseManager.db.loggerDao.queryAllModule()
            val moduleList = mutableListOf<FilterEntity>()
            moduleList.add(FilterEntity(true, "全部"))

            queryAllModule.forEach {
                moduleList.add(FilterEntity(false, it))
            }
            allModuleLiveData.value = moduleList
        }
    }
}