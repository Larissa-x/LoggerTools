package com.qlcd.android.loggertools

import androidx.activity.viewModels
import com.qlcd.android.loggertools.databinding.ActivityTestBinding
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.manager.LoggerDataManager
import com.qlcd.loggertools.ui.list.LogListActivity

class MainActivity : BaseActivity() {

    private val _binding: ActivityTestBinding by binding()
    private val _viewModel: MainViewModel by viewModels()
    override fun bindLayout(): Int {
        return R.layout.activity_test
    }

    override fun bindBaseViewModel() = _viewModel
    override fun bindViews() {
        _binding.viewModel = _viewModel
    }

    override fun doBusiness() {
        initEvent()
    }

    private fun initEvent() {
        val json = "{ \"request\": {\n" +
                "         \"method\": \"POST\",\n" +
                "         \"scheme\": \"http\",\n" +
                "         \"host\": \"39.107.85.70\",\n" +
                "         \"port\": 8301,\n" +
                "         \"path\": \"\\/app\\/relation\\/follow\",\n" +
                "         \"header\": {\n" +
                "           \"os\": \"Android\",\n" +
                "           \"version\": \"1.0.0\"\n" +
                "         },\n" +
                "         \"params\": {\n" +
                "           \"password\": \"admin.123\",\n" +
                "           \"phone\": \"13521402817\"\n" +
                "         }\n" +
                "       },\n" +
                "       \"response\": {\n" +
                "         \"code\": 405,\n" +
                "         \"msg\": \"令牌不能为空\",\n" +
                "         \"responseDuration\": 75\n" +
                "       }\n" +
                "     }"
        //LogKit.x 不管哪种类型的日志，都会插入到数据库中
        _binding.btnInsertJson.setOnClickListener {
            LoggerDataManager.insertToDatabase("json", json)
        }

        _binding.btnInsertOther.setOnClickListener {
            LoggerDataManager.insertToDatabase("debug", "日志内容日志内容日志内容日志内容日志内容日志内容日志内容日志内容日志内容日志内容")
        }

        _binding.btnHome.setOnClickListener {
            LogListActivity.start(this)
        }
    }

}


