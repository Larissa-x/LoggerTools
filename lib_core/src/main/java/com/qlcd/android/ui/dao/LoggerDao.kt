package com.qlcd.loggertools.dao

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.qlcd.loggertools.entity.LoggerEntity

const val everyday = 1 * 24 * 60 * 60 * 1000

@Dao
interface LoggerDao {

    @Insert
    suspend fun insertLogger(entity: LoggerEntity)

    /**
     * 查询logger_table所有数据，不排序
     */
    @Query(value = "SELECT * FROM logger_table")
    suspend fun queryAllLoggers(): List<LoggerEntity>

    /**
     * 查询logger_table所有level，去重
     */
    @Query(value = "SELECT DISTINCT level FROM logger_table")
    suspend fun queryAllLevel(): List<String>

    /**
     * 时间降序查询
     * 按条件查询logger_table数据
     * 全部必传，sql需要优化
     */
    @Query(value = "SELECT * FROM logger_table WHERE level= :level AND fileName= :fileName AND time>=:time ORDER BY time DESC LIMIT (:page-1)* :pageNum,:pageNum")
    suspend fun queryAllLoggersByDesc(
        level: String,
        fileName: String,
        time: Long,
        page: Int,
        pageNum: Int = 10,
    ): List<LoggerEntity>

    /**
     * 时间升序查询
     */
    @Query(value = "SELECT * FROM logger_table WHERE level= :level AND fileName= :fileName AND time>=:time ORDER BY time ASC LIMIT (:page-1)* :pageNum,:pageNum")
    suspend fun queryAllLoggersByAsc(
        level: String? = "*",
        fileName: String? = "*",
        time: Long? = 0,
        page: Int = 1,
        pageNum: Int = 10,
    ): List<LoggerEntity>

    @RawQuery
    suspend fun queryAllLoggersByDesc(query: SupportSQLiteQuery): List<LoggerEntity>


    suspend fun query(
        level: String? = null,
        fileName: String? = null,
        time: String? = null,
        page: Int = 1,
        pageNum: Int = 10,
    ): List<LoggerEntity> {
        val buffer = StringBuffer("SELECT * FROM logger_table ")

        if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(fileName) || !TextUtils.isEmpty(time)) {
            buffer.append("WHERE ")
        }
        if (!TextUtils.isEmpty(level)) {
            buffer.append("level='${level}'")
        }
        return queryAllLoggersByDesc(SimpleSQLiteQuery(buffer.toString()))
    }
}