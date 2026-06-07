package com.byd.mediaplayer.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.byd.mediaplayer.model.Lyrics
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

object LrcParser {

    private const val TAG = "LrcParser"

    /**
     * 解析歌词，支持指定目录限制
     * @param context Context
     * @param musicPath 音乐文件路径或URI
     * @param musicDirectoryUri 可选的目录URI，用于限制歌词搜索范围（使用SAF）
     */
    fun parseLrc(context: Context, musicPath: String, musicDirectoryUri: Uri? = null): Lyrics? {
        Logger.i(TAG, "开始解析歌词，音乐路径: $musicPath, 目录限制: $musicDirectoryUri")

        // 如果有目录限制，使用SAF方式搜索
        if (musicDirectoryUri != null) {
            val lyrics = trySearchLrcInSafDirectory(context, musicPath, musicDirectoryUri)
            if (lyrics != null) {
                Logger.i(TAG, "通过SAF目录搜索找到歌词")
                return lyrics
            }
            return null
        }

        // 尝试多种方式查找歌词文件（原有逻辑）
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

    /**
     * 通过SAF在指定目录中搜索歌词文件
     */
    private fun trySearchLrcInSafDirectory(context: Context, musicPath: String, directoryUri: Uri): Lyrics? {
        return try {
            val documentFile = DocumentFile.fromTreeUri(context, directoryUri) ?: run {
                Logger.e(TAG, "无法创建DocumentFile")
                return null
            }

            val musicFileName = Uri.parse(musicPath).lastPathSegment?.substringBeforeLast(".") ?: return null
            Logger.d(TAG, "SAF目录搜索，文件名: $musicFileName")

            searchLrcFileInDirectory(context, documentFile, musicFileName)
        } catch (e: Exception) {
            Logger.e(TAG, "SAF目录搜索异常: ${e.message}", e)
            null
        }
    }

    private fun searchLrcFileInDirectory(context: Context, dir: DocumentFile, musicFileName: String): Lyrics? {
        val files: Array<DocumentFile>? = dir.listFiles() ?: return null
        if (files == null) return null
        for (file in files) {
            if (file.isDirectory) {
                val result = searchLrcFileInDirectory(context, file, musicFileName)
                if (result != null) return result
            } else if (file.isFile) {
                val name = file.name ?: continue
                if (name.startsWith(musicFileName, ignoreCase = true) &&
                    (name.endsWith(".lrc", true) || name.endsWith(".LRC", true))) {
                    Logger.d(TAG, "SAF找到歌词文件: ${file.uri}")
                    return parseLrcFromUri(context, file.uri)
                }
            }
        }
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
            val encodings = listOf("UTF-8", "GB18030", "GBK", "BIG5", "ISO-8859-1")
            var bestLyrics: Lyrics? = null
            var bestScore = -1

            for (encoding in encodings) {
                try {
                    val bytes = file.readBytes()
                    val content = String(bytes, Charset.forName(encoding))
                    val lyrics = Lyrics.parse(content)
                    val lineCount = lyrics?.lines?.size ?: 0

                    // 检查是否有乱码
                    val hasGarbledChars = content.contains('\uFFFD')
                    // 计算CJK中文字符比例，正常中文歌词应该有较高的汉字比例
                    val chineseCharCount = content.toCharArray().count {
                        it in '\u4E00'..'\u9FFF' || it in '\u3400'..'\u4DBF'
                    }
                    val chineseRatio = chineseCharCount.toFloat() / content.length.coerceAtLeast(1)

                    // 乱码判定：有替换字符OR（CJK比例太低且CJK字符数>0但不是有效中文）
                    val isGarbled = hasGarbledChars || (chineseRatio < 0.1 && chineseCharCount > 10)

                    // 评分：有乱码则大幅降低分数
                    var score = if (isGarbled) -1000 else 0
                    score += lineCount * 10
                    // 优先选择中文比例高的编码
                    score += (chineseRatio * 100).toInt()

                    Logger.d(TAG, "编码 $encoding 解析出 $lineCount 行, 乱码=$isGarbled, 汉字比例=${String.format("%.2f", chineseRatio)}, score=$score")

                    if (score > bestScore) {
                        bestScore = score
                        bestLyrics = lyrics
                    }
                } catch (e: Exception) {
                    Logger.d(TAG, "编码 $encoding 解析异常: ${e.message}")
                }
            }

            if (bestLyrics != null && bestScore > -1000) {
                Logger.i(TAG, "歌词解析完成，最佳编码score=$bestScore")
                bestLyrics
            } else {
                Logger.e(TAG, "所有编码尝试失败或存在乱码")
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
            // 先将内容读入字节数组，然后使用parseFile的多种编码检测逻辑
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                Logger.d(TAG, "通过Uri读取成功，字节长度: ${bytes.size}")

                // 尝试多种编码
                val encodings = listOf("UTF-8", "GB18030", "GBK", "BIG5", "ISO-8859-1")
                var bestLyrics: Lyrics? = null
                var bestScore = -1

                for (encoding in encodings) {
                    try {
                        val content = String(bytes, Charset.forName(encoding))
                        val lyrics = Lyrics.parse(content)
                        val lineCount = lyrics?.lines?.size ?: 0

                        val hasGarbledChars = content.contains('\uFFFD')
                        val chineseCharCount = content.toCharArray().count {
                            it in '\u4E00'..'\u9FFF' || it in '\u3400'..'\u4DBF'
                        }
                        val chineseRatio = chineseCharCount.toFloat() / content.length.coerceAtLeast(1)
                        val isGarbled = hasGarbledChars || (chineseRatio < 0.1 && chineseCharCount > 10)

                        var score = if (isGarbled) -1000 else 0
                        score += lineCount * 10
                        score += (chineseRatio * 100).toInt()

                        Logger.d(TAG, "编码 $encoding 解析出 $lineCount 行, 乱码=$isGarbled, 汉字比例=${String.format("%.2f", chineseRatio)}, score=$score")

                        if (score > bestScore) {
                            bestScore = score
                            bestLyrics = lyrics
                        }
                    } catch (e: Exception) {
                        Logger.d(TAG, "编码 $encoding 解析异常: ${e.message}")
                    }
                }

                if (bestLyrics != null && bestScore > -1000) {
                    Logger.i(TAG, "歌词解析完成，最佳编码score=$bestScore")
                    bestLyrics
                } else {
                    Logger.e(TAG, "所有编码尝试失败或存在乱码")
                    null
                }
            } ?: run {
                Logger.w(TAG, "无法打开Uri: $uri")
                null
            }
        } catch (e: Exception) {
            Logger.e(TAG, "通过Uri解析歌词失败: $uri", e)
            null
        }
    }
}