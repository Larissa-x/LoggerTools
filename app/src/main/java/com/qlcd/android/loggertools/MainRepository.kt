package com.qlcd.android.loggertools

import com.qlcd.loggertools.entity.LoggerEntity
import com.qlcd.loggertools.manager.DatabaseManager
import javax.inject.Inject

class MainRepository @Inject constructor(){

   suspend fun requestQueryAll(): List<LoggerEntity> {
      return DatabaseManager.db.loggerDao.queryAllLoggers();
   }
}