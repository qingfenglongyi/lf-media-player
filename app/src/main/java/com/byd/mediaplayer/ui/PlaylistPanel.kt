package com.byd.mediaplayer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.byd.mediaplayer.model.Song

enum class PlaylistTab {
    PLAYING,    // 当前播放
    PLAYLISTS,  // 歌单
    LIBRARY     // 歌曲库
}

@Composable
fun PlaylistPanel(
    visible: Boolean,
    currentPlaylist: List<Song>,
    allSongs: List<Song>,
    playlists: List<String>,
    currentTab: PlaylistTab,
    currentSongIndex: Int,
    onTabChange: (PlaylistTab) -> Unit,
    onSongClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(320.dp)
                .background(Color(0xFF1A1A2E), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab 切换
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabButton("播放", currentTab == PlaylistTab.PLAYING) {
                        onTabChange(PlaylistTab.PLAYING)
                    }
                    TabButton("歌单", currentTab == PlaylistTab.PLAYLISTS) {
                        onTabChange(PlaylistTab.PLAYLISTS)
                    }
                    TabButton("库", currentTab == PlaylistTab.LIBRARY) {
                        onTabChange(PlaylistTab.LIBRARY)
                    }
                }

                // 内容区域
                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        PlaylistTab.PLAYING -> {
                            PlaylistContent(
                                songs = currentPlaylist,
                                currentIndex = currentSongIndex,
                                onSongClick = onSongClick
                            )
                        }
                        PlaylistTab.PLAYLISTS -> {
                            PlaylistListContent(
                                playlists = playlists,
                                onPlaylistClick = { /* TODO: 打开歌单详情 */ }
                            )
                        }
                        PlaylistTab.LIBRARY -> {
                            PlaylistContent(
                                songs = allSongs,
                                currentIndex = -1,
                                onSongClick = onSongClick
                            )
                        }
                    }
                }
            }

            // 关闭按钮
            Text(
                text = "✕",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = if (selected) Color(0xFF00D4AA) else Color.Gray,
        fontSize = 16.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun PlaylistContent(
    songs: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit
) {
    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无歌曲", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(songs) { index, song ->
                val isCurrentSong = index == currentIndex

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) }
                        .background(if (isCurrentSong) Color(0xFF2A2A4E) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 12.sp,
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
            }
        }
    }
}

@Composable
private fun PlaylistListContent(
    playlists: List<String>,
    onPlaylistClick: (String) -> Unit
) {
    if (playlists.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无歌单", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(playlists) { _, name ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaylistClick(name) }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📋", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
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