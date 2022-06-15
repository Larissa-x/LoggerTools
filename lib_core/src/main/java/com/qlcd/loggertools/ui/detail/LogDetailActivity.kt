package com.qlcd.loggertools.ui.detail

import androidx.activity.viewModels
import com.blankj.utilcode.util.TimeUtils
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityLogDetailBinding
import org.json.JSONObject
import java.lang.Exception
import java.util.*

class LogDetailActivity : BaseActivity() {


    var logEntity: LoggerEntity? = null

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

    private fun initView() {
        logEntity = intent.getParcelableExtra("entity")
        try {
            logEntity?.content?.let {
                if (it.startsWith("{") || it.startsWith("[")) {
                    val jsonObject = JSONObject(logEntity?.content!!)
                    val responseJson = jsonObject.optJSONObject("response")
                    val formatTime = formatTime(logEntity?.time!!, responseJson!!)
                    _binding.tvTitle.text = formatTime
                    responseJson.remove("responseDuration")
                    _binding.rvJson.bindData(jsonObject)
                } else {
                    _binding.tvTitle.text =
                        "${TimeUtils.date2String(Date(logEntity?.time!!))}\n${logEntity?.content}"
                }
            }
        } catch (e: Exception) {

        }

    }

    private fun initEvent() {
        _binding.ivNavBack.setOnClickListener {
            finish()
        }
    }

    private fun formatTime(startTime: Long, response: JSONObject): String {
        val buffer = StringBuffer()
        buffer.append("开始时间：")
        buffer.appendLine(TimeUtils.date2String(Date(startTime)))
        buffer.append("响应时间：")
        buffer.appendLine(TimeUtils.date2String(Date(startTime + response.optLong("responseDuration"))))
        buffer.append("耗时：")
        buffer.appendLine(response.optLong("responseDuration").toString())
        return buffer.toString()
    }
}


