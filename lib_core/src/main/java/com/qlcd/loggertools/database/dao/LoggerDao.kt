package com.qlcd.loggertools.database.dao

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.qlcd.loggertools.logger.LogKit
import com.qlcd.loggertools.database.entity.LoggerEntity
import java.util.*


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

    @RawQuery
    suspend fun customQueryAllLoggers(query: SupportSQLiteQuery): List<LoggerEntity>


    /**
     * 多种条件组合查询、全都非必传
     */
    suspend fun query(
        level: String? = null,
        time: String? = null,//yyyy-MM-dd格式
        sort: String? = "DESC",
//        page: Int = 1,//去掉分页逻辑
//        pageNum: Int = 10,//去掉分页逻辑
    ): List<LoggerEntity> {
        val buffer = StringBuffer("SELECT * FROM logger_table")

        if (!TextUtils.isEmpty(level) || !TextUtils.isEmpty(time)) {
            buffer.append(" WHERE ")
        }

        try {
            if (!TextUtils.isEmpty(time)) {
                val string2Date = TimeUtils.string2Date(time, "yyyy-MM-dd")
                val calendar = Calendar.getInstance()
                calendar.time = string2Date
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                //当天0点
                val startTime = calendar.time.time
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                //当天23：59：:59
                val endTime = calendar.time.time
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
        buffer.append(" ORDER BY time $sort ")

        //去掉分页逻辑
//        buffer.append("LIMIT ${(page-1)*pageNum},${pageNum}")

        LogUtils.d(buffer.toString())
        return customQueryAllLoggers(SimpleSQLiteQuery(buffer.toString()))
    }
}