package com.byd.mediaplayer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
    onAddSongsToPlaylist: ((List<Song>) -> Unit)? = null,
    onAddSongsToQueue: ((List<Song>) -> Unit)? = null,
    onDeleteSongsFromPlaylist: ((List<Int>) -> Unit)? = null,
    onRemoveSongFromPlaylist: ((String, Int) -> Unit)? = null,
    onDeleteSongsFromLibrary: ((List<Long>) -> Unit)? = null,
    onClearPlaylist: (() -> Unit)? = null,
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
            // 歌曲信息（当标题过长时自动滚动）
            val songTitle = currentSong?.title ?: "未选择歌曲"
            val songArtist = currentSong?.artist ?: "比亚迪音乐播放器"
            val songInfo = "$songTitle - $songArtist"
            AutoScrollingText(
                text = songInfo,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // 进度条
            ProgressBar(
                currentPosition = currentPosition,
                duration = duration,
                onSeek = onSeek
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 播放控制按钮（根据屏幕方向自适应布局）
            PlaybackControls(
                isPlaying = isPlaying,
                playMode = playMode,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onPlayModeChange = onPlayModeChange,
                onPlaylistToggle = onPlaylistToggle,
                onVolumeClick = { showVolumeSlider = !showVolumeSlider }
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
            onAddSongsToPlaylist = onAddSongsToPlaylist,
            onAddSongsToQueue = onAddSongsToQueue,
            onDeleteSongsFromPlaylist = onDeleteSongsFromPlaylist,
            onRemoveSongFromPlaylist = onRemoveSongFromPlaylist,
            onDeleteSongsFromLibrary = onDeleteSongsFromLibrary,
            onClearPlaylist = onClearPlaylist,
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
            getPlaylistSongs = getPlaylistSongs
        )
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
    playMode: PlayMode,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onPlayModeChange: () -> Unit,
    onPlaylistToggle: () -> Unit,
    onVolumeClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    if (isLandscape) {
        // 横屏布局：一行，五个按键等大
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
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
                color = Color.White,
                modifier = Modifier.clickable(onClick = onPlayModeChange)
            )
            // 上一曲
            Text("⏮", fontSize = 32.sp, color = Color.White, modifier = Modifier.clickable(onClick = onPrevious))
            // 播放/暂停
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
            // 下一曲
            Text("⏭", fontSize = 32.sp, color = Color.White, modifier = Modifier.clickable(onClick = onNext))
            // 列表
            Text("📋", fontSize = 24.sp, color = Color.White, modifier = Modifier.clickable(onClick = onPlaylistToggle))
        }
    } else {
        // 竖屏布局：两行
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 上行：上一曲、播放/暂停、下一曲（按键较大）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⏮", fontSize = 48.sp, color = Color.White, modifier = Modifier.clickable(onClick = onPrevious))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00D4AA))
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isPlaying) "⏸" else "▶", fontSize = 40.sp, color = Color.White)
                }
                Text("⏭", fontSize = 48.sp, color = Color.White, modifier = Modifier.clickable(onClick = onNext))
            }
            // 下行：播放模式、列表（按键略小）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (playMode) {
                        PlayMode.LIST_LOOP -> "🔁"
                        PlayMode.SINGLE_LOOP -> "🔂"
                        PlayMode.SHUFFLE -> "🔀"
                    },
                    fontSize = 28.sp,
                    color = Color.White,
                    modifier = Modifier.clickable(onClick = onPlayModeChange)
                )
                Text("📋", fontSize = 28.sp, color = Color.White, modifier = Modifier.clickable(onClick = onPlaylistToggle))
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

@Composable
private fun AutoScrollingText(
    text: String,
    modifier: Modifier = Modifier,
    scrollDelayMs: Long = 3000L,
    scrollSpeed: Float = 50f
) {
    var textWidth by remember { mutableFloatStateOf(0f) }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var isScrolling by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var shouldScroll by remember { mutableStateOf(false) }

    LaunchedEffect(text, containerWidth, textWidth) {
        shouldScroll = textWidth > containerWidth
        if (!shouldScroll) {
            offsetX = 0f
            isScrolling = false
        }
    }

    LaunchedEffect(shouldScroll, isScrolling) {
        if (!shouldScroll || !isScrolling) return@LaunchedEffect
        while (isScrolling) {
            delay(50)
            offsetX -= scrollSpeed / 50f
            val minOffset = -(textWidth - containerWidth)
            if (offsetX <= minOffset) {
                delay(scrollDelayMs)
                offsetX = containerWidth
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                containerWidth = constraints.maxWidth.toFloat()
                textWidth = placeable.width.toFloat()
                layout(placeable.width, placeable.height) {
                    placeable.place(offsetX.toInt(), 0)
                }
            }
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }

    LaunchedEffect(Unit) {
        // 检测是否需要滚动
        delay(100)
        if (textWidth > containerWidth && !isScrolling) {
            delay(scrollDelayMs)
            isScrolling = true
        }
    }
}