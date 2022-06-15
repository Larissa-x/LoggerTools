package com.qlcd.android.loggertools

import com.qlcd.loggertools.logger.LoggerInterceptor
import com.qlcd.loggertools.manager.DatabaseManager
import okhttp3.*
import java.io.IOException

class MainRepository {

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
        time: String? = null,
        sort: String? = "DESC",
    ) = DatabaseManager.db.loggerDao.query(level, time, sort)


    fun requestNetwork() {
        val build = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor())
            .build()

        var body = FormBody.Builder()
            .addEncoded("phone", "13521402817")
            .addEncoded("password", "admin.123")
            .build();
        val request = Request.Builder()
            .url("http://39.107.85.70:8301/app/relation/follow")
            .post(body)
            .addHeader("version", "1.0.0")
            .build()
        build.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }
}