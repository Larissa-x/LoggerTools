package com.qlcd.loggertools.base.view.fragment

import android.os.Bundle
import android.view.View

abstract class BaseLazyFragment : BaseFragment() {
    //是否第一次加载
    protected var isFirst: Boolean = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isFirst = true
    }
    /**
     * 懒加载
     */
    abstract fun onLazyLoad()

    override fun visibleFragment() {
        super.visibleFragment()
        if (isFirst){
            onLazyLoad()
            isFirst = false
        }
    }
}