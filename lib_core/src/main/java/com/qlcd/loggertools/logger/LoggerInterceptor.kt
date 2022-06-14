package com.qlcd.loggertools.logger

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * 对应request和response的日志拦截器。
 */
class LoggerInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val startTime = System.nanoTime()
        val request = chain.request()
        val response = chain.proceed(request)

        val body = response.body
        val mediaType = body?.contentType()
        val content = body?.string()
        val jsonObject = JSONObject()
        val responseJson = JSONObject(content)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)

        responseJson.put("responseDuration",tookMs)
        jsonObject.put("request",formatRequestJson(request))
        jsonObject.put("response",responseJson)
        LogKit.json(jsonObject.toString())
        return response.newBuilder().body(content?.toResponseBody(mediaType)).build()
    }

    private fun formatRequestJson(request:Request):JSONObject{
        val requestJson = JSONObject()
        requestJson.put("method",request.method)
        requestJson.put("url",request.url.toUri().toString() + request.url.encodedPath)
        val jsonArray = JSONArray()
        val names = request.headers.names()
        names.forEach {
            val jsonObject = JSONObject()
            jsonObject.put(it,request.headers[it])
            jsonArray.put(jsonObject)
        }
        requestJson.put("header",jsonArray)
        return requestJson
    }
}