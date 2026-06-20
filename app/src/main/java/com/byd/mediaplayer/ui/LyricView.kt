package com.byd.mediaplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byd.mediaplayer.model.Lyrics

/**
 * 歌词视图组件
 * 显示滚动歌词，点击可切换回黑胶唱片视图
 *
 * 功能：
 * - 显示歌词每行内容
 * - 根据当前播放时间自动高亮当前行
 * - 自动滚动到当前行附近
 *
 * @param lyrics 歌词数据（null表示无歌词）
 * @param currentTime 当前播放时间（毫秒）
 * @param modifier 修饰符
 * @param onClick 点击事件（切换到黑胶唱片视图）
 */
@Composable
fun LyricView(
    lyrics: Lyrics?,
    currentTime: Long,
    modifier: Modifier = Modifier,
    scale: Float = 1f,
    onClick: () -> Unit = {}
) {
    val s = scale.coerceIn(0.5f, 2.0f)
    val currentLineIndex = lyrics?.getCurrentLineIndex(currentTime) ?: -1
    val listState = rememberLazyListState()

    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0 && lyrics != null) {
            val targetIndex = (currentLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding((16 * s).dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (lyrics == null || lyrics.lines.isEmpty()) {
            Text(
                text = "暂无歌词",
                color = Color.Gray,
                fontSize = (18 * s).sp
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(lyrics.lines) { index, line ->
                    val isCurrentLine = index == currentLineIndex

                    Text(
                        text = line.text.ifEmpty { "♪" },
                        color = if (isCurrentLine) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = (if (isCurrentLine) 22 * s else 16 * s).sp,
                        fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = (12 * s).dp)
                    )
                }
            }
        }
    }
}

