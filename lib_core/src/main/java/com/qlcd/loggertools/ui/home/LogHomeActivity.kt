package com.qlcd.loggertools.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
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
import com.qlcd.loggertools.logger.LogKit
import com.qlcd.loggertools.ui.detail.LogDetailActivity
import com.qlcd.loggertools.widget.dialog.BaseDialog
import com.qlcd.loggertools.widget.dialog.DialogViewConverter
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
    private val listAdapter = LogHomeListAdapter()
    private var searchJob: Job? = null

    override fun bindViews() {
        _binding.vm = _viewModelLog
    }

    override fun doBusiness() {
        initClick()
        initFilter()
        initList()
        _viewModelLog.keywords.observe(this) {
            if (it == _viewModelLog.prevKeywords) return@observe
            _viewModelLog.prevKeywords = it
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                if (it.isNotEmpty()) {
                    delay(500)
                }
                listAdapter.setNewInstance(LogKit.getLogData().filter { e -> e.content.orEmpty().contains(it) }.toMutableList())
            }
        }
    }

    private fun initList() {
        _binding.rv.adapter = listAdapter
        listAdapter.setNewInstance(LogKit.getLogData().toMutableList())
        listAdapter.setOnItemClickListener { _, _, position ->
            val loggerEntity = listAdapter.data[position]
            val intent = Intent(context, LogDetailActivity::class.java)
            intent.putExtra("entity", loggerEntity)
            startActivity(intent)
        }
        _binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) {
                    // 上滑显示
                } else if (dy > 0) {
                    // 下滑隐藏
                }
            }
        })
    }

    private fun initClick() {
        _binding.ivClose.setThrottleClickListener {
            onBackPressed()
        }
        _binding.ivFilter.setThrottleClickListener {
            showFilter()
        }
        _binding.tvClean.setThrottleClickListener {
            BaseDialog(
                viewConverter = object : DialogViewConverter() {
                    override fun convertView(dialogView: View, dialog: DialogFragment) {
                        dialogView.run {
                            findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
                                dialog.dismiss()
                            }
                            findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
                                LogKit.cleanData()
                                listAdapter.setNewInstance(null)
                                dialog.dismiss()
                            }
                        }
                    }
                }
            ).show(supportFragmentManager)
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

private class LogHomeListAdapter :
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


