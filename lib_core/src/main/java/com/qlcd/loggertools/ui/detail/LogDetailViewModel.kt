package com.qlcd.loggertools.ui.detail

import com.qlcd.loggertools.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val repository: LogDetailRepository,
) : BaseViewModel() {

}