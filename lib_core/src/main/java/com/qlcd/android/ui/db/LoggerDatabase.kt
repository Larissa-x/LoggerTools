package com.qlcd.loggertools.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qlcd.loggertools.dao.LoggerDao
import com.qlcd.loggertools.entity.LoggerEntity

private const val DB_NAME = "my_db"
private const val DB_VERSION = 1

@Database(version = DB_VERSION, entities = [LoggerEntity::class])
abstract class LoggerDatabase : RoomDatabase() {

    val loggerDao: LoggerDao by lazy { createLogDao() }
    abstract fun createLogDao(): LoggerDao
}