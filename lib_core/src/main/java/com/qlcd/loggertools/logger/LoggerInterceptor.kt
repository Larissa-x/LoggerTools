package com.qlcd.loggertools.logger

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

/**
 * 对应request和response的日志拦截器。
 */
class LoggerInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val body = response.body
        val mediaType = body?.contentType()
        val content = body?.string()
        val jsonObject = JSONObject()
        val requestJson = JSONObject()
        requestJson.put("method",request.method)
        requestJson.put("url",request.url.toUri().toString() + request.url.encodedPath.replace("\\",""))
        requestJson.put("header",request.headers)
        jsonObject.put("request",requestJson)
        jsonObject.put("response",content)
        LogKit.d(jsonObject.toString())
        return response.newBuilder().body(content?.toResponseBody(mediaType)).build()
    }

}