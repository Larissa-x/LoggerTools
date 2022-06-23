package com.qlcd.android.loggertools

import android.app.Application
import com.qlcd.loggertools.LoggerTools

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        LoggerTools.init(this)
    }
}