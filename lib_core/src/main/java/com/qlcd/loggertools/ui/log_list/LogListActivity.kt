package com.qlcd.loggertools.ui.log_list

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.qlcd.loggertools.BaseApplication
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.activity.BaseActivity
import com.qlcd.loggertools.database.entity.ApiEntity
import com.qlcd.loggertools.database.entity.LoggerEntity
import com.qlcd.loggertools.databinding.ActivityLogListBinding
import com.qlcd.loggertools.ext.setThrottleClickListener
import com.qlcd.loggertools.ui.detail.LogDetailActivity
import com.qlcd.loggertools.widget.KEY_ENTITY
import com.qlcd.loggertools.widget.STR_ERROR
import com.qlcd.loggertools.widget.STR_JSON
import com.qlcd.loggertools.widget.dialog.BaseDialog
import com.qlcd.loggertools.widget.dialog.DialogViewConverter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class LogListActivity : BaseActivity() {

    private val _binding: ActivityLogListBinding by binding()
    private val _viewModel: LogListViewModel by viewModels()
    override fun bindLayout() = R.layout.activity_log_list
    override fun bindBaseViewModel() = _viewModel
    private var searchJob: Job? = null
    private lateinit var _adapter: LogHomeListAdapter
    private val _filterListAdapter = FilterListAdapter()
    override fun bindViews() {
        _binding.viewModel = _viewModel
    }

    override fun doBusiness() {
        initView()
        initEvent()

        //默认选择倒序
        _binding.rbDesc.isChecked = true
        _filterListAdapter.setNewInstance(_viewModel.getFilterLabel().toMutableList())
        _viewModel.getData(getSortType())
    }

    private fun initView() {
        _binding.rvSelectedFilter.run {
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
            adapter = _filterListAdapter
        }

        _binding.rvList.layoutManager = LinearLayoutManager(this)
        _adapter = LogHomeListAdapter()
        _binding.rvList.adapter = _adapter

        _adapter.setOnItemClickListener { _, _, position ->
            val loggerEntity = _adapter.data[position]
            val intent = Intent(BaseApplication.context, LogDetailActivity::class.java)
            intent.putExtra(KEY_ENTITY, loggerEntity)
            startActivity(intent)
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun initEvent() {
        _binding.ivClose.setThrottleClickListener {
            onBackPressed()
        }

        //右上角抽屉按钮开关
        _binding.ivFilter.setThrottleClickListener {
            resetFilterState()
            _binding.drawLayout.openDrawer(Gravity.RIGHT)
        }

        //点击自启动方式获取数据
        _binding.tvStarting.setThrottleClickListener {
            _viewModel.isDateFilter.value = false
            _viewModel.dateTextFilter.value = ""
        }

        //点击开始根据日期筛选数据
        _binding.tvDateFilter.setThrottleClickListener {
            showDateSelectorDialog()
        }

        _binding.btnReset.setThrottleClickListener {
            //默认选择倒序
            _binding.rbDesc.isChecked = true
            _viewModel.dateTextFilter.value = ""
            _viewModel.isDateFilter.value = false
            _viewModel.prevSortType = LogListViewModel.DESC
            _viewModel.prevDateFlag = false
            _viewModel.prevDateText = ""
            _filterListAdapter.setNewInstance(_viewModel.getFilterLabel().toMutableList())
            _viewModel.getData(getSortType())
        }

        _binding.btnConfirm.setThrottleClickListener {
            if (_viewModel.isDateFilter.value) {
                if (_viewModel.dateTextFilter.value.isEmpty()) {
                    ToastUtils.showShort("请先选择日期")
                    return@setThrottleClickListener
                }
            }
            // 选择完后记录下选择的数据
            _viewModel.prevSortType = getSortType()
            _viewModel.prevDateFlag = _viewModel.isDateFilter.value
            _viewModel.prevDateText = _viewModel.dateTextFilter.value

            _binding.drawLayout.closeDrawers()
            _filterListAdapter.setNewInstance(_viewModel.getFilterLabel().toMutableList())
            _viewModel.getData(getSortType())
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
                                _viewModel.cleanData()
                                _adapter.setNewInstance(null)
                                dialog.dismiss()
                            }
                        }
                    }
                }
            ).show(supportFragmentManager)
        }
        _viewModel.keywords.observe(this) {
            if (it == _viewModel.prevKeywords) return@observe
            _viewModel.prevKeywords = it
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                if (it.isNotEmpty()) {
                    delay(500)
                }
                _adapter.setNewInstance(
                    _viewModel.loggerListLivedata.value?.filter { e -> e.content.orEmpty().contains(it) }?.toMutableList()
                )
            }
        }
        //true：指定日期   false：自启动
        _viewModel.isDateFilter.observe(this) {
            if (it && _viewModel.dateTextFilter.value.isNotEmpty()) {
                //指定日期获取数据
                _binding.tvDateFilter.setBackgroundResource(R.drawable.bg_sort_true)
                _binding.tvDateFilter.setTextColor(ColorUtils.getColor(R.color.app_color_white))
                _binding.tvStarting.setBackgroundResource(R.drawable.bg_sort_false)
                _binding.tvStarting.setTextColor(ColorUtils.getColor(R.color.app_color_black))
            } else {
                _binding.tvDateFilter.setBackgroundResource(R.drawable.bg_sort_false)
                _binding.tvDateFilter.setTextColor(ColorUtils.getColor(R.color.app_color_black))
                _binding.tvStarting.setBackgroundResource(R.drawable.bg_sort_true)
                _binding.tvStarting.setTextColor(ColorUtils.getColor(R.color.app_color_white))
            }
        }
        _viewModel.loggerListLivedata.observe(this) {
            _adapter.setNewInstance(it.toMutableList())
        }
    }

    private fun getSortType(): String {
        return if (_binding.rgSort.checkedRadioButtonId == R.id.rb_asc) {
            LogListViewModel.ASC
        } else {
            LogListViewModel.DESC
        }
    }

    /**选择完筛选项后，只有点击确定，选择的筛选信息才生效，否则需重置选择前的状态*/
    private fun resetFilterState() {
        _binding.rbDesc.isChecked = _viewModel.prevSortType == LogListViewModel.DESC
        _binding.rbAsc.isChecked = _viewModel.prevSortType == LogListViewModel.ASC
        _viewModel.isDateFilter.value = _viewModel.prevDateFlag
        _viewModel.dateTextFilter.value = _viewModel.prevDateText
    }

    private fun showDateSelectorDialog() {
        TimePickerBuilder(
            this
        ) { date, _ ->
            _viewModel.dateTextFilter.value = TimeUtils.date2String(date, "yyyy-MM-dd")
            _viewModel.isDateFilter.value = true
        }
            .setDate(Calendar.getInstance())
            .build()
            .show()
    }

    override fun onBackPressed() {
        if (_binding.drawLayout.isDrawerOpen(GravityCompat.END)) {
            _binding.drawLayout.closeDrawers()
            return
        }
        super.onBackPressed()
    }
}

private class LogHomeListAdapter :
    BaseQuickAdapter<LoggerEntity, BaseViewHolder>(R.layout.rv_item_log_list) {
    override fun convert(holder: BaseViewHolder, item: LoggerEntity) {
        holder.setText(R.id.tv_level, item.level)
            .setTextColorRes(
                R.id.tv_level, if (item.level.equals(STR_ERROR, true)) {
                    R.color.app_color_red
                } else {
                    R.color.app_color_gray
                }
            )
            .setText(R.id.tv_date, TimeUtils.date2String(Date(item.time!!)))
        if (item.level.equals(STR_JSON, true) && item.content.orEmpty().startsWith("{")) {
            holder.setText(
                R.id.tv_content,
                "code: ${ApiEntity().parseJson(item.content.orEmpty()).response.code}  path: ${
                    ApiEntity().parseJson(item.content.orEmpty()).request.path
                }"
            )
        } else {
            holder.setText(R.id.tv_content, item.content)
        }
    }
}

private class FilterListAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.rv_item_filter) {

    override fun convert(holder: BaseViewHolder, item: String) {
        (holder.itemView as TextView).text = item
    }
}




