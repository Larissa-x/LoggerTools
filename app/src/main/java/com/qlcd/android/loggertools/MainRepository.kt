package com.qlcd.android.loggertools

import com.blankj.utilcode.util.GsonUtils
import com.qlcd.loggertools.logger.LoggerInterceptor
import com.qlcd.loggertools.manager.DatabaseManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

        val toMediaTypeOrNull = "application/json; charset=utf-8".toMediaTypeOrNull()

        val hashMapOf = hashMapOf<String, String>()
        hashMapOf["phone"] = "13521402817"
        hashMapOf["password"] = "admin.123"
        val body: RequestBody = RequestBody.create(toMediaTypeOrNull, GsonUtils.toJson(hashMapOf))

//        var body = FormBody.Builder()
//            .addEncoded("phone", "13521402817")
//            .addEncoded("password", "admin.123")
//            .build();
        val request = Request.Builder()
            .url("http://39.107.85.70:8301/app/relation/follow")
            .post(body)
            .addHeader("version", "1.0.0")
            .addHeader("os", "Android")
            .build()
        build.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }
}