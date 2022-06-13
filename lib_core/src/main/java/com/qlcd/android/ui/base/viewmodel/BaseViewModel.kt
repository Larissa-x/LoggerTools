package com.qlcd.android.ui.base.viewmodel

import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hjq.bar.OnTitleBarListener
import com.qlcd.android.ui.base.view.ViewState
import com.qlcd.android.ui.livedata.SingleLiveData
import com.qlcd.android.ui.logger.LogKit


abstract class BaseViewModel : ViewModel(), IViewModel{
    //当前页面的状态
    val viewState: MutableLiveData<ViewState> = MutableLiveData(ViewState.NormalState)

    //退出当前Activity
    private val _finishCurrentActivityLiveData: SingleLiveData<Boolean> = SingleLiveData()
    var toLoadNum: Int = 0

    fun getFinishLiveData() = _finishCurrentActivityLiveData

    override fun setNormal() {
        setViewState(ViewState.NormalState)
    }

    override fun setRetry() {
        setViewState(ViewState.RetryState)
    }

    override fun setEmpty() {
        setViewState(ViewState.EmptyState)
    }

    override fun setLoading() {
        setViewState(ViewState.LoadingState)
    }

    @Synchronized
    private fun setViewState(state: ViewState) {
        LogKit.i("LoadingState---before--->$toLoadNum")
        if (state == ViewState.LoadingState) {
            toLoadNum = toLoadNum.inc()
            LogKit.i("LoadingState---after--->$toLoadNum")
        } else if (toLoadNum > 0) {
            toLoadNum = toLoadNum.dec()
            LogKit.i("LoadingState---after--->$toLoadNum")
            if (toLoadNum > 0) {
                return
            }
        }

//        if (viewState.value == state) {
//            return
//        }

        if (Looper.getMainLooper() == Looper.myLooper()) {
            viewState.value = state
        } else {
            viewState.postValue(state)
        }
    }

    /**
     * 关闭当前activity
     */
    fun finish() {
        _finishCurrentActivityLiveData.postValue(true)
    }

    override fun onCleared() {
        super.onCleared()
        setViewState(ViewState.DestroyState)
    }

}