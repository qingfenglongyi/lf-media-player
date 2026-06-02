package com.byd.mediaplayer.model

data class LyricLine(
    val time: Long,  // 毫秒
    val text: String
)

data class Lyrics(
    val lines: List<LyricLine>,
    val offset: Long = 0  // 偏移量（毫秒）
) {
    fun getCurrentLineIndex(currentTime: Long): Int {
        for (i in lines.indices.reversed()) {
            if (currentTime >= lines[i].time) {
                return i
            }
        }
        return -1
    }

    companion object {
        fun parse(lrcContent: String): Lyrics {
            val lines = mutableListOf<LyricLine>()
            var offset = 0L

            val lrcLines = lrcContent.lines()
            for (line in lrcLines) {
                when {
                    line.startsWith("[offset:", ignoreCase = true) -> {
                        offset = parseTimeTag(line.removePrefix("[offset:").removeSuffix("]").trim()) ?: 0L
                    }
                    line.startsWith("[", ignoreCase = true) && line.length > 10 -> {
                        val timeTag = line.substringBefore("]")
                        val text = line.substringAfter("]").trim()
                        if (text.isNotEmpty()) {
                            parseTimeTag(timeTag)?.let { time ->
                                lines.add(LyricLine(time + offset, text))
                            }
                        }
                    }
                }
            }

            return Lyrics(lines.sortedBy { it.time })
        }

        private fun parseTimeTag(tag: String): Long? {
            // 支持格式: [mm:ss.xx] 或 [mm:ss:xx]
            val cleanTag = tag.removePrefix("[")
            val parts = cleanTag.split("[:.]")
            if (parts.size >= 3) {
                val minutes = parts[0].toLongOrNull() ?: return null
                val seconds = parts[1].toLongOrNull() ?: return null
                val centiseconds = parts[2].take(2).padEnd(2, '0').toLongOrNull() ?: return null
                return (minutes * 60 + seconds) * 1000 + centiseconds * 10
            }
            return null
        }
    }
}