package com.qlcd.loggertools.ui.detail

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.TimeUtils
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityLogDetailBinding
import com.qlcd.loggertools.utils.setThrottleClickListener
import com.qlcd.loggertools.widget.KEY_ENTITY
import com.qlcd.loggertools.widget.KEY_RESPONSE
import com.qlcd.loggertools.widget.KEY_RESPONSE_DURATION
import org.json.JSONObject
import java.util.*

class LogDetailActivity : BaseActivity() {

    private val _binding: ActivityLogDetailBinding by binding()
    private val _viewModel: LogDetailViewModel by viewModels()
    override fun bindLayout() = R.layout.activity_log_detail
    override fun bindBaseViewModel() = _viewModel
    override fun bindViews() {
        _binding.viewModel = _viewModel
    }

    override fun doBusiness() {
        initView()
        initEvent()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val logEntity: LoggerEntity? = intent.getParcelableExtra(KEY_ENTITY)
        logEntity?.content?.let {
            if (it.startsWith("{") || it.startsWith("[")) {
                val content = JSONObject(it)
                val jsonObject = JSONObject()
                jsonObject.put("fileName",logEntity.fileName)
                jsonObject.put("funcName",logEntity.funcName)
                jsonObject.put("lineNumber",logEntity.lineNumber)
                jsonObject.put("name",logEntity.level)
                jsonObject.put("time",TimeUtils.date2String(Date(logEntity.time!!)))
                jsonObject.put("module",logEntity.module)
                jsonObject.put("content",content)
                _binding.rvJson.bindData(jsonObject)
            } else {
                val toJson = GsonUtils.toJson(logEntity)
                _binding.rvJson.bindData(toJson)
            }
        }

//        try {
//            logEntity?.content?.let {
//                if (it.startsWith("{") || it.startsWith("[")) {
//                    val jsonObject = JSONObject(logEntity.content!!)
//                    val responseJson = jsonObject.optJSONObject(KEY_RESPONSE)
//                    val formatTime = formatTime(logEntity, responseJson!!)
//                    _binding.tvTitle.text = formatTime
//                    responseJson.remove(KEY_RESPONSE_DURATION)
//                    _binding.rvJson.bindData(jsonObject)
//                } else {
//                    val buffer = StringBuffer()
//                    buffer.appendLine(TimeUtils.date2String(Date(logEntity.time!!)))
//                    buffer.append("文件名：")
//                    buffer.appendLine(logEntity.fileName)
//                    buffer.append("行号：")
//                    buffer.appendLine("${logEntity.lineNumber}")
//                    buffer.append("方法名：")
//                    buffer.appendLine(logEntity.funcName)
//                    buffer.append("所属模块：")
//                    buffer.appendLine(logEntity.module)
//                    buffer.appendLine()
//                    buffer.appendLine(logEntity.content)
//                    _binding.tvTitle.text = buffer.toString()
//                    _binding.rvJson.visibility = View.GONE
//                }
//            }
//        } catch (e: Exception) {
//
//        }

    }

    private fun initEvent() {
        _binding.ivNavBack.setThrottleClickListener {
            finishActivity()
        }
    }

    override fun onBackPressed() {
        finishActivity()
    }

    private fun formatTime(entity: LoggerEntity, response: JSONObject): String {
        val buffer = StringBuffer()
        buffer.append("开始时间：")
        buffer.appendLine(TimeUtils.date2String(Date(entity.time!!), "yyyy-MM-dd HH:mm:ss:SSS"))
        buffer.append("响应时间：")
        buffer.appendLine(TimeUtils.date2String(Date(entity.time!! + response.optLong(
            KEY_RESPONSE_DURATION)), "yyyy-MM-dd HH:mm:ss:SSS"))
        buffer.append("耗时：")
        buffer.appendLine(response.optLong(KEY_RESPONSE_DURATION).toString() + "ms")
        buffer.append("文件名：")
        buffer.appendLine(entity.fileName)
        buffer.append("行号：")
        buffer.appendLine("${entity.lineNumber}")
        buffer.append("方法名：")
        buffer.appendLine(entity.funcName)
        buffer.append("所属模块：")
        buffer.appendLine(entity.module)
        return buffer.toString()
    }
}


