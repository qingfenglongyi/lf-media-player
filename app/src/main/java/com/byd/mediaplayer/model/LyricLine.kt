package com.byd.mediaplayer.model

import com.byd.mediaplayer.util.Logger

data class LyricLine(
    val time: Long,  // 毫秒
    val text: String
)

data class Lyrics(
    val lines: List<LyricLine>,
    val offset: Long = 0  // 偏移量（毫秒）
) {
    private val TAG = "Lyrics"

    fun getCurrentLineIndex(currentTime: Long): Int {
        for (i in lines.indices.reversed()) {
            if (currentTime >= lines[i].time) {
                return i
            }
        }
        return -1
    }

    companion object {
        private const val TAG = "Lyrics"

        fun parse(lrcContent: String): Lyrics {
            Logger.d(TAG, "开始解析歌词内容, 长度: ${lrcContent.length} 字符")
            val lines = mutableListOf<LyricLine>()
            var offset = 0L

            val lrcLines = lrcContent.lines()
            Logger.d(TAG, "歌词文件共 ${lrcLines.size} 行")

            var parsedCount = 0
            for (line in lrcLines) {
                // 跳过空白行
                if (line.isBlank()) continue

                // 处理offset标签
                if (line.startsWith("[offset:", ignoreCase = true)) {
                    offset = parseTimeTag(line.removePrefix("[offset:").removeSuffix("]").trim()) ?: 0L
                    Logger.v(TAG, "解析到offset: $offset")
                    continue
                }

                // 提取时间标签（时间标签格式：[mm:ss.xx] 或 [mm:ss:xx] 或只有 [mm:ss]）
                val timeTagStart = line.indexOf('[')
                val timeTagEnd = line.lastIndexOf(']')

                if (timeTagStart >= 0 && timeTagEnd > timeTagStart) {
                    val timeTag = line.substring(timeTagStart + 1, timeTagEnd)
                    val text = line.substring(timeTagEnd + 1).trim()

                    if (timeTag.isNotEmpty() && text.isNotEmpty()) {
                        parseTimeTag("[$timeTag]")?.let { time ->
                            lines.add(LyricLine(time + offset, text))
                            parsedCount++
                        }
                    }
                }
            }

            val sortedLines = lines.sortedBy { it.time }
            Logger.i(TAG, "歌词解析完成, 有效行数: $parsedCount, 排序后: ${sortedLines.size} 行")

            if (sortedLines.isNotEmpty()) {
                Logger.v(TAG, "第一行: ${sortedLines.first().text}, 时间: ${sortedLines.first().time}ms")
                Logger.v(TAG, "最后一行: ${sortedLines.last().text}, 时间: ${sortedLines.last().time}ms")
            }

            return Lyrics(sortedLines, offset)
        }

        private fun parseTimeTag(tag: String): Long? {
            // 支持格式: [mm:ss.xx] 或 [mm:ss:xx]
            val cleanTag = tag.removePrefix("[")
            val parts = cleanTag.split("[:.]")
            if (parts.size >= 3) {
                val minutes = parts[0].toLongOrNull() ?: return null
                val seconds = parts[1].toLongOrNull() ?: return null
                val centiseconds = parts[2].take(2).padEnd(2, '0').toLongOrNull() ?: return null
                val timeMs = (minutes * 60 + seconds) * 1000 + centiseconds * 10
                Logger.v(TAG, "解析时间标签: $tag -> ${timeMs}ms")
                return timeMs
            }
            Logger.w(TAG, "无法解析时间标签: $tag")
            return null
        }
    }
}