package com.qlcd.android.loggertools

import android.view.View
import com.blankj.utilcode.util.GsonUtils
import com.qlcd.android.ui.logger.LogKit
import androidx.activity.viewModels
import com.blankj.utilcode.util.LogUtils
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.qlcd.android.loggertools.databinding.ActivityMainBinding
import com.qlcd.android.ui.base.view.activity.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val _binding: ActivityMainBinding by binding()
    private val _viewModel: MainViewModel by viewModels()
    override fun bindLayout() = R.layout.activity_main
    override fun bindBaseViewModel() = _viewModel
    override fun bindViews() {
        _binding.viewModel = _viewModel
    }

    override fun doBusiness() {
        initEvent()

    }

    private fun initEvent(){

        _binding.tbTitle.setOnTitleBarListener(object :OnTitleBarListener{
            override fun onLeftClick(titleBar: TitleBar?) {
                super.onLeftClick(titleBar)
                finish()
            }
        })
        //LogKit.x 不管哪种类型的日志，都会插入到数据库中
        _binding.btnInsert.setOnClickListener {
            val hashMapOf = hashMapOf<String, Any>()
            hashMapOf["code"] = "200"
            hashMapOf["msg"] = "请求成功"

            val hashMap = hashMapOf<String, String>()
            hashMap["name"] = "小明"
            hashMapOf["data"] = hashMap

            val toJson = GsonUtils.toJson(hashMapOf);
            LogKit.json(toJson)
        }

        _binding.btnQuery.setOnClickListener {
            //查询
            _viewModel.requestQuery()
        }
    }

}


