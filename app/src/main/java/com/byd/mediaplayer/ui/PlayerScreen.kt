package com.byd.mediaplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byd.mediaplayer.model.Song

@Composable
fun PlayerScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    playlist: List<Song>,
    currentPosition: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSongClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 歌曲信息
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = currentSong?.title ?: "未选择歌曲",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentSong?.artist ?: "比亚迪音乐播放器",
            color = Color.Gray,
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 进度条
        var sliderPosition by remember(currentPosition) { mutableFloatStateOf(currentPosition.toFloat()) }

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { onSeek(sliderPosition.toLong()) },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = SliderDefaultsColors(
                thumbColor = Color(0xFF00D4AA),
                activeTrackColor = Color(0xFF00D4AA),
                inactiveTrackColor = Color.Gray
            )
        )

        // 时间显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = formatTime(duration),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 播放控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevious,
                modifier = Modifier.size(64.dp)
            ) {
                Text("⏮", fontSize = 32.sp, color = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00D4AA))
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isPlaying) "⏸" else "▶",
                    fontSize = 32.sp,
                    color = Color.White
                )
            }

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(64.dp)
            ) {
                Text("⏭", fontSize = 32.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 播放列表
        Text(
            text = "播放列表 (${playlist.size})",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(playlist.size) { index ->
                val song = playlist[index]
                val isCurrentSong = index == playlist.indexOf(currentSong)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            color = if (isCurrentSong) Color(0xFF00D4AA) else Color.White,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = song.artist,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = formatTime(song.duration),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                HorizontalDivider(color = Color(0xFF2A2A4E))
            }
        }
    }
}

private fun formatTime(time: Long): String {
    if (time <= 0) return "00:00"
    val seconds = (time / 1000) % 60
    val minutes = (time / 1000) / 60
    return "%02d:%02d".format(minutes, seconds)
}