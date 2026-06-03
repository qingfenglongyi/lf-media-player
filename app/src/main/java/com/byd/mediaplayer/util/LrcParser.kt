package com.byd.mediaplayer.util

import android.content.Context
import com.byd.mediaplayer.model.Lyrics
import java.io.File
import java.nio.charset.Charset

object LrcParser {

    private const val TAG = "LrcParser"

    fun parseLrc(musicPath: String): Lyrics? {
        Logger.d(TAG, "解析歌词: $musicPath")
        // 尝试在同一目录查找同名 .lrc 文件
        val lrcPath = musicPath.substringBeforeLast(".") + ".lrc"
        val lrcFile = File(lrcPath)

        if (!lrcFile.exists()) {
            Logger.d(TAG, "歌词文件不存在: $lrcPath")
            // 尝试其他可能的命名
            val parentDir = File(musicPath).parentFile
            val musicName = File(musicPath).nameWithoutExtension
            val possibleLrcFiles = parentDir?.listFiles { _, name ->
                name.startsWith(musicName, ignoreCase = true) && name.endsWith(".lrc", ignoreCase = true)
            }
            if (!possibleLrcFiles.isNullOrEmpty()) {
                Logger.d(TAG, "找到替代歌词文件: ${possibleLrcFiles.first().absolutePath}")
                return parseFile(possibleLrcFiles.first())
            }
            Logger.w(TAG, "未找到歌词文件")
            return null
        }

        Logger.d(TAG, "找到歌词文件: ${lrcFile.absolutePath}")
        return parseFile(lrcFile)
    }

    fun parseLrcFromAssets(context: Context, fileName: String): Lyrics? {
        return try {
            Logger.d(TAG, "从Assets解析歌词: $fileName")
            val content = context.assets.open(fileName).bufferedReader().use { it.readText() }
            Lyrics.parse(content)
        } catch (e: Exception) {
            Logger.e(TAG, "从Assets解析歌词失败: $fileName", e)
            null
        }
    }

    private fun parseFile(file: File): Lyrics? {
        return try {
            Logger.d(TAG, "读取歌词文件: ${file.absolutePath}")
            val content = file.readText(Charsets.UTF_8)
            Lyrics.parse(content)
        } catch (e: Exception) {
            Logger.e(TAG, "解析歌词文件失败(UTF-8): ${file.absolutePath}", e)
            try {
                // 尝试 GBK 编码
                Logger.d(TAG, "尝试GBK编码: ${file.absolutePath}")
                val content = file.readBytes().toString(Charset.forName("GBK"))
                Lyrics.parse(content)
            } catch (e2: Exception) {
                Logger.e(TAG, "解析歌词文件失败(GBK): ${file.absolutePath}", e2)
                null
            }
        }
    }
}