package com.byd.mediaplayer.ui

import androidx.compose.foundation.background
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

@Composable
fun LyricView(
    lyrics: Lyrics?,
    currentTime: Long,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val currentLineIndex = lyrics?.getCurrentLineIndex(currentTime) ?: -1
    val listState = rememberLazyListState()

    // 自动滚动到当前行
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
            .padding(16.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (lyrics == null || lyrics.lines.isEmpty()) {
            Text(
                text = "暂无歌词",
                color = Color.Gray,
                fontSize = 18.sp
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

private fun Modifier.clickable(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
        ) { onClick() }
    )

private val Int.sp: androidx.compose.ui.unit.TextUnit
    get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)