package com.byd.mediaplayer

import android.app.Application
import com.byd.mediaplayer.util.Logger

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 默认开启日志用于调试
        Logger.init(this, true)
    }
}