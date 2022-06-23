package com.qlcd.loggertools

import android.app.Application

object LoggerTools {

    lateinit var context: Application
    
    fun init(context: Application) {
        this.context = context
    }
}