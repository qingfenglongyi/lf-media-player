package com.byd.mediaplayer.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

object Logger {
    private var isEnabled = false
    private var logDir: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun init(context: Context, enabled: Boolean = false) {
        isEnabled = enabled
        // 使用 /sdcard/documents/logs 目录
        val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        logDir = File(documentsDir, "logs")
        if (!logDir!!.exists()) {
            logDir!!.mkdirs()
        }
        if (isEnabled) {
            log("INFO", "Logger", "日志目录: ${logDir?.absolutePath}")
        }
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun isEnabled(): Boolean = isEnabled

    private fun getLogFile(): File? {
        if (!isEnabled || logDir == null) return null
        val fileName = "app_${fileNameFormat.format(Date())}.log"
        return File(logDir, fileName)
    }

    fun d(tag: String, message: String) {
        log("DEBUG", tag, message)
    }

    fun v(tag: String, message: String) {
        log("VERBOSE", tag, message)
    }

    fun i(tag: String, message: String) {
        log("INFO", tag, message)
    }

    fun w(tag: String, message: String) {
        log("WARN", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log("ERROR", tag, message, throwable)
    }

    private fun log(level: String, tag: String, message: String, throwable: Throwable? = null) {
        if (!isEnabled) return

        val logFile = getLogFile() ?: return
        try {
            PrintWriter(FileWriter(logFile, true)).use { writer ->
                val timestamp = dateFormat.format(Date())
                val throwableStr = if (throwable != null) "\n${android.util.Log.getStackTraceString(throwable)}" else ""
                writer.println("[$timestamp] [$level] [$tag] $message$throwableStr")
            }
        } catch (e: Exception) {
            // Silently fail if logging fails
        }
    }

    fun clearLogs() {
        if (!isEnabled || logDir == null) return
        logDir!!.listFiles()?.forEach { it.delete() }
    }

    fun getLogDir(): File? = logDir
}