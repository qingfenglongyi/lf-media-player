package com.byd.mediaplayer.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.byd.mediaplayer.model.Lyrics
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

object LrcParser {

    private const val TAG = "LrcParser"

    fun parseLrc(context: Context, musicPath: String): Lyrics? {
        Logger.i(TAG, "开始解析歌词，音乐路径: $musicPath")

        // 尝试多种方式查找歌词文件
        val lrcPath = musicPath.substringBeforeLast(".") + ".lrc"
        Logger.d(TAG, "尝试直接路径: $lrcPath")

        // 方式1: 直接文件访问
        var lyrics = tryDirectFileAccess(lrcPath)
        if (lyrics != null) {
            Logger.i(TAG, "通过直接文件访问找到歌词")
            return lyrics
        }

        // 方式2: 从音乐文件目录搜索
        lyrics = trySearchInMusicDirectory(musicPath)
        if (lyrics != null) {
            Logger.i(TAG, "通过目录搜索找到歌词")
            return lyrics
        }

        // 方式3: 使用MediaStore查询同目录的lrc文件
        lyrics = tryMediaStoreQuery(context, musicPath)
        if (lyrics != null) {
            Logger.i(TAG, "通过MediaStore查询找到歌词")
            return lyrics
        }

        Logger.w(TAG, "未找到歌词文件: $lrcPath")
        return null
    }

    private fun tryDirectFileAccess(lrcPath: String): Lyrics? {
        return try {
            val file = File(lrcPath)
            if (file.exists() && file.canRead()) {
                Logger.d(TAG, "直接文件存在且可读: ${file.absolutePath}, 大小: ${file.length()} bytes")
                parseFile(file)
            } else {
                Logger.d(TAG, "直接文件不可访问: $lrcPath, exists=${file.exists()}, canRead=${file.canRead()}")
                null
            }
        } catch (e: Exception) {
            Logger.e(TAG, "直接文件访问异常: $lrcPath", e)
            null
        }
    }

    private fun trySearchInMusicDirectory(musicPath: String): Lyrics? {
        return try {
            val musicFile = File(musicPath)
            val parentDir = musicFile.parentFile

            if (parentDir == null || !parentDir.exists() || !parentDir.canRead()) {
                Logger.d(TAG, "父目录不可访问: ${parentDir?.absolutePath}")
                return null
            }

            val musicName = musicFile.nameWithoutExtension
            Logger.d(TAG, "在目录 ${parentDir.absolutePath} 中搜索歌词，前缀: $musicName")

            val lrcFiles = parentDir.listFiles { _, name ->
                name.startsWith(musicName, ignoreCase = true) &&
                (name.endsWith(".lrc", ignoreCase = true) || name.endsWith(".LRC", ignoreCase = true))
            }

            if (lrcFiles.isNullOrEmpty()) {
                Logger.d(TAG, "目录中未找到匹配的lrc文件")
                // 列出目录中的lrc文件以便调试
                val allLrcFiles = parentDir.listFiles { _, name ->
                    name.endsWith(".lrc", ignoreCase = true)
                }
                Logger.d(TAG, "目录中的lrc文件: ${allLrcFiles?.map { it.name }}")
                null
            } else {
                Logger.d(TAG, "找到匹配的lrc文件: ${lrcFiles.first().absolutePath}")
                parseFile(lrcFiles.first())
            }
        } catch (e: Exception) {
            Logger.e(TAG, "目录搜索异常: $musicPath", e)
            null
        }
    }

    private fun tryMediaStoreQuery(context: Context, musicPath: String): Lyrics? {
        return try {
            // 从路径中提取目录
            val musicFile = File(musicPath)
            val parentDir = musicFile.parentFile?.absolutePath ?: return null

            Logger.d(TAG, "通过MediaStore搜索目录: $parentDir")

            // 查询该目录下的lrc文件
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Files.getContentUri("external")
            }

            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE
            )

            val selection = "${MediaStore.Files.FileColumns.DATA} LIKE ? AND (" +
                    "${MediaStore.Files.FileColumns.DATA} LIKE '%.lrc' OR " +
                    "${MediaStore.Files.FileColumns.DATA} LIKE '%.LRC')"
            val selectionArgs = arrayOf("$parentDir%")

            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                val dataColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val filePath = if (dataColumn >= 0) cursor.getString(dataColumn) else null
                    val fileName = if (nameColumn >= 0) cursor.getString(nameColumn) else null

                    Logger.d(TAG, "MediaStore找到文件: $fileName, 路径: $filePath")

                    // 检查是否匹配音乐文件名
                    val musicName = musicFile.nameWithoutExtension
                    if (filePath != null && fileName != null &&
                        fileName.startsWith(musicName, ignoreCase = true)) {
                        val file = File(filePath)
                        if (file.exists()) {
                            Logger.d(TAG, "匹配成功: $filePath")
                            return parseFile(file)
                        }
                    }
                }
            }

            Logger.d(TAG, "MediaStore未找到匹配的lrc文件")
            null
        } catch (e: Exception) {
            Logger.e(TAG, "MediaStore查询异常", e)
            null
        }
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
            Logger.d(TAG, "解析歌词文件: ${file.absolutePath}")
            // 尝试多种编码
            val encodings = listOf("UTF-8", "GBK", "BIG5", "GB18030", "ISO-8859-1")
            var bestLyrics: Lyrics? = null
            var bestScore = 0

            for (encoding in encodings) {
                try {
                    val bytes = file.readBytes()
                    val content = String(bytes, Charset.forName(encoding))
                    val lyrics = Lyrics.parse(content)
                    val lineCount = lyrics?.lines?.size ?: 0
                    Logger.d(TAG, "编码 $encoding 解析出 $lineCount 行歌词, 原始字节数: ${bytes.size}")

                    // 评分：有效行数越多越好，如果有相同行数，选择GBK/BIG5等中文编码优先
                    if (lineCount > bestScore) {
                        bestScore = lineCount
                        bestLyrics = lyrics
                    }
                } catch (e: Exception) {
                    Logger.d(TAG, "编码 $encoding 解析异常: ${e.message}")
                }
            }

            if (bestLyrics != null) {
                Logger.i(TAG, "歌词解析完成，最佳编码有效行数: $bestScore")
                bestLyrics
            } else {
                Logger.e(TAG, "所有编码尝试失败")
                null
            }
        } catch (e: Exception) {
            Logger.e(TAG, "解析歌词文件失败: ${file.absolutePath}", e)
            null
        }
    }

    // 通过Uri读取歌词文件（用于Content URI访问）
    fun parseLrcFromUri(context: Context, uri: Uri): Lyrics? {
        return try {
            Logger.d(TAG, "通过Uri解析歌词: $uri")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    val content = reader.readText()
                    Logger.d(TAG, "通过Uri读取成功，内容长度: ${content.length} 字符")
                    return Lyrics.parse(content)
                }
            }
            Logger.w(TAG, "无法打开Uri: $uri")
            null
        } catch (e: Exception) {
            Logger.e(TAG, "通过Uri解析歌词失败: $uri", e)
            null
        }
    }
}