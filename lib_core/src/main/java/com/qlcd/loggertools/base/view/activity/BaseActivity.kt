package com.qlcd.loggertools.base.view.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.BarUtils.getStatusBarHeight
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.ResourceUtils
import com.qlcd.loggertools.BaseApplication.Companion.context
import com.qlcd.loggertools.R
import com.qlcd.loggertools.base.view.IView
import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import com.qlcd.loggertools.logger.LogKit


abstract class BaseActivity :
    AppCompatActivity(), IView {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogKit.i("ActivityName:${this::class.simpleName}")
        setStatusBar()
        initWindow()
        bindViews()
        bindViewState()
        doBusiness()
    }

    protected inline fun <reified T : ViewDataBinding> binding(): Lazy<T> = lazy {
        val binding: T = DataBindingUtil.setContentView(this, bindLayout())
        binding.lifecycleOwner = this
        return@lazy binding
    }

    private fun bindViewState() {
        bindBaseViewModel()?.apply {
            getFinishLiveData().observe(this@BaseActivity) {
                this@BaseActivity.finish()
            }
        }
    }

    /**
     * 绑定布局
     */
    abstract fun bindLayout(): Int

    /**
     * 将ViewModel传给父布局做基础处理
     */
    abstract fun bindBaseViewModel(): BaseViewModel?

    /**
     * 绑定View
     */
    abstract fun bindViews()

    abstract fun doBusiness()

    /**
     * 进行初始化Window的操作
     * 在setContentView前执行
     */
    open fun initWindow() {}


    protected open fun setStatusBar() {
        BarUtils.setStatusBarColor(this, ColorUtils.getColor(R.color.app_color_white))
        BarUtils.setStatusBarLightMode(this,true)
    }

}
