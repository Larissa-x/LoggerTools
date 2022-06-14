package com.qlcd.loggertools

import android.app.Application
import com.qlcd.loggertools.manager.DatabaseManager

open class BaseApplication : Application() {

   companion object {
       lateinit var context: Application
   }

    init {
        DatabaseManager.saveApplication(this)
    }



    override fun onCreate() {
        super.onCreate()
        context = this
    }
}