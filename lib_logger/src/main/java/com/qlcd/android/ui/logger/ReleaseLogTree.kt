package com.qlcd.android.ui.logger

import timber.log.Timber

class ReleaseLogTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        //todo 这里可以对log信息做各种各样的操作！！！数据库、网络等，不写则Release版不会有输出

    }
}