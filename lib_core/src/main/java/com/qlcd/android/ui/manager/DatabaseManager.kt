package com.qlcd.android.ui.manager

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.qlcd.loggertools.db.LoggerDatabase

object DatabaseManager {
    private const val DB_NAME = "my_db.db"
    private val MIGRATIONS = arrayOf(Migration1)
    private lateinit var application: Application
    val db: LoggerDatabase by lazy {
        Room.databaseBuilder(application.applicationContext, LoggerDatabase::class.java, DB_NAME)
            .addCallback(CreatedCallBack)
//            .addMigrations(*MIGRATIONS)
            .build()
    }

    fun saveApplication(application: Application) {
        DatabaseManager.application = application
    }

    private object CreatedCallBack : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            //在新装app时会调用，调用时机为数据库build()之后，数据库升级时不调用此函数
            MIGRATIONS.map {
                Migration1.migrate(db)
            }
        }
    }

    private object Migration1 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 数据库的升级语句
            // database.execSQL("")
        }
    }
}