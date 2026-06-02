package com.byd.mediaplayer.util

import android.content.Context
import android.util.Log
import com.byd.mediaplayer.model.Lyrics
import java.io.File

object LrcParser {

    private const val TAG = "LrcParser"

    fun parseLrc(musicPath: String): Lyrics? {
        // 尝试在同一目录查找同名 .lrc 文件
        val lrcPath = musicPath.substringBeforeLast(".") + ".lrc"
        val lrcFile = File(lrcPath)

        if (!lrcFile.exists()) {
            // 尝试其他可能的命名
            val parentDir = File(musicPath).parentFile
            val musicName = File(musicPath).nameWithoutExtension
            val possibleLrcFiles = parentDir?.listFiles { _, name ->
                name.startsWith(musicName, ignoreCase = true) && name.endsWith(".lrc", ignoreCase = true)
            }
            if (!possibleLrcFiles.isNullOrEmpty()) {
                return parseFile(possibleLrcFiles.first())
            }
            return null
        }

        return parseFile(lrcFile)
    }

    fun parseLrcFromAssets(context: Context, fileName: String): Lyrics? {
        return try {
            val content = context.assets.open(fileName).bufferedReader().use { it.readText() }
            Lyrics.parse(content)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse lrc from assets: $fileName", e)
            null
        }
    }

    private fun parseFile(file: File): Lyrics? {
        return try {
            val content = file.readText(Charsets.UTF_8)
            Lyrics.parse(content)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse lrc file: ${file.absolutePath}", e)
            try {
                // 尝试 GBK 编码
                val content = file.readBytes().toString(Charsets.GBK)
                Lyrics.parse(content)
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to parse lrc with GBK: ${file.absolutePath}", e2)
                null
            }
        }
    }
}