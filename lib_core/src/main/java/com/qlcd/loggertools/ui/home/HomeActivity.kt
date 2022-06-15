package com.qlcd.loggertools.ui.home

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qlcd.loggertools.BaseApplication.Companion.context
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.ApiEntity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityHomeBinding
import com.qlcd.loggertools.ext.dpToPx
import com.qlcd.loggertools.ext.setThrottleClickListener
import com.qlcd.loggertools.ui.detail.LogDetailActivity
import java.util.*

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
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
            val intent = Intent(context, LogDetailActivity::class.java)
            intent.putExtra("entity", loggerEntity)
            startActivity(intent)
        }
        initFilter()
    }

    override fun onBackPressed() {
        if (_binding.flFilterContainer.isVisible) {
            hideFilter()
        } else {
            finish()
        }
    }


    private fun initFilter() {
        _binding.bgFilter.setThrottleClickListener {
            hideFilter()
        }
    }

    private fun showFilter() {
//        resetFilterData()

        _binding.bgFilter.animate().alpha(1f)
        _binding.clFilter.animate().translationX(0f)
            .withStartAction {
                _binding.flFilterContainer.visibility = View.VISIBLE
            }
    }

    private fun hideFilter() {
        _binding.bgFilter.animate().alpha(0f)
        _binding.clFilter.animate().translationX(300.dpToPx)
            .withEndAction { _binding.flFilterContainer.visibility = View.GONE }
    }
}

private class HomeListAdapter :
    BaseQuickAdapter<LoggerEntity, BaseViewHolder>(R.layout.rv_item_home) {
    override fun convert(holder: BaseViewHolder, item: LoggerEntity) {
        holder.setText(R.id.tv_level, item.level)
            .setText(R.id.tv_date, TimeUtils.date2String(Date(item.time!!)))
        if (item.level.equals("json", true) && item.content.orEmpty().startsWith("{")) {
            holder.setText(
                R.id.tv_content,
                "code: ${ApiEntity().parseJson(item.content.orEmpty()).response.code}  path: ${ApiEntity().parseJson(item.content.orEmpty()).request.path}"
            )
        } else {
            holder.setText(R.id.tv_content, item.content)
        }

    }
}


