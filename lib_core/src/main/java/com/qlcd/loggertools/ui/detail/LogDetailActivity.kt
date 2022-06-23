package com.qlcd.loggertools.ui.detail

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
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


    private var logEntity: LoggerEntity? = null

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
        logEntity = intent.getParcelableExtra(KEY_ENTITY)
        try {
            logEntity?.content?.let {
                if (it.startsWith("{") || it.startsWith("[")) {
                    val jsonObject = JSONObject(logEntity?.content!!)
                    val responseJson = jsonObject.optJSONObject(KEY_RESPONSE)
                    val formatTime = formatTime(logEntity?.time!!, responseJson!!)
                    _binding.tvTitle.text = formatTime
                    responseJson.remove(KEY_RESPONSE_DURATION)
                    _binding.rvJson.bindData(jsonObject)
                } else {
                    _binding.tvTitle.text =
                        "${TimeUtils.date2String(Date(logEntity?.time!!))}\n\n${logEntity?.content}"
                    _binding.rvJson.visibility = View.GONE
                }
            }
        } catch (e: Exception) {

        }

    }

    private fun initEvent() {
        _binding.ivNavBack.setThrottleClickListener {
            finish()
        }
    }

    private fun formatTime(startTime: Long, response: JSONObject): String {
        val buffer = StringBuffer()
        buffer.append("开始时间：")
        buffer.appendLine(TimeUtils.date2String(Date(startTime), "yyyy-MM-dd HH:mm:ss:SSS"))
        buffer.append("响应时间：")
        buffer.appendLine(TimeUtils.date2String(Date(startTime + response.optLong(KEY_RESPONSE_DURATION)), "yyyy-MM-dd HH:mm:ss:SSS"))
        buffer.append("耗时：")
        buffer.append(response.optLong(KEY_RESPONSE_DURATION).toString() + "ms")
        return buffer.toString()
    }
}


