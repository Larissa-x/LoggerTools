package com.qlcd.android.ui.base.view

sealed class FragmentState {
    object VisibleState : FragmentState()
    object InvisibleState : FragmentState()
}