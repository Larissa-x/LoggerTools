package com.qlcd.android.loggertools

import android.content.Intent
import androidx.activity.viewModels
import com.blankj.utilcode.util.GsonUtils
import com.qlcd.android.loggertools.databinding.ActivityTestBinding
import com.qlcd.loggertools.LoggerTools.context
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.logger.LogKit
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
        //LogKit.x 不管哪种类型的日志，都会插入到数据库中
        _binding.btnInsert.setOnClickListener {
            val hashMapOf = hashMapOf<String, Any>()
            hashMapOf["code"] = "200"
            hashMapOf["msg"] = "请求成功"

            val hashMap = hashMapOf<String, String>()
            hashMap["name"] = "小明"
            hashMapOf["data"] = hashMap

            val toJson = GsonUtils.toJson(hashMapOf)
            LogKit.json(toJson)

        }

        var click = 0
        _binding.btnQuery.setOnClickListener {
            click++
            //查询
            if (click % 2 == 0) {
                _viewModel.requestQuery()
            } else {
                LoggerDataManager.insertToDatabase("debug", "日志内容日志内容日志内容日志内容日志内容日志内容日志内容日志内容日志内容日志内容")
            }
        }

        _binding.btnHome.setOnClickListener {
            startActivity(Intent(context, LogListActivity::class.java))

        }
    }

}


