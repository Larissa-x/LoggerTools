package com.qlcd.android.ui.base.view.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.qlcd.android.ui.viewstate.OnViewStateListener
import com.qlcd.android.ui.viewstate.ViewStateManager
import com.qlcd.android.ui.R
import com.qlcd.android.ui.base.view.FragmentState
import com.qlcd.android.ui.base.view.IView
import com.qlcd.android.ui.base.view.ViewState
import com.qlcd.android.ui.base.viewmodel.BaseViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates
import com.qlcd.android.ui.base.view.loading.ZLoadingDialog
import com.qlcd.android.ui.logger.LogKit

abstract class BaseFragment : Fragment(), IView, OnFragmentVisibilityChangedListener,
    View.OnAttachStateChangeListener {
    var rootView: View? = null
    private var viewStateManager: ViewStateManager? = null
    private var dialog: ZLoadingDialog? = null

    //ParentActivity是否可见
    private var parentActivityVisible = false

    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    private var visible = false

    private var localParentFragment: BaseFragment? = null
    private val visibilityListeners = ArrayList<OnFragmentVisibilityChangedListener>()

    fun addOnVisibilityChangedListener(listener: OnFragmentVisibilityChangedListener?) {
        listener?.apply {
            visibilityListeners.add(this)
        }
    }

    fun removeOnVisibilityChangedListener(listener: OnFragmentVisibilityChangedListener?) {
        listener?.apply {
            visibilityListeners.remove(this)
        }
    }

    var state: FragmentState? by Delegates.observable(null) { _, old, new ->
        new.takeIf { it != old }
            .let {
                visibilityListeners.forEach { listener ->
                    listener.onFragmentVisibilityChanged(visible)
                }
                when (it) {
                    FragmentState.VisibleState ->
                        visibleFragment()
                    FragmentState.InvisibleState ->
                        invisibleFragment()
                }
            }
    }

    protected var smartRefreshLayout: SmartRefreshLayout? = null

    //是否有下拉刷新（默认有）
    protected var hasRefresh = true
        set(value) {
            smartRefreshLayout?.setEnableRefresh(value)
            field = value
        }

    //是否有上拉加载（默认有）
    protected var hasLoadMore = true
        set(value) {
            smartRefreshLayout?.setEnableLoadMore(value)
            field = value
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parentFragment = parentFragment
        if (parentFragment != null && parentFragment is BaseFragment) {
            localParentFragment = parentFragment
            localParentFragment?.addOnVisibilityChangedListener(this)
        }
        checkVisibility(true)
    }

    override fun onDetach() {
        localParentFragment?.removeOnVisibilityChangedListener(this)
        super.onDetach()
        checkVisibility(false)
        localParentFragment = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initWindow()
        super.onCreate(savedInstanceState)
        LogKit.i("FragmentName:${this::class.simpleName}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            rootView = inflater.inflate(bindLayout(), container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.addOnAttachStateChangeListener(this)
        initSmartRefreshLayout()
        bindViewState()
        bindViews()
        doBusiness()
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout = rootView?.findViewById(R.id.smartRefreshLayout)
        smartRefreshLayout?.apply {
            setEnableRefresh(hasRefresh)
            setEnableLoadMore(hasLoadMore)
            setOnRefreshListener { it ->
                onRefresh(it)
            }
            setOnLoadMoreListener { it ->
                onLoadMore(it)
            }
        }
    }

    /**
     * 下拉刷新的方法（如果有）
     * @param layout RefreshLayout
     */
    open fun onRefresh(layout: RefreshLayout) {}

    /**
     * 上拉加载的方法（如果有）
     * @param layout RefreshLayout
     */
    open fun onLoadMore(layout: RefreshLayout) {}

    protected inline fun <reified T : ViewDataBinding> binding(): Lazy<T> = lazy {
        val binding: T = DataBindingUtil.bind(rootView!!)!!
        binding.lifecycleOwner = this
        return@lazy binding
    }

    private fun bindViewState() {
        //若ViewModel不为空
        bindBaseViewModel()?.apply {
            viewState.observe(viewLifecycleOwner, {
                when (it) {
                    ViewState.NormalState -> showContent()
                    ViewState.EmptyState -> showEmpty()
                    ViewState.LoadingState -> showLoading()
                    ViewState.RetryState -> showRetry()
                }
                dialog?.apply {
                    if (it != ViewState.LoadingState && isShowing) {
                        dismiss()
                    }
                }
            })
        }
        bindShowEmptyView()?.let {
            viewStateManager = ViewStateManager(it, onViewStateListener(), this@BaseFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        onActivityVisibilityChanged(true)
    }

    override fun onPause() {
        super.onPause()
        onActivityVisibilityChanged(false)
    }

    /**
     * ParentActivity可见性改变
     */
    protected fun onActivityVisibilityChanged(visible: Boolean) {
        parentActivityVisible = visible
        checkVisibility(visible)
    }

    /**
     * ParentFragment可见性改变
     */
    override fun onFragmentVisibilityChanged(visible: Boolean) {
        checkVisibility(visible)
    }

    @CallSuper
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        checkVisibility(isVisibleToUser)
    }

    @CallSuper
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        checkVisibility(!hidden)
    }

    override fun onViewAttachedToWindow(v: View?) {
        checkVisibility(true)
    }

    override fun onViewDetachedFromWindow(v: View) {
        v.removeOnAttachStateChangeListener(this)
        checkVisibility(false)
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
     * 将ViewModel传给父布局做基础处理
     */
    abstract fun bindBaseViewModel(): BaseViewModel?

    abstract fun bindLayout(): Int

    /**
     * 绑定View
     */
    abstract fun bindViews()

    /**
     * 业务逻辑代码
     */
    abstract fun doBusiness()

    /**
     * 进行初始化Window的操作
     * 在绑定View前执行
     */
    open fun initWindow() {
    }

    /**
     * 当fragment可见时回调该方法
     * 注：fragment启动过程也会回调
     */
    open fun visibleFragment() {
        LogKit.i(this.javaClass.simpleName + "---->当前fragment可见")
    }

    /**
     * 当fragment不可见时回调该方法
     * 注：fragment销毁过程也会回调
     */
    open fun invisibleFragment() {
        LogKit.i(this.javaClass.simpleName + "---->当前fragment不可见")
    }

    /**
     * 检查可见性是否变化
     * @param expected 可见性期望的值。只有当前值和expected不同，才需要做判断
     */
    private fun checkVisibility(expected: Boolean) {
        if (expected == visible) return
        val parentVisible =
            if (localParentFragment == null) parentActivityVisible
            else localParentFragment?.isFragmentVisible() ?: false
        val superVisible = super.isVisible()
        val hintVisible = userVisibleHint
        val visible = parentVisible && superVisible && hintVisible
        LogKit.i(String.format("==> checkVisibility = %s  ( parent = %s, super = %s, hint = %s )", visible, parentVisible, superVisible, hintVisible))
        if (visible != this.visible) {
            this.visible = visible
            state = if (visible) FragmentState.VisibleState else FragmentState.InvisibleState
        }
    }

    /**
     * 是否可见（Activity处于前台、Tab被选中、Fragment被添加、Fragment没有隐藏、Fragment.View已经Attach）
     */
    fun isFragmentVisible(): Boolean {
        return visible
    }

    /**
     * 展示正常页面
     * 不可直接调用
     */
    protected fun showContent() {
        viewStateManager?.showContent()
    }

    /**
     * 展示空页面
     * 不可直接调用
     */
    protected fun showEmpty() {
        viewStateManager?.showEmpty()
    }

    /**
     * Loading
     * 不可直接调用
     */
    protected fun showLoading() {
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
            dialog = ZLoadingDialog(requireContext())
                .setLoadingColor(Color.WHITE)
//                .setHintText(AppUtils.getAppName())
//                .setHintTextColor(Color.GRAY)
        }
        dialog?.show()

    }

    /**
     * 展示重试页面
     * 不可直接调用
     */
    protected fun showRetry() {
        viewStateManager?.showRetry()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewStateManager?.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

}