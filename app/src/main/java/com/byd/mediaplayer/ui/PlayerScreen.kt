package com.byd.mediaplayer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.byd.mediaplayer.model.Lyrics
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import kotlinx.coroutines.delay

enum class CenterView {
    VINYL,
    LYRIC
}

@Composable
fun PlayerScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    playlist: List<Song>,
    librarySongs: List<Song>,
    currentPosition: Long,
    duration: Long,
    playMode: PlayMode,
    lyrics: Lyrics?,
    showPlaylistPanel: Boolean,
    playlistTab: PlaylistTab,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSongClick: (Int) -> Unit,
    onPlayModeChange: () -> Unit,
    onCenterViewToggle: () -> Unit,
    onPlaylistToggle: () -> Unit,
    onPlaylistTabChange: (PlaylistTab) -> Unit,
    onPlaylistDismiss: () -> Unit,
    onCreatePlaylist: ((String) -> Unit)? = null,
    onDeletePlaylist: ((String) -> Unit)? = null,
    onAddToPlaylist: ((Song, String) -> Unit)? = null,
    onAddSongsToPlaylist: ((List<Song>, String) -> Unit)? = null,
    onAddSongsToQueue: ((List<Song>) -> Unit)? = null,
    onPlayPlaylistSongs: ((List<Song>, Int) -> Unit)? = null,
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
    onRenamePlaylist: ((String, String) -> Unit)? = null,
    selectedPlaylistName: String? = null,
    onBackFromPlaylist: (() -> Unit)? = null,
    getPlaylistSongs: ((String) -> List<Song>)? = null,
    onSetMusicDirectory: (() -> Unit)? = null,
    onPlaySongFromLibrary: ((Song) -> Unit)? = null,
    onPlayAllSongs: (() -> Unit)? = null,
    playlists: List<Pair<String, Int>> = emptyList(),
    modifier: Modifier = Modifier
) {
    var centerView by remember { mutableStateOf(CenterView.VINYL) }
    var controlsVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // 30秒无操作自动隐藏进度条和控制区
    LaunchedEffect(isPlaying, controlsVisible) {
        if (!isPlaying) {
            controlsVisible = true
            return@LaunchedEffect
        }
        while (isPlaying && controlsVisible) {
            delay(1000)
            if (System.currentTimeMillis() - lastInteractionTime >= 30_000) {
                controlsVisible = false
            }
        }
    }

    // 点击屏幕恢复控制区
    fun onScreenTap() {
        lastInteractionTime = System.currentTimeMillis()
        if (!controlsVisible) controlsVisible = true
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        val baseHeight = 700.dp
        val scale = (maxHeight / baseHeight).coerceIn(0.5f, 2.0f)

        if (isLandscape) {
            // ===== 横屏布局：列表区在左2/3，播放区在右1/3 =====
            Row(modifier = Modifier.fillMaxSize()) {
                // 左侧：列表区
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    PlaylistPanel(
                        visible = true,
                        currentPlaylist = playlist,
                        allSongs = librarySongs,
                        playlists = playlists,
                        currentTab = playlistTab,
                        currentSongIndex = playlist.indexOf(currentSong),
                        onTabChange = onPlaylistTabChange,
                        onSongClick = { index ->
                            if (playlist.isNotEmpty() && index in playlist.indices) {
                                onSongClick(index)
                            }
                        },
                        onDismiss = onPlaylistDismiss,
                        onCreatePlaylist = onCreatePlaylist,
                        onDeletePlaylist = onDeletePlaylist,
                        onAddToPlaylist = onAddToPlaylist,
                        onAddSongsToPlaylist = onAddSongsToPlaylist,
                        onAddSongsToQueue = onAddSongsToQueue,
                        onPlayPlaylistSongs = onPlayPlaylistSongs,
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
                        onRenamePlaylist = onRenamePlaylist,
                        selectedPlaylistName = selectedPlaylistName,
                        onBackFromPlaylist = onBackFromPlaylist,
                        getPlaylistSongs = getPlaylistSongs,
                        onSetMusicDirectory = onSetMusicDirectory,
                        onPlaySongFromLibrary = onPlaySongFromLibrary,
                        onPlayAllSongs = onPlayAllSongs
                    )
                }

                // 右侧：播放区
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onScreenTap() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerContent(
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = duration,
                        playMode = playMode,
                        lyrics = lyrics,
                        centerView = centerView,
                        onCenterViewChange = { centerView = it },
                        controlsVisible = controlsVisible,
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        onSeek = onSeek,
                        onPlayModeChange = onPlayModeChange,
                        onPlaylistToggle = onPlaylistToggle,
                        scale = scale
                    )
                }
            }
        } else {
            // ===== 竖屏布局：播放区在上，列表区在下2/3 =====
            Column(modifier = Modifier.fillMaxSize()) {
                // 上方：播放区
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable { onScreenTap() },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlayerContent(
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        currentPosition = currentPosition,
                        duration = duration,
                        playMode = playMode,
                        lyrics = lyrics,
                        centerView = centerView,
                        onCenterViewChange = { centerView = it },
                        controlsVisible = controlsVisible,
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        onSeek = onSeek,
                        onPlayModeChange = onPlayModeChange,
                        onPlaylistToggle = onPlaylistToggle,
                        scale = scale
                    )
                }

                // 下方：列表区（占2/3高度）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.667f)
                ) {
                    PlaylistPanel(
                        visible = true,
                        currentPlaylist = playlist,
                        allSongs = librarySongs,
                        playlists = playlists,
                        currentTab = playlistTab,
                        currentSongIndex = playlist.indexOf(currentSong),
                        onTabChange = onPlaylistTabChange,
                        onSongClick = { index ->
                            if (playlist.isNotEmpty() && index in playlist.indices) {
                                onSongClick(index)
                            }
                        },
                        onDismiss = onPlaylistDismiss,
                        onCreatePlaylist = onCreatePlaylist,
                        onDeletePlaylist = onDeletePlaylist,
                        onAddToPlaylist = onAddToPlaylist,
                        onAddSongsToPlaylist = onAddSongsToPlaylist,
                        onAddSongsToQueue = onAddSongsToQueue,
                        onPlayPlaylistSongs = onPlayPlaylistSongs,
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
                        onRenamePlaylist = onRenamePlaylist,
                        selectedPlaylistName = selectedPlaylistName,
                        onBackFromPlaylist = onBackFromPlaylist,
                        getPlaylistSongs = getPlaylistSongs,
                        onSetMusicDirectory = onSetMusicDirectory,
                        onPlaySongFromLibrary = onPlaySongFromLibrary,
                        onPlayAllSongs = onPlayAllSongs
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerContent(
    currentSong: Song?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    playMode: PlayMode,
    lyrics: Lyrics?,
    centerView: CenterView,
    onCenterViewChange: (CenterView) -> Unit,
    controlsVisible: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onPlayModeChange: () -> Unit,
    onPlaylistToggle: () -> Unit,
    scale: Float
) {
    val s = scale.coerceIn(0.5f, 2.0f)
    val gap = (12 * scale).dp
    val titleHeight = (50 * scale).dp
    val outerPad = (10 * scale).dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(outerPad),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 歌曲信息
        val songTitle = currentSong?.title ?: "未选择歌曲"
        val songArtist = currentSong?.artist ?: "比亚迪音乐播放器"
        val songInfo = "$songTitle - $songArtist"
        Box(modifier = Modifier.height(titleHeight)) {
            AutoScrollingText(
                text = songInfo,
                modifier = Modifier.padding(vertical = (10 * scale).dp),
                fontSize = (20 * scale).sp
            )
        }

        Spacer(modifier = Modifier.height(gap))

        // 中心视图
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
                            scale = scale,
                            onClick = { onCenterViewChange(CenterView.LYRIC) }
                        )
                    }
                    CenterView.LYRIC -> {
                        LyricView(
                            lyrics = lyrics,
                            currentTime = currentPosition,
                            scale = scale,
                            onClick = { onCenterViewChange(CenterView.VINYL) }
                        )
                    }
                }
            }
        }

        // 进度条和控制区（可自动隐藏）
        AnimatedVisibility(visible = controlsVisible) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(gap))

                ProgressBar(
                    currentPosition = currentPosition,
                    duration = duration,
                    onSeek = onSeek,
                    scale = scale
                )

                Spacer(modifier = Modifier.height(gap))

                PlaybackControls(
                    isPlaying = isPlaying,
                    playMode = playMode,
                    onPlayPause = onPlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onPlayModeChange = onPlayModeChange,
                    onPlaylistToggle = onPlaylistToggle,
                    scale = scale
                )
            }
        }
    }
}

