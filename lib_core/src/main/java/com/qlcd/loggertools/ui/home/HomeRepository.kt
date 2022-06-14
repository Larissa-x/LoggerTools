package com.qlcd.loggertools.ui.home

import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.manager.DatabaseManager
import javax.inject.Inject

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
class HomeRepository @Inject constructor() {
    suspend fun requestLoggerList(): List<LoggerEntity> {
        return DatabaseManager.db.loggerDao.query(fileName = "LoggerInterceptor.kt")
    }
}