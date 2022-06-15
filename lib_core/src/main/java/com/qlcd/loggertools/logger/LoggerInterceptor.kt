package com.qlcd.loggertools.logger

import okhttp3.*
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

        responseJson.put("responseDuration", tookMs)
        jsonObject.put("request", formatRequestJson(request))
        jsonObject.put("response", responseJson)
        LogKit.json(jsonObject.toString())
        return response.newBuilder().body(content?.toResponseBody(mediaType)).build()
    }

    private fun formatRequestJson(request: Request): JSONObject {
        val requestJson = JSONObject()
        requestJson.put("method", request.method)
        requestJson.put("scheme",request.url.scheme)
        requestJson.put("host",request.url.host)
        requestJson.put("port",request.url.port)
        requestJson.put("path",request.url.encodedPath)
        val jsonArray = JSONArray()
        val names = request.headers.names()
        names.forEach {
            val jsonObject = JSONObject()
            jsonObject.put("key",it)
            jsonObject.put("value",request.headers[it])
            jsonArray.put(jsonObject)
        }

        val dataJson = JSONArray()
        requestJson.put("header", jsonArray)

        if (request.method.equals("post", true)) {
            if (request.body is FormBody) {
                val formBody = request.body as FormBody
                for (i in 0 until formBody.size) {
                    val jsonObject = JSONObject()
                    jsonObject.put("key",formBody.encodedName(i))
                    jsonObject.put("value",formBody.encodedValue(i))
                    dataJson.put(jsonObject)
                }
            }

        } else if (request.method.equals("get",true)){
            val queryParameterNames = request.url.queryParameterNames
            queryParameterNames.forEach {
                val queryParameter = request.url.queryParameter(it)
                val jsonObject = JSONObject()
                jsonObject.put("key",it)
                jsonObject.put("value",queryParameter)
                dataJson.put(jsonObject)
            }
        }
        requestJson.put("params",dataJson)

        return requestJson
    }
}