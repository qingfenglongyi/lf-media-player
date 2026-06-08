package com.byd.mediaplayer

import android.app.Application
import com.byd.mediaplayer.util.Logger

/**
 * 应用程序入口类
 * 在应用启动时进行初始化操作
 */
class MainApplication : Application() {

    /**
     * 应用创建时调用
     * 初始化日志系统，默认开启日志用于调试
     */
    override fun onCreate() {
        super.onCreate()
        // 默认开启日志用于调试
        Logger.init(this, true)
    }
}