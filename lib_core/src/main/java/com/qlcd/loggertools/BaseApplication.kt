package com.qlcd.loggertools

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.qlcd.loggertools.manager.DatabaseManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

open class BaseApplication : Application() {

   companion object {
       lateinit var context: Application
   }

    init {
        initARouter()
        initSmartRefreshLayout()
        DatabaseManager.saveApplication(this)
    }

    private fun initSmartRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
//            layout.setPrimaryColorsId(R.color.colorPrimary, R.color.white) //全局设置主题颜色
            MaterialHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(
                context
            ).setDrawableSize(20f)
        }
    }

    /**
     * 初始化ARouter（路由框架）
     */
    private fun initARouter() {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this@BaseApplication)
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}