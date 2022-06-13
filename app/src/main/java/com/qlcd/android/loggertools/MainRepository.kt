package com.qlcd.android.loggertools

//import com.qlcd.android.ui.entity.LoggerEntity
//import com.qlcd.android.ui.manager.DatabaseManager
import com.qlcd.android.ui.manager.DatabaseManager
import javax.inject.Inject

class MainRepository @Inject constructor() {

    //    suspend fun requestQueryAll(): List<LoggerEntity> {
//        return DatabaseManager.db.loggerDao.queryAllLoggers()
//    }
//
//    suspend fun requestQueryLevel(): List<String> {
//        return DatabaseManager.db.loggerDao.queryAllLevel()
//    }
//
    suspend fun requestQuery(
        level: String? = null,
        fileName: String? = null,
        time: String? = null,
        sort: String? = "DESC",
        page: Int = 1,
        pageNum: Int = 10,
    ) = DatabaseManager.db.loggerDao.query(level, fileName, time, sort, page, pageNum)
}