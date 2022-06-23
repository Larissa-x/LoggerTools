package com.qlcd.loggertools.logger

import android.util.Log
import com.qlcd.loggertools.LoggerTools
import com.qlcd.loggertools.manager.LoggerDataManager
import com.qlcd.loggertools.widget.STR_ERROR
import com.qlcd.loggertools.widget.STR_JSON
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** * Created by Tony Shen on 2017/1/2. */
object LogKit {
    enum class LogLevel {
        NONE {
            override val value: Int get() = -1
        },
        ERROR {
            override val value: Int get() = 0
        },
        WARN {
            override val value: Int get() = 1
        },
        INFO {
            override val value: Int get() = 2
        },
        DEBUG {
            override val value: Int get() = 3
        };

        abstract val value: Int
    }

    private var TAG = "日志"
    var logLevel = LogLevel.DEBUG // 日志的等级，可以进行配置，最好在Application中进行全局的配置

    @JvmStatic
    fun init(clazz: Class<*>) {
        TAG = clazz.simpleName
    }

    /** * 支持用户自己传tag，可扩展性更好 * @param tag */
    @JvmStatic
    fun init(tag: String) {
        TAG = tag
    }

    @JvmStatic
    fun e(msg: String?) {
        if (LogLevel.ERROR.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.e(TAG, String.format(s, msg))
                LoggerDataManager.insertToDatabase(STR_ERROR, msg)
            }
        }
    }

    @JvmStatic
    fun w(msg: String?) {
        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.e(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun i(msg: String?) {
        if (LogLevel.INFO.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.i(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun d(msg: String?) {
        if (LogLevel.DEBUG.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.d(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun json(json: String?) {
        var j = json
        if (j.toString().isEmpty()) {
            d("Empty/Null json liveInfoDetail")
            return
        }
        try {
            j = j.toString().trim { it <= ' ' }
            if (j.startsWith("{")) {
                val jsonObject = JSONObject(j)
                var message = jsonObject.toString(LoggerPrinter.JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n│ ")
                d(message)
                LoggerDataManager.insertToDatabase(STR_JSON, json)
                return
            }
            if (j.startsWith("[")) {
                val jsonArray = JSONArray(j)
                var message = jsonArray.toString(LoggerPrinter.JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n│ ")
                d(message)
                LoggerDataManager.insertToDatabase(STR_JSON, json)
                return
            }
        } catch (e: JSONException) {
            e(e.toString())
        }
    }

    private fun getMethodNames(): String {
        val sElements = Thread.currentThread().stackTrace
        var stackOffset = LoggerPrinter.getStackOffset(sElements)
        stackOffset++
        val builder = StringBuilder()
        builder.append(" \r\n")
            .append(LoggerPrinter.TOP_BORDER).append("\r\n") // 添加当前线程名
            .append("│ " + "Thread: " + Thread.currentThread().name).append("\r\n")
            .append(LoggerPrinter.MIDDLE_BORDER).append("\r\n")
            // 添加类名、方法名、行数
            .append("│ ")
            .append(sElements[stackOffset].className)
            .append(".")
            .append(sElements[stackOffset].methodName)
            .append(" ")
            .append(" (")
            .append(sElements[stackOffset].fileName)
            .append(":")
            .append(sElements[stackOffset].lineNumber)
            .append(")").append("\r\n")
            .append(LoggerPrinter.MIDDLE_BORDER).append("\r\n")
            // 添加打印的日志信息
            .append("│ ")
            .append("%s").append("\r\n")
            .append(LoggerPrinter.BOTTOM_BORDER).append("\r\n")
        return builder.toString()
    }
}