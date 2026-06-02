package com.byd.mediaplayer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.byd.mediaplayer.model.Lyrics
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song

enum class CenterView {
    VINYL,  // 转盘
    LYRIC   // 歌词
}

@Composable
fun PlayerScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    playlist: List<Song>,
    currentPosition: Long,
    duration: Long,
    playMode: PlayMode,
    lyrics: Lyrics?,
    volume: Float,
    showPlaylistPanel: Boolean,
    playlistTab: PlaylistTab,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSongClick: (Int) -> Unit,
    onPlayModeChange: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onCenterViewToggle: () -> Unit,
    onPlaylistToggle: () -> Unit,
    onPlaylistTabChange: (PlaylistTab) -> Unit,
    onPlaylistDismiss: () -> Unit,
    onCreatePlaylist: ((String) -> Unit)? = null,
    onDeletePlaylist: ((String) -> Unit)? = null,
    onAddToPlaylist: ((Song) -> Unit)? = null,
    onSearchQueryChange: ((String) -> Unit)? = null,
    searchQuery: String = "",
    sortType: LibrarySortType = LibrarySortType.ALL,
    onSortTypeChange: ((LibrarySortType) -> Unit)? = null,
    artists: List<String> = emptyList(),
    albums: List<String> = emptyList(),
    onArtistClick: ((String) -> Unit)? = null,
    onAlbumClick: ((String) -> Unit)? = null,
    selectedArtist: String? = null,
    selectedAlbum: String? = null,
    onBackFromArtist: (() -> Unit)? = null,
    onBackFromAlbum: (() -> Unit)? = null,
    onPlaylistClick: ((String) -> Unit)? = null,
    selectedPlaylistName: String? = null,
    onBackFromPlaylist: (() -> Unit)? = null,
    getPlaylistSongs: ((String) -> List<Song>)? = null,
    viewState: LibraryViewState = LibraryViewState.SONGS,
    modifier: Modifier = Modifier
) {
    var centerView by remember { mutableStateOf(CenterView.VINYL) }
    var showVolumeSlider by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部栏
            TopBar(
                playMode = playMode,
                onPlayModeChange = onPlayModeChange,
                onPlaylistToggle = onPlaylistToggle,
                onVolumeClick = { showVolumeSlider = !showVolumeSlider }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 中心视图切换区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = centerView,
                    label = "centerView"
                ) { view ->
                    when (view) {
                        CenterView.VINYL -> {
                            VinylView(
                                song = currentSong,
                                isPlaying = isPlaying,
                                onClick = { centerView = CenterView.LYRIC }
                            )
                        }
                        CenterView.LYRIC -> {
                            LyricView(
                                lyrics = lyrics,
                                currentTime = currentPosition,
                                onClick = { centerView = CenterView.VINYL }
                            )
                        }
                    }
                }
            }

            // 歌曲信息
            Text(
                text = buildString {
                    append(currentSong?.title ?: "未选择歌曲")
                    append(" - ")
                    append(currentSong?.artist ?: "比亚迪音乐播放器")
                },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 进度条
            ProgressBar(
                currentPosition = currentPosition,
                duration = duration,
                onSeek = onSeek
            )

            // 播放控制按钮
            PlaybackControls(
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious
            )

            // 当前播放列表预览
            CurrentPlaylistPreview(
                playlist = playlist,
                currentSong = currentSong,
                onSongClick = onSongClick
            )
        }

        // 音量控制
        if (showVolumeSlider) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showVolumeSlider = false },
                contentAlignment = Alignment.Center
            ) {
                VolumeControl(
                    currentVolume = volume,
                    onVolumeChange = {
                        onVolumeChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .clickable(enabled = false) {}
                )
            }
        }

        // 播放列表面板
        PlaylistPanel(
            visible = showPlaylistPanel,
            currentPlaylist = playlist,
            allSongs = playlist,
            playlists = emptyList(),
            currentTab = playlistTab,
            currentSongIndex = playlist.indexOf(currentSong),
            onTabChange = onPlaylistTabChange,
            onSongClick = { index ->
                onSongClick(index)
                onPlaylistDismiss()
            },
            onDismiss = onPlaylistDismiss,
            onCreatePlaylist = onCreatePlaylist,
            onDeletePlaylist = onDeletePlaylist,
            onAddToPlaylist = onAddToPlaylist,
            onSearchQueryChange = onSearchQueryChange,
            searchQuery = searchQuery,
            sortType = sortType,
            onSortTypeChange = onSortTypeChange,
            artists = artists,
            albums = albums,
            onArtistClick = onArtistClick,
            onAlbumClick = onAlbumClick,
            selectedArtist = selectedArtist,
            selectedAlbum = selectedAlbum,
            onBackFromArtist = onBackFromArtist,
            onBackFromAlbum = onBackFromAlbum,
            onPlaylistClick = onPlaylistClick,
            selectedPlaylistName = selectedPlaylistName,
            onBackFromPlaylist = onBackFromPlaylist,
            getPlaylistSongs = getPlaylistSongs,
            viewState = viewState
        )
    }
}

@Composable
private fun TopBar(
    playMode: PlayMode,
    onPlayModeChange: () -> Unit,
    onPlaylistToggle: () -> Unit,
    onVolumeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 播放模式
        Text(
            text = when (playMode) {
                PlayMode.LIST_LOOP -> "🔁"
                PlayMode.SINGLE_LOOP -> "🔂"
                PlayMode.SHUFFLE -> "🔀"
            },
            fontSize = 24.sp,
            modifier = Modifier.clickable(onClick = onPlayModeChange)
        )

        Row {
            Text(
                text = "🔊",
                fontSize = 24.sp,
                modifier = Modifier.clickable(onClick = onVolumeClick)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "📋",
                fontSize = 24.sp,
                modifier = Modifier.clickable(onClick = onPlaylistToggle)
            )
        }
    }
}

@Composable
private fun ProgressBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit
) {
    var sliderPosition by remember(currentPosition) { mutableFloatStateOf(currentPosition.toFloat()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { onSeek(sliderPosition.toLong()) },
            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00D4AA),
                activeTrackColor = Color(0xFF00D4AA),
                inactiveTrackColor = Color.Gray
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatTime(currentPosition), color = Color.Gray, fontSize = 12.sp)
            Text(text = formatTime(duration), color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⏮", fontSize = 32.sp, color = Color.White, modifier = Modifier.clickable(onClick = onPrevious))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF00D4AA))
                .clickable(onClick = onPlayPause),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isPlaying) "⏸" else "▶", fontSize = 32.sp, color = Color.White)
        }

        Text("⏭", fontSize = 32.sp, color = Color.White, modifier = Modifier.clickable(onClick = onNext))
    }
}

@Composable
private fun CurrentPlaylistPreview(
    playlist: List<Song>,
    currentSong: Song?,
    onSongClick: (Int) -> Unit
) {
    if (playlist.isEmpty()) return

    Column {
        Text(
            text = "当前播放 (${playlist.size})",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 150.dp)
        ) {
            itemsIndexed(playlist.take(5)) { index, song ->
                val isCurrent = song == currentSong

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongClick(index) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrent) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = song.title,
                        color = if (isCurrent) Color(0xFF00D4AA) else Color.White,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
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