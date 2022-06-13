package com.qlcd.android.ui.dao

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.qlcd.android.ui.logger.LogKit
import com.qlcd.android.ui.entity.LoggerEntity
import java.util.*

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


    /**
     * 多种条件组合查询、全都非必传
     */
    suspend fun query(
        level: String? = null,
        fileName: String? = null,
        time: String? = null,
        sort: String? = "DESC",
        page: Int = 1,
        pageNum: Int = 10,
    ): List<LoggerEntity> {
        val buffer = StringBuffer("SELECT * FROM logger_table")

        if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(fileName) || !TextUtils.isEmpty(time)) {
            buffer.append(" WHERE ")
        }

        try {
            if (!TextUtils.isEmpty(time)) {
                val string2Date = TimeUtils.string2Date(time, "yyyy-MM-dd")
                val calendar = Calendar.getInstance()
                calendar.time = string2Date
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                //当天0点
                val startTime = calendar.time.time
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                val endTime = calendar.time.time
                //当天23：59：:59
                buffer.append("time <=${endTime}")
                buffer.append(" and ")
                buffer.append("time >=${startTime}")
            }
        } catch (e: Exception) {
            LogKit.e(e.toString())
        }

        if (!TextUtils.isEmpty(level)) {
            if (!buffer.endsWith(" WHERE ")) {
                buffer.append(" and ")
            }
            buffer.append("level='${level}'")
        }
        if (!TextUtils.isEmpty(fileName)) {
            if (!buffer.endsWith(" WHERE ")) {
                buffer.append(" and ")
            }
            buffer.append("fileName='${fileName}'")
        }
        buffer.append(" ORDER BY time ASC ")
        buffer.append("LIMIT ${(page-1)*pageNum},${pageNum}")

        LogUtils.d(buffer.toString())
        return queryAllLoggersByDesc(SimpleSQLiteQuery(buffer.toString()))
    }
}