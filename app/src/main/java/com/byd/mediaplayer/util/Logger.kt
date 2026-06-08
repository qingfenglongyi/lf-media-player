package com.byd.mediaplayer.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日志工具类
 * 提供统一的日志记录接口，支持DEBUG/VERBOSE/INFO/WARN/ERROR五个级别
 * 日志文件保存在 /sdcard/documents/logs 目录下
 *
 * 使用方法：
 * - Logger.init(context, true) 初始化并开启日志
 * - Logger.d("TAG", "debug message") 记录调试日志
 * - Logger.i("TAG", "info message") 记录信息日志
 * - Logger.e("TAG", "error message", exception) 记录错误日志
 */
object Logger {
    /** 日志开关 */
    private var isEnabled = false

    /** 日志保存目录 */
    private var logDir: File? = null

    /** 日期格式化：用于日志时间戳 */
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /** 日期格式化：用于日志文件名 */
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * 初始化日志系统
     *
     * @param context Android上下文
     * @param enabled 是否开启日志记录
     */
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

    /**
     * 设置日志开关
     * @param enabled 是否开启
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /**
     * 获取日志是否开启
     * @return 日志开关状态
     */
    fun isEnabled(): Boolean = isEnabled

    /**
     * 获取当日的日志文件
     * @return 日志文件对象，如果日志未开启或目录不存在返回null
     */
    private fun getLogFile(): File? {
        if (!isEnabled || logDir == null) return null
        val fileName = "app_${fileNameFormat.format(Date())}.log"
        return File(logDir, fileName)
    }

    /** 记录DEBUG级别日志 */
    fun d(tag: String, message: String) {
        log("DEBUG", tag, message)
    }

    /** 记录VERBOSE级别日志 */
    fun v(tag: String, message: String) {
        log("VERBOSE", tag, message)
    }

    /** 记录INFO级别日志 */
    fun i(tag: String, message: String) {
        log("INFO", tag, message)
    }

    /** 记录WARN级别日志 */
    fun w(tag: String, message: String) {
        log("WARN", tag, message)
    }

    /**
     * 记录ERROR级别日志
     * @param tag 日志标签
     * @param message 日志消息
     * @param throwable 可选的异常对象
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log("ERROR", tag, message, throwable)
    }

    /**
     * 实际执行日志写入
     *
     * @param level 日志级别
     * @param tag 标签
     * @param message 消息
     * @param throwable 可选异常
     */
    private fun log(level: String, tag: String, message: String, throwable: Throwable? = null) {
        if (!isEnabled) return

        val logFile = getLogFile() ?: return
        try {
            PrintWriter(FileWriter(logFile, true)).use { writer ->
                val timestamp = dateFormat.format(Date())
                // 如果有异常，附加堆栈信息
                val throwableStr = if (throwable != null) "\n${android.util.Log.getStackTraceString(throwable)}" else ""
                writer.println("[$timestamp] [$level] [$tag] $message$throwableStr")
            }
        } catch (e: Exception) {
            // 日志写入失败时静默处理，避免递归异常
        }
    }

    /**
     * 清除所有日志文件
     */
    fun clearLogs() {
        if (!isEnabled || logDir == null) return
        logDir!!.listFiles()?.forEach { it.delete() }
    }

    /**
     * 获取日志目录
     * @return 日志目录File对象
     */
    fun getLogDir(): File? = logDir
}