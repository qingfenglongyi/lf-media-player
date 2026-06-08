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
    onClick: () -> Unit = {}
) {
    // 根据当前时间计算应高亮的行索引
    val currentLineIndex = lyrics?.getCurrentLineIndex(currentTime) ?: -1
    // LazyColumn的列表状态（用于控制滚动位置）
    val listState = rememberLazyListState()

    // 当高亮行变化时，自动滚动到该行
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0 && lyrics != null) {
            // 目标索引：当前行往上2行，这样当前行在屏幕偏下位置
            val targetIndex = (currentLineIndex - 2).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))  // 深色背景
            .padding(16.dp)
            .clickable(onClick = onClick),  // 点击切换回唱片视图
        contentAlignment = Alignment.Center
    ) {
        if (lyrics == null || lyrics.lines.isEmpty()) {
            // 无歌词时显示提示文字
            Text(
                text = "暂无歌词",
                color = Color.Gray,
                fontSize = 18.sp
            )
        } else {
            // 歌词列表
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(lyrics.lines) { index, line ->
                    val isCurrentLine = index == currentLineIndex

                    Text(
                        text = line.text.ifEmpty { "♪" },  // 空文本显示音符
                        // 当前行高亮显示，其他行灰色
                        color = if (isCurrentLine) Color(0xFF00D4AA) else Color.Gray,
                        // 当前行字体更大、加粗
                        fontSize = if (isCurrentLine) 22.sp else 16.sp,
                        fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