@Composable
private fun ProgressBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    scale: Float = 1f
) {
    var sliderPosition by remember(currentPosition) { mutableFloatStateOf(currentPosition.toFloat()) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val timeFontSize = if (isLandscape) (14 * scale).sp else (12 * scale).sp

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = formatTime(currentPosition), color = Color.Gray, fontSize = timeFontSize)
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeFinished = { onSeek(sliderPosition.toLong()) },
                valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF00D4AA),
                    activeTrackColor = Color(0xFF00D4AA),
                    inactiveTrackColor = Color.Gray
                )
            )
            Text(text = formatTime(duration), color = Color.Gray, fontSize = timeFontSize)
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
    scale: Float = 1f
) {
    val s = scale.coerceIn(0.5f, 2.0f)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = (10 * s).dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (playMode) {
                    PlayMode.LIST_LOOP -> "🔁"
                    PlayMode.SINGLE_LOOP -> "🔂"
                    PlayMode.SHUFFLE -> "🔀"
                },
                fontSize = (24 * s).sp,
                color = Color.White,
                modifier = Modifier.clickable(onClick = onPlayModeChange)
            )
            Text("⏮", fontSize = (32 * s).sp, color = Color.White, modifier = Modifier.clickable(onClick = onPrevious))
            Box(
                modifier = Modifier
                    .size((72 * s).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00D4AA))
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (isPlaying) "⏸" else "▶", fontSize = (32 * s).sp, color = Color.White)
            }
            Text("⏭", fontSize = (32 * s).sp, color = Color.White, modifier = Modifier.clickable(onClick = onNext))
            Text("📋", fontSize = (24 * s).sp, color = Color.White, modifier = Modifier.clickable(onClick = onPlaylistToggle))
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = (10 * s).dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⏮", fontSize = (48 * s).sp, color = Color.White, modifier = Modifier.clickable(onClick = onPrevious))
                Box(
                    modifier = Modifier
                        .size((80 * s).dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00D4AA))
                        .clickable(onClick = onPlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (isPlaying) "⏸" else "▶", fontSize = (40 * s).sp, color = Color.White)
                }
                Text("⏭", fontSize = (48 * s).sp, color = Color.White, modifier = Modifier.clickable(onClick = onNext))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = (5 * s).dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (playMode) {
                        PlayMode.LIST_LOOP -> "🔁"
                        PlayMode.SINGLE_LOOP -> "🔂"
                        PlayMode.SHUFFLE -> "🔀"
                    },
                    fontSize = (28 * s).sp,
                    color = Color.White,
                    modifier = Modifier.clickable(onClick = onPlayModeChange)
                )
                Text("📋", fontSize = (28 * s).sp, color = Color.White, modifier = Modifier.clickable(onClick = onPlaylistToggle))
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
    scrollSpeed: Float = 50f,
    fontSize: TextUnit = 20.sp
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
            .fillMaxWidth(0.667f)
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                containerWidth = constraints.maxWidth.toFloat()
                textWidth = placeable.width.toFloat()
                layout(placeable.width, placeable.height) {
                    placeable.place(offsetX.toInt(), 0)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }

    LaunchedEffect(Unit) {
        delay(100)
        if (textWidth > containerWidth && !isScrolling) {
            delay(scrollDelayMs)
            isScrolling = true
        }
    }
}
