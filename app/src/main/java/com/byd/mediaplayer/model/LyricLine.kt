package com.byd.mediaplayer.model

import com.byd.mediaplayer.util.Logger

/**
 * 单行歌词数据类
 *
 * @param time 该行歌词开始播放的时间戳（毫秒）
 * @param text 歌词文本内容
 */
data class LyricLine(
    val time: Long,  // 毫秒，该行歌词开始的时间
    val text: String  // 歌词文本
)

/**
 * 歌词数据类
 * 包含多行歌词以及整体偏移设置
 *
 * @param lines 歌词行列表，按时间排序
 * @param offset 歌词时间偏移量（毫秒），正值表示歌词提前，负值表示歌词延后
 */
data class Lyrics(
    val lines: List<LyricLine>,  // 歌词行列表
    val offset: Long = 0        // 偏移量（毫秒）
) {
    private val TAG = "Lyrics"

    /**
     * 根据当前播放时间获取应高亮显示的行索引
     * 使用二分查找的思想，从后往前找第一行时间 <= currentTime的歌词
     *
     * @param currentTime 当前播放时间（毫秒）
     * @return 应高亮的行索引，如果无匹配返回-1
     */
    fun getCurrentLineIndex(currentTime: Long): Int {
        // 从后往前遍历，找到第一个时间小于等于当前时间的歌词行
        for (i in lines.indices.reversed()) {
            if (currentTime >= lines[i].time) {
                return i
            }
        }
        return -1
    }

    companion object {
        private const val TAG = "Lyrics"

        /**
         * 解析LRC格式歌词内容
         * LRC格式示例：[00:12.34]歌词内容
         *
         * @param lrcContent LRC格式的歌词文本
         * @return 解析后的Lyrics对象，如果解析失败返回null
         */
        fun parse(lrcContent: String): Lyrics {
            Logger.d(TAG, "开始解析歌词内容, 长度: ${lrcContent.length} 字符")
            val lines = mutableListOf<LyricLine>()
            var offset = 0L

            // 按行分割歌词内容
            val lrcLines = lrcContent.lines()
            Logger.d(TAG, "歌词文件共 ${lrcLines.size} 行")

            var parsedCount = 0
            for (line in lrcLines) {
                // 跳过空白行
                if (line.isBlank()) continue

                // 处理offset标签 [offset:+/-毫秒]
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
                            // 解析成功，将时间和歌词文本组成一行
                            lines.add(LyricLine(time + offset, text))
                            parsedCount++
                        }
                    }
                }
            }

            // 按时间排序
            val sortedLines = lines.sortedBy { it.time }
            Logger.i(TAG, "歌词解析完成, 有效行数: $parsedCount, 排序后: ${sortedLines.size} 行")

            // 记录首尾行用于调试
            if (sortedLines.isNotEmpty()) {
                Logger.v(TAG, "第一行: ${sortedLines.first().text}, 时间: ${sortedLines.first().time}ms")
                Logger.v(TAG, "最后一行: ${sortedLines.last().text}, 时间: ${sortedLines.last().time}ms")
            }

            return Lyrics(sortedLines, offset)
        }

        /**
         * 解析时间标签为毫秒
         * 支持格式：
         * - [mm:ss.xx] 如 [01:23.45] 表示1分23秒45厘秒 = 83500毫秒
         * - [mm:ss:xx] 如 [01:23:45] 同样表示1分23秒45厘秒
         *
         * @param tag 时间标签字符串，包含方括号
         * @return 毫秒时间，如果解析失败返回null
         */
        private fun parseTimeTag(tag: String): Long? {
            // 支持格式: [mm:ss.xx] 或 [mm:ss:xx]
            val cleanTag = tag.removePrefix("[").removeSuffix("]")
            // 用正则分割：冒号或点
            val parts = cleanTag.split(Regex("[:.]"))
            if (parts.size >= 3) {
                val minutes = parts[0].toLongOrNull() ?: return null
                val seconds = parts[1].toLongOrNull() ?: return null
                // 取前2位并补零到2位
                val centiseconds = parts[2].take(2).padEnd(2, '0').toLongOrNull() ?: return null
                // 计算总毫秒数：分钟*60 + 秒 = 总秒数，再*1000 + 厘秒*10
                val timeMs = (minutes * 60 + seconds) * 1000 + centiseconds * 10
                // Logger.v(TAG, "解析时间标签: $tag -> ${timeMs}ms")
                return timeMs
            }
            Logger.w(TAG, "无法解析时间标签: $tag")
            return null
        }
    }
}