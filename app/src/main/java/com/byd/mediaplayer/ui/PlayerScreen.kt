package com.byd.mediaplayer.ui

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.ui.unit.Dp
import com.byd.mediaplayer.model.Lyrics
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import kotlinx.coroutines.delay

/**
 * 中心视图类型枚举
 * 用于切换黑胶唱片和歌词显示
 */
enum class CenterView {
    VINYL,  // 黑胶唱片视图
    LYRIC   // 歌词视图
}

/**
 * 播放器主界面
 * 包含歌曲信息显示、中心视图（唱片/歌词）、进度条、播放控制按钮
 *
 * @param currentSong 当前播放的歌曲
 * @param isPlaying 是否正在播放
 * @param playlist 当前播放列表
 * @param librarySongs 歌曲库列表
 * @param currentPosition 当前播放位置（毫秒）
 * @param duration 当前歌曲总时长（毫秒）
 * @param playMode 当前播放模式
 * @param lyrics 当前歌曲的歌词
 * @param showPlaylistPanel 是否显示播放列表面板
 * @param playlistTab 当前播放列表标签页
 * @param onPlayPause 播放/暂停回调
 * @param onNext 下一曲回调
 * @param onPrevious 上一曲回调
 * @param onSeek 跳转播放位置回调（参数：目标位置毫秒）
 * @param onSongClick 歌曲点击回调（参数：歌曲索引）
 * @param onPlayModeChange 播放模式切换回调
 * @param onCenterViewToggle 中心视图切换回调
 * @param onPlaylistToggle 播放列表面板显示/隐藏回调
 * @param onPlaylistTabChange 列表标签页切换回调
 * @param onPlaylistDismiss 列表面板关闭回调
 * @param onCreatePlaylist 创建歌单回调
 * @param onDeletePlaylist 删除歌单回调
 * @param onAddToPlaylist 添加歌曲到歌单回调
 * @param onAddSongsToPlaylist 批量添加歌曲到歌单回调
 * @param onAddSongsToQueue 添加歌曲到播放队列回调
 * @param onDeleteSongsFromPlaylist 从播放列表删除歌曲回调
 * @param onRemoveSongFromPlaylist 从歌单移除歌曲回调
 * @param onDeleteSongsFromLibrary 从歌曲库删除回调
 * @param onClearPlaylist 清空播放列表回调
 * @param onSearchQueryChange 搜索关键词变化回调
 * @param searchQuery 当前搜索关键词
 * @param sortType 歌曲库排序类型
 * @param onSortTypeChange 排序类型切换回调
 * @param artists 艺术家列表
 * @param albums 专辑列表
 * @param onArtistClick 艺术家点击回调
 * @param onAlbumClick 专辑点击回调
 * @param selectedArtist 当前选中的艺术家
 * @param selectedAlbum 当前选中的专辑
 * @param onBackFromArtist 从艺术家详情返回回调
 * @param onBackFromAlbum 从专辑详情返回回调
 * @param onPlaylistClick 歌单点击回调
 * @param onRenamePlaylist 重命名歌单回调
 * @param selectedPlaylistName 当前选中的歌单名称
 * @param onBackFromPlaylist 从歌单详情返回回调
 * @param getPlaylistSongs 获取歌单歌曲列表的回调
 * @param onSetMusicDirectory 设置音乐目录回调
 * @param playlists 歌单列表
 * @param modifier 修饰符
 */
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
    playlists: List<Pair<String, Int>> = emptyList(),
    modifier: Modifier = Modifier
) {
    // 当前中心视图状态（唱片或歌词）
    var centerView by remember { mutableStateOf(CenterView.VINYL) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))  // 深色背景
    ) {
        // 基于屏幕高度计算缩放因子（700dp为设计基准）
        val baseHeight = 700.dp
        val scale = (maxHeight / baseHeight).coerceIn(0.5f, 2.0f)
        val gap = (12 * scale).dp
        val titleHeight = (50 * scale).dp
        val outerPad = (10 * scale).dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 歌曲信息显示区域（标题过长时自动滚动）
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

            // 中心视图区域（唱片或歌词，可切换）
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
                                onClick = { centerView = CenterView.LYRIC }
                            )
                        }
                        CenterView.LYRIC -> {
                            LyricView(
                                lyrics = lyrics,
                                currentTime = currentPosition,
                                scale = scale,
                                onClick = { centerView = CenterView.VINYL }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(gap))

            // 进度条（显示播放进度，可拖动跳转）
            ProgressBar(
                currentPosition = currentPosition,
                duration = duration,
                onSeek = onSeek,
                scale = scale
            )

            Spacer(modifier = Modifier.height(gap))

            // 播放控制按钮区域
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

        // 播放列表面板（从右侧滑入）
        PlaylistPanel(
            visible = showPlaylistPanel,
            currentPlaylist = playlist,
            allSongs = librarySongs,
            playlists = playlists,
            currentTab = playlistTab,
            currentSongIndex = playlist.indexOf(currentSong),
            onTabChange = onPlaylistTabChange,
            onSongClick = { index ->
                if (playlist.isNotEmpty() && index in playlist.indices) {
                    onSongClick(index)
                    onPlaylistDismiss()
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
            onSetMusicDirectory = onSetMusicDirectory
        )
    }
}

/**
 * 进度条组件
 * 显示当前播放进度，支持拖动跳转
 *
 * @param currentPosition 当前播放位置（毫秒）
 * @param duration 总时长（毫秒）
 * @param onSeek 跳转回调（参数：目标位置毫秒）
 */
@Composable
private fun ProgressBar(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    scale: Float = 1f
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
            Text(text = formatTime(currentPosition), color = Color.Gray, fontSize = (12 * scale).sp)
            Text(text = formatTime(duration), color = Color.Gray, fontSize = (12 * scale).sp)
        }
    }
}

