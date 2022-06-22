package com.qlcd.loggertools

import android.app.Application

open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LoggerTools.init(this)
    }
}