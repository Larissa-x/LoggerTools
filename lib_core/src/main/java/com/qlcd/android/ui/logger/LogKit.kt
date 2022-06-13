package com.qlcd.android.ui.logger

import android.util.Log
import com.qlcd.loggertools.dao.LoggerDao
import com.qlcd.loggertools.db.LoggerDatabase
import com.qlcd.loggertools.entity.LoggerEntity
import com.qlcd.loggertools.manager.DatabaseManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/** * Created by Tony Shen on 2017/1/2. */
object LogKit {


    private var entity: LoggerEntity = LoggerEntity()
    private var db: LoggerDao = DatabaseManager.db.loggerDao

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
        entity.level = "error"
        entity.content = msg
        if (LogLevel.ERROR.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.e(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun w(msg: String?) {
        entity.level = "warn"
        entity.content = msg

        if (LogLevel.WARN.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.e(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun i(msg: String?) {
        entity.level = "info"
        entity.content = msg

        if (LogLevel.INFO.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.i(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun d(msg: String?) {
        entity.level = "debug"
        entity.content = msg

        if (LogLevel.DEBUG.value <= logLevel.value) {
            if (msg.toString().isNotEmpty()) {
                val s = getMethodNames()
                Log.d(TAG, String.format(s, msg))
            }
        }
    }

    @JvmStatic
    fun json(json: String?) {
        entity.level = "json"
        entity.content = json
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
                message = message.replace("\n".toRegex(), "\n║ ")
                val s = getMethodNames()
                println(String.format(s, message))
                return
            }
            if (j.startsWith("[")) {
                val jsonArray = JSONArray(j)
                var message = jsonArray.toString(LoggerPrinter.JSON_INDENT)
                message = message.replace("\n".toRegex(), "\n║ ")
                val s = getMethodNames()
                println(String.format(s, message))
                return
            }
            e("Invalid Json")
        } catch (e: JSONException) {
            e("Invalid Json")
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

        entity.fileName = sElements[stackOffset].fileName
        entity.fucName = sElements[stackOffset].methodName
        entity.lineNum = sElements[stackOffset].lineNumber.toString()
        entity.time = System.currentTimeMillis()
        insertToDatabase()
        return builder.toString()
    }

    private fun insertToDatabase() {
        GlobalScope.launch {
            db.insertLogger(entity)
        }
    }
}