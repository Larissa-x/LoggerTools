package com.qlcd.loggertools.base.viewmodel

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qlcd.loggertools.base.view.ViewState
import com.qlcd.loggertools.livedata.SingleLiveData


abstract class BaseViewModel : ViewModel(), IViewModel {
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
        if (state == ViewState.LoadingState) {
            toLoadNum = toLoadNum.inc()
        } else if (toLoadNum > 0) {
            toLoadNum = toLoadNum.dec()
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

class MutableIntLiveData(value: Int = 0) : MutableLiveData<Int>(value) {
    override fun getValue(): Int {
        return super.getValue() ?: 0
    }
}

open class IntLiveData(value: Int = 0) : LiveData<Int>(value) {
    override fun getValue(): Int {
        return super.getValue() ?: 0
    }
}

class MutableStringLiveData(value: String = "") : MutableLiveData<String>(value) {
    override fun getValue(): String {
        return super.getValue() ?: ""
    }
}

open class StringLiveData(value: String = "") : LiveData<String>(value) {
    override fun getValue(): String {
        return super.getValue() ?: ""
    }
}

class MutableBooleanLiveData(value: Boolean = false) : MutableLiveData<Boolean>(value) {
    override fun getValue(): Boolean {
        return super.getValue() ?: false
    }

    fun toggle() {
        postValue(!value)
    }
}

open class BooleanLiveData(value: Boolean = false) : LiveData<Boolean>(value) {
    override fun getValue(): Boolean {
        return super.getValue() ?: false
    }
}