/**
 * 播放控制按钮组件
 * 根据屏幕方向自适应布局（横屏一行，竖屏两行）
 *
 * @param isPlaying 是否正在播放
 * @param playMode 当前播放模式
 * @param onPlayPause 播放/暂停回调
 * @param onNext 下一曲回调
 * @param onPrevious 上一曲回调
 * @param onPlayModeChange 播放模式切换回调
 * @param onPlaylistToggle 播放列表显示/隐藏回调
 */
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

/**
 * 格式化时间显示
 * 将毫秒转换为 mm:ss 格式
 *
 * @param time 时间（毫秒）
 * @return 格式化后的字符串
 */
private fun formatTime(time: Long): String {
    if (time <= 0) return "00:00"
    val seconds = (time / 1000) % 60
    val minutes = (time / 1000) / 60
    return "%02d:%02d".format(minutes, seconds)
}

/**
 * 自动滚动文字组件
 * 当文字宽度超过容器宽度时，自动向左滚动显示
 *
 * @param text 要显示的文字
 * @param modifier 修饰符
 * @param scrollDelayMs 滚动前等待时间（毫秒）
 * @param scrollSpeed 滚动速度（像素/50毫秒）
 */
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

    // 检测是否需要滚动
    LaunchedEffect(text, containerWidth, textWidth) {
        shouldScroll = textWidth > containerWidth
        if (!shouldScroll) {
            offsetX = 0f
            isScrolling = false
        }
    }

    // 滚动动画
    LaunchedEffect(shouldScroll, isScrolling) {
        if (!shouldScroll || !isScrolling) return@LaunchedEffect
        while (isScrolling) {
            delay(50)
            offsetX -= scrollSpeed / 50f
            val minOffset = -(textWidth - containerWidth)
            if (offsetX <= minOffset) {
                // 滚动到末尾，等待后重置到开头
                delay(scrollDelayMs)
                offsetX = containerWidth
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.667f)  // 最多占容器2/3宽度
            .layout { measurable, constraints ->
                // 自定义布局，测量文字宽度
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

    // 检测是否需要开始滚动
    LaunchedEffect(Unit) {
        delay(100)
        if (textWidth > containerWidth && !isScrolling) {
            delay(scrollDelayMs)
            isScrolling = true
        }
    }
}