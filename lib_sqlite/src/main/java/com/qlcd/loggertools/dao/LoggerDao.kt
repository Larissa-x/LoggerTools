package com.qlcd.loggertools.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.qlcd.loggertools.entity.LoggerEntity

@Dao
interface LoggerDao {
    @Insert
    suspend fun insertLogger(entity: LoggerEntity)

    @Query(value = "select * from logger_table")
    suspend fun queryAllLoggers(): List<LoggerEntity>

}