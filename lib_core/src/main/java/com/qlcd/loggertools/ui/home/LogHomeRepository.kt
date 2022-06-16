package com.qlcd.loggertools.ui.home

import com.qlcd.loggertools.logger.LogKit
import com.qlcd.loggertools.manager.DatabaseManager

/**
 * Created by GaoLuHan on 2022/6/16
 * Describe:
 */
class LogHomeRepository {
    suspend fun queryFromDB(
        level: String? = null,
        time: String? = null,
        sort: String? = "DESC",
    ) = DatabaseManager.db.loggerDao.query(level, time, sort)

    fun queryFromLocal(sort: String) = if (sort == LogHomeViewModel.DESC) LogKit.getLogData() else LogKit.getLogData().reversed()
}