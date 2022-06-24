package com.qlcd.loggertools.manager

import com.qlcd.loggertools.database.dao.LoggerDao
import com.qlcd.loggertools.database.entity.LoggerEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


object LoggerDataManager {
    // 往数据库中存储log信息
    private var db: LoggerDao = DatabaseManager.db.loggerDao

    // 启动时存储的log信息
    private val logDataList = Collections.synchronizedList(mutableListOf<LoggerEntity>())

    fun getCurrentLogData(): List<LoggerEntity> {
        return logDataList
    }

    fun cleanCurrentData() {
        logDataList.clear()
    }

    fun insertToDatabase(entity: LoggerEntity) {
        GlobalScope.launch {
            entity.time = System.currentTimeMillis()
            logDataList.add(0, entity)
            db.insertLogger(entity)
        }
    }
}