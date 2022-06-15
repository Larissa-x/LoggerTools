package com.qlcd.loggertools.ui.home

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.qlcd.loggertools.BaseApplication.Companion.context
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.ApiEntity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityLogHomeBinding
import com.qlcd.loggertools.ext.dpToPx
import com.qlcd.loggertools.ext.setThrottleClickListener
import com.qlcd.loggertools.ui.detail.LogDetailActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by GaoLuHan on 2022/6/13
 * Describe:
 */
class HomeActivity : BaseActivity() {

    private val _binding: ActivityLogHomeBinding by binding()
    private val _viewModelLog: LogHomeViewModel by viewModels()
    override fun bindLayout(): Int = R.layout.activity_log_home
    override fun bindBaseViewModel() = _viewModelLog
    private val listAdapter = HomeListAdapter()
    private var searchJob: Job? = null

    override fun bindViews() {
        _binding.vm = _viewModelLog
    }

    override fun doBusiness() {
        initClick()
        initFilter()
        initList()

        _viewModelLog.getData()
        _viewModelLog.listLiveData.observe(this) {
            listAdapter.setNewInstance(it.toMutableList())
        }

        _viewModelLog.keywords.observe(this) {
            if (it == _viewModelLog.prevKeywords) return@observe
            _viewModelLog.prevKeywords = it
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                if (it.isNotEmpty()) {
                    delay(500)
                }
                listAdapter.setNewInstance(listAdapter.data.filter { e-> e.content.orEmpty().contains(it) }.toMutableList())
            }
        }
    }

    private fun initList() {
        _binding.rv.adapter = listAdapter
        listAdapter.setOnItemClickListener { adapter, view, position ->
            val loggerEntity = listAdapter.data[position]
            val intent = Intent(context, LogDetailActivity::class.java)
            intent.putExtra("entity", loggerEntity)
            startActivity(intent)
        }
    }

    private fun initClick() {
        _binding.ivClose.setThrottleClickListener {
            onBackPressed()
        }
        _binding.ivFilter.setThrottleClickListener {
            showFilter()
        }
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


