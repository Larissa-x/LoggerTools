package com.qlcd.loggertools.ui.home

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qlcd.loggertools.ARouterPath
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
@AndroidEntryPoint
@Route(path = ARouterPath.ACTIVITY_HOME)
class HomeActivity : BaseActivity() {

    private val _binding: ActivityHomeBinding by binding()
    private val _viewModel: HomeViewModel by viewModels()
    override fun bindLayout(): Int = R.layout.activity_home
    override fun bindBaseViewModel() = _viewModel
    private val listAdapter = HomeListAdapter()

    override fun bindViews() {
        _binding.vm = _viewModel
    }

    override fun doBusiness() {
        _viewModel.keywords.observe(this) {
        }
        _binding.rv.adapter = listAdapter

        listAdapter.setNewInstance(_viewModel.getData().toMutableList())
    }


}

private class HomeListAdapter : BaseQuickAdapter<LoggerEntity, BaseViewHolder>(R.layout.rv_item_home) {
    override fun convert(holder: BaseViewHolder, item: LoggerEntity) {

    }
}
