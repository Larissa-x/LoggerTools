package com.qlcd.android.ui.base.view.activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ColorUtils
import com.gyf.immersionbar.ImmersionBar
import com.qlcd.android.ui.viewstate.OnViewStateListener
import com.qlcd.android.ui.viewstate.ViewStateManager
import com.qlcd.android.ui.R
import com.qlcd.android.ui.base.view.IView
import com.qlcd.android.ui.base.view.ViewState
import com.qlcd.android.ui.base.view.loading.ZLoadingDialog
import com.qlcd.android.ui.base.viewmodel.BaseViewModel
import com.qlcd.android.ui.logger.LogKit
import org.greenrobot.eventbus.EventBus


abstract class BaseActivity :
    AppCompatActivity(), IView {
    private var dialog: ZLoadingDialog? = null
    private var viewStateManager: ViewStateManager? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogKit.i("ActivityName:${this::class.simpleName}")
        setStatusBar()
        initWindow()
        ARouter.getInstance().inject(this)
        bindViews()
        bindViewState()
        doBusiness()
    }

    protected inline fun <reified T : ViewDataBinding> binding(): Lazy<T> = lazy {
        val binding : T =  DataBindingUtil.setContentView(this,bindLayout())
        binding.lifecycleOwner = this
        return@lazy binding
    }

    private fun bindViewState() {
        //若ViewModel不为空
        bindBaseViewModel()?.apply {
            viewState.observe(this@BaseActivity) {
                when (it) {
                    ViewState.NormalState -> showContent()
                    ViewState.EmptyState -> showEmpty()
                    ViewState.LoadingState -> showLoading()
                    ViewState.RetryState -> showRetry()
                    ViewState.DestroyState -> return@observe
                }
                dialog?.apply {
                    if (it != ViewState.LoadingState && isShowing) {
                        dismiss()
                    }
                }
            }
        }

        bindShowEmptyView()?.let {
            viewStateManager = ViewStateManager(it, onViewStateListener())
        }

        bindBaseViewModel()?.apply {
            getFinishLiveData().observe(this@BaseActivity) {
                this@BaseActivity.finish()
            }
        }
    }

    /**
     * 绑定展示空页面时替换的布局
     */
    open fun bindShowEmptyView(): View? {
        return null
    }

    /**
     * View状态相关的监听事件
     */
    open fun onViewStateListener(): OnViewStateListener {
        return object : OnViewStateListener {
            override fun bindRetryView(): Int {
                return R.layout.view_empty
            }

            override fun bindEmptyView(): Int {
                return R.layout.view_empty
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

    /**
     * 展示正常页面
     * 不可直接调用
     */
    private fun showContent() {
        viewStateManager?.showContent()
    }

    /**
     * 展示空页面
     * 不可直接调用
     */
    private fun showEmpty() {
        viewStateManager?.showEmpty()
    }

    /**
     * Loading
     * 不可直接调用
     */
    private fun showLoading() {
        dialog?.apply {
            if (isShowing) {
                return@showLoading
            }
        }
        viewStateManager?.apply {
            if (showLoading()) {
                return@showLoading
            }
        }
        if (dialog == null) {
            dialog = ZLoadingDialog(this)
                .setLoadingColor(ColorUtils.getColor(R.color.c_AppColor_01))
//                .setHintText(AppUtils.getAppName())
//                .setHintTextColor(Color.GRAY)
        }
        dialog?.show()
    }

    /**
     * 展示重试页面
     * 不可直接调用
     */
    private fun showRetry() {
        viewStateManager?.showRetry()
    }


    protected open fun setStatusBar() {
        setStatusBarColor(Color.parseColor("#ffffff"))
    }


    /**
     * 设置状态栏的背景颜色
     */
    fun setStatusBarColor(@ColorInt color: Int) {
        ImmersionBar
            .with(this)
            .statusBarColorInt(color)
            .navigationBarColorInt(0xffffff)
            .autoDarkModeEnable(true)
            .fitsSystemWindows(true)
            .keyboardEnable(true)
            .init()
    }



    override fun onDestroy() {
        super.onDestroy()
        viewStateManager?.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}
