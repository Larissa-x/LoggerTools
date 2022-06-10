package com.qlcd.android.ui.base.view

/**
 * View的状态
 */
sealed class ViewState {
    object NormalState : ViewState()//正常状态
    object LoadingState : ViewState()//Loading状态
    object EmptyState : ViewState()//空状态
    object RetryState : ViewState()//错误状态
    object DestroyState : ViewState()//回收状态
}