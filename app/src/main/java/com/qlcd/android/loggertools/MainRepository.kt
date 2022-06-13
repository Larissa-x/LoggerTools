package com.qlcd.android.loggertools

import com.blankj.utilcode.util.LogUtils
import com.qlcd.loggertools.logger.LoggerInterceptor
import com.qlcd.loggertools.manager.DatabaseManager
import okhttp3.*
import java.io.IOException
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


    suspend fun requestNetwork() {
        val build = OkHttpClient.Builder()
            .addInterceptor(LoggerInterceptor())
            .build()
        var body = MultipartBody.Builder()
            .addFormDataPart("targetuserid", "userid")
            .addFormDataPart("version", "1.0.0")
            .addFormDataPart("plat","1")
            .build();
        val request = Request.Builder()
            .url("http://39.107.85.70:8301/app/relation/follow")
            .post(body)
            .addHeader("version","test1")
            .build()

        val newCall = build.newCall(request)
        val response = newCall.enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                LogUtils.d(response.body)
            }
        })

    }
}