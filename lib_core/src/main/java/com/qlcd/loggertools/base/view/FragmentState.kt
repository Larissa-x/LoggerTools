package com.qlcd.loggertools.base.view

sealed class FragmentState {
    object VisibleState : FragmentState()
    object InvisibleState : FragmentState()
}