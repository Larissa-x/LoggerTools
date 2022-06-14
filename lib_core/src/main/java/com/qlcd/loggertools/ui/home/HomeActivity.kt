package com.qlcd.loggertools.ui.home

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qlcd.loggertools.ARouterPath
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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
        _viewModel.listLiveData.observe(this) {
            listAdapter.setNewInstance(it.toMutableList())
        }

        _viewModel.getData()
        listAdapter.setOnItemClickListener { adapter, view, position ->
            val loggerEntity = listAdapter.data[position]
            ARouter.getInstance()
                .build(ARouterPath.ACTIVITY_DETAIL)
                .withParcelable("entity",loggerEntity)
                .navigation()
        }
    }


}

private class HomeListAdapter :
    BaseQuickAdapter<LoggerEntity, BaseViewHolder>(R.layout.rv_item_home) {
    override fun convert(holder: BaseViewHolder, item: LoggerEntity) {
        holder.setText(R.id.tv_state,"Success")
        holder.setText(R.id.tv_date, TimeUtils.date2String(Date(item.time!!)))
        holder.setText(R.id.tv_content,item.content)
    }
}
