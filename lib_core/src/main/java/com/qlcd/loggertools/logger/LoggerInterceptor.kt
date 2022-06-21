package com.qlcd.loggertools.logger

import com.blankj.utilcode.util.LogUtils
import com.qlcd.loggertools.widget.KEY_REQUEST
import com.qlcd.loggertools.widget.KEY_RESPONSE
import com.qlcd.loggertools.widget.KEY_RESPONSE_DURATION
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
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
        val content = body?.string().orEmpty()
        val jsonObject = JSONObject()
        val responseJson = JSONObject(content)
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)

        responseJson.put(KEY_RESPONSE_DURATION, tookMs)
        jsonObject.put(KEY_REQUEST, formatRequestJson(request))
        jsonObject.put(KEY_RESPONSE, responseJson)
        LogKit.json(jsonObject.toString())
        return response.newBuilder().body(content.toResponseBody(mediaType)).build()
    }

    private fun formatRequestJson(request: Request): JSONObject {
        val requestJson = JSONObject()
        requestJson.put("method", request.method)
        requestJson.put("scheme", request.url.scheme)
        requestJson.put("host", request.url.host)
        requestJson.put("port", request.url.port)
        requestJson.put("path", request.url.encodedPath)
        val jsonArray = JSONArray()
        val names = request.headers.names()
        names.forEach {
            val jsonObject = JSONObject()
            jsonObject.put("key", it)
            jsonObject.put("value", request.headers[it])
            jsonArray.put(jsonObject)
        }

        val dataJson = JSONArray()
        requestJson.put("header", jsonArray)
        if (request.method.equals("post", true)) {
            if (request.body is FormBody) {
                val formBody = request.body as FormBody
                for (i in 0 until formBody.size) {
                    val jsonObject = JSONObject()
                    jsonObject.put("key", formBody.encodedName(i))
                    jsonObject.put("value", formBody.encodedValue(i))
                    dataJson.put(jsonObject)
                }
                requestJson.put("params", dataJson)
            } else {
                val buffer = Buffer()
                request.body?.writeTo(buffer)
                val contentType = request.body?.contentType()
                val charset = contentType?.charset(Charset.forName("UTF-8"))
                val readString = buffer.readString(charset!!)
                requestJson.put("params", JSONObject(readString))
            }
        } else if (request.method.equals("get", true)) {
            val queryParameterNames = request.url.queryParameterNames
            queryParameterNames.forEach {
                val queryParameter = request.url.queryParameter(it)
                val jsonObject = JSONObject()
                jsonObject.put("key", it)
                jsonObject.put("value", queryParameter)
                dataJson.put(jsonObject)
            }
            requestJson.put("params", dataJson)
        }
        return requestJson
    }
}