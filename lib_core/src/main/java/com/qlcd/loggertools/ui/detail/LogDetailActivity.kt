package com.qlcd.loggertools.ui.detail

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.TimeUtils
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.qlcd.loggertools.ARouterPath
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityLogDetailBinding
import com.qlcd.loggertools.manager.DatabaseManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.util.*

@Route(path = ARouterPath.ACTIVITY_DETAIL)
@AndroidEntryPoint
class LogDetailActivity : BaseActivity() {

    @Autowired(name = "entity")
    @JvmField
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
        try {
            val content = logEntity?.content
            val jsonObject = JSONObject(content)
            var request = jsonObject.optJSONObject("request")
            val response = jsonObject.optJSONObject("response")
            val toString = request.toString().replace("\\", "")
            val requestFormatJson =
                formatJson("${formatTime(logEntity?.time!!, response)}requestBody:$toString")
            response.remove("responseDuration")
            val responseFormatJson = formatJson(response.toString())


            _binding.tvTitle.text = "$requestFormatJson"
            _binding.tvContent.text = "responseBody:$responseFormatJson"
        } catch (e: Exception) {

        }
    }

    private fun initEvent() {
        _binding.tbTitle.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }
        })
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


    private fun formatJson(json: String): String {
        if (json.isEmpty()) return ""
        var startBlank = ""

        val jsonLine = json
            .replace(":{", ": {\n")
            .replace("{\"", "{\n\"")
            .replace(":[", ": [\n")
            .replace("{[", "{\n[")
            .replace("}", "\n}")
            .replace("]", "\n]")
            .replace(",", ",\n")

        val lines = jsonLine.split("\n")
        val result = StringBuilder()
        lines.forEachIndexed { index, s ->
            val prevLastC = lines.getOrNull(index - 1)?.lastOrNull()
            val firstC = s.firstOrNull()
            if (prevLastC == '{' || prevLastC == '[') {
                startBlank += "  "
            } else if (firstC == '}' || firstC == ']') {
                startBlank = startBlank.drop(2)
            }
            result.append(startBlank).append(s).append("\n")
        }
        return result.toString()
    }
}


