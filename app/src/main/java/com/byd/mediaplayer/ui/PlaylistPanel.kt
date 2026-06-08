package com.byd.mediaplayer.ui

/**
 * 本文件是本地MP3音乐播放器的播放列表面板组件
 *
 * 主要功能：
 * 1. 展示当前播放列表内容
 * 2. 管理用户创建的歌单
 * 3. 浏览本地歌曲库
 * 4. 支持多选模式进行批量操作
 * 5. 提供歌曲搜索和分类浏览功能（按艺术家、专辑）
 *
 * 该组件采用Jetpack Compose构建，响应式UI设计
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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

/**
 * 播放列表面板的Tab标签枚举
 * 用于切换不同的内容区域
 *
 * @property PLAYING 当前播放 - 显示当前正在播放的歌曲列表
 * @property PLAYLISTS 歌单 - 显示用户创建的所有歌单
 * @property LIBRARY 本地歌曲库 - 显示设备上的所有歌曲，支持搜索和分类
 */
enum class PlaylistTab {
    PLAYING,    // 当前播放
    PLAYLISTS,  // 歌单
    LIBRARY     // 歌曲库
}

/**
 * 歌曲库的排序类型枚举
 * 用于对歌曲库中的歌曲进行分类展示
 *
 * @property ALL 全部 - 显示所有歌曲
 * @property BY_ARTIST 按艺术家 - 按艺术家分组显示
 * @property BY_ALBUM 按专辑 - 按专辑分组显示
 */
enum class LibrarySortType {
    ALL,        // 全部
    BY_ARTIST,  // 按艺术家
    BY_ALBUM    // 按专辑
}

/**
 * 歌曲库的视图状态枚举
 * 用于跟踪用户当前的浏览状态，支持返回导航
 *
 * @property SONGS 歌曲列表 - 默认视图，显示所有歌曲
 * @property ARTIST_LIST 艺术家列表 - 显示所有艺术家
 * @property ALBUM_LIST 专辑列表 - 显示所有专辑
 * @property ARTIST_SONGS 艺术家歌曲 - 某个艺术家的所有歌曲
 * @property ALBUM_SONGS 专辑歌曲 - 某个专辑的所有歌曲
 * @property PLAYLIST_DETAIL 歌单详情 - 某个歌单的具体内容
 */
enum class LibraryViewState {
    SONGS,          // 歌曲列表
    ARTIST_LIST,    // 艺术家列表
    ALBUM_LIST,     // 专辑列表
    ARTIST_SONGS,   // 艺术家歌曲列表
    ALBUM_SONGS,    // 专辑歌曲列表
    PLAYLIST_DETAIL // 歌单详情
}

/**
 * 播放列表面板主组件
 *
 * 这是一个从右侧滑入的半透明面板，提供播放列表、歌单和歌曲库的浏览与管理功能。
 * 面板支持多标签页切换、多选模式操作、搜索过滤等功能。
 *
 * @param visible 是否显示面板
 * @param currentPlaylist 当前播放列表（正在播放的歌曲列表）
 * @param allSongs 歌曲库中的所有歌曲
 * @param playlists 用户创建的所有歌单名称列表
 * @param currentTab 当前选中的Tab标签
 * @param currentSongIndex 当前正在播放的歌曲索引
 * @param onTabChange 切换Tab时的回调
 * @param onSongClick 点击歌曲时的回调，参数为歌曲索引
 * @param onDismiss 关闭面板时的回调
 * @param onCreatePlaylist 创建新歌单，参数为歌单名称
 * @param onDeletePlaylist 删除歌单，参数为歌单名称
 * @param onAddToPlaylist 添加歌曲到歌单，参数为歌曲对象
 * @param onAddSongsToPlaylist 批量添加歌曲到歌单，参数为歌曲列表
 * @param onAddSongsToQueue 批量添加歌曲到播放队列
 * @param onDeleteSongsFromPlaylist 从播放列表中删除歌曲，参数为索引列表
 * @param onRemoveSongFromPlaylist 从歌单中移除歌曲，参数为歌单名称和歌曲索引
 * @param onDeleteSongsFromLibrary 从歌曲库中删除歌曲（隐藏），参数为歌曲ID列表
 * @param onClearPlaylist 清空当前播放列表
 * @param onSearchQueryChange 搜索关键词变化时的回调
 * @param searchQuery 当前搜索关键词
 * @param sortType 当前排序类型
 * @param onSortTypeChange 排序类型变化时的回调
 * @param artists 艺术家列表（用于歌曲库分类）
 * @param albums 专辑列表（用于歌曲库分类）
 * @param onArtistClick 点击艺术家时的回调，参数为艺术家名称
 * @param onAlbumClick 点击专辑时的回调，参数为专辑名称
 * @param selectedArtist 当前选中的艺术家（用于导航状态）
 * @param selectedAlbum 当前选中的专辑（用于导航状态）
 * @param onBackFromArtist 从艺术家视图返回时的回调
 * @param onBackFromAlbum 从专辑视图返回时的回调
 * @param onPlaylistClick 点击歌单时的回调，参数为歌单名称
 * @param onRenamePlaylist 重命名歌单，参数为旧名称和新名称
 * @param selectedPlaylistName 当前选中的歌单名称（用于导航状态）
 * @param onBackFromPlaylist 从歌单详情返回时的回调
 * @param getPlaylistSongs 获取指定歌单中的歌曲列表，参数为歌单名称，返回歌曲列表
 * @param onSetMusicDirectory 打开音乐目录选择器
 * @param modifier Compose修饰符
 */
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
    onRenamePlaylist: ((String, String) -> Unit)? = null,
    selectedPlaylistName: String? = null,
    onBackFromPlaylist: (() -> Unit)? = null,
    getPlaylistSongs: ((String) -> List<Song>)? = null,
    onSetMusicDirectory: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // 对话框相关状态
    var showCreateDialog by remember { mutableStateOf(false) }      // 控制"创建歌单"对话框显示
    var newPlaylistName by remember { mutableStateOf("") }           // 新建歌单时用户输入的名称
    var showDeleteDialog by remember { mutableStateOf(false) }      // 控制"删除歌单"对话框显示
    var playlistToDelete by remember { mutableStateOf<String?>(null) } // 待删除的歌单名称
    var showRenameDialog by remember { mutableStateOf(false) }       // 控制"重命名歌单"对话框显示
    var playlistToRename by remember { mutableStateOf<String?>(null) } // 待重命名的歌单名称
    var renamePlaylistName by remember { mutableStateOf("") }       // 重命名时用户输入的新名称
    var showAddToPlaylistDialog by remember { mutableStateOf(false) } // 控制"添加到歌单"对话框显示
    var songToAdd by remember { mutableStateOf<Song?>(null) }       // 待添加到歌单的歌曲

    // 当前视图状态，用于歌曲库的二级导航
    var viewState by remember { mutableStateOf(LibraryViewState.SONGS) }

    // 初始化时检查selectedArtist/selectedAlbum是否已有值
    // 用于从其他界面跳转过来时恢复正确的视图状态
    LaunchedEffect(selectedArtist, selectedAlbum) {
        when {
            selectedArtist != null -> viewState = LibraryViewState.ARTIST_SONGS
            selectedAlbum != null -> viewState = LibraryViewState.ALBUM_SONGS
        }
    }

    // 当selectedArtist从有值变为null时（返回操作），同步更新视图状态
    LaunchedEffect(selectedArtist) {
        if (selectedArtist == null && viewState == LibraryViewState.ARTIST_SONGS) {
            viewState = LibraryViewState.SONGS
        }
    }

    // 当selectedAlbum从有值变为null时（返回操作），同步更新视图状态
    LaunchedEffect(selectedAlbum) {
        if (selectedAlbum == null && viewState == LibraryViewState.ALBUM_SONGS) {
            viewState = LibraryViewState.SONGS
        }
    }

    // 多选模式状态（用于播放列表Tab）
    var isMultiSelectMode by remember { mutableStateOf(false) }          // 是否处于多选模式
    var selectedSongIndices by remember { mutableStateOf<Set<Int>>(emptySet()) } // 已选中的歌曲索引集合
    var showMenu by remember { mutableStateOf(false) }                  // 下拉菜单显示状态

    // 歌曲库多选模式状态（用于歌曲库Tab）
    var libraryMultiSelectMode by remember { mutableStateOf(false) }
    var librarySelectedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // 面板动画：右侧滑入效果
    AnimatedVisibility(
        visible = visible,
        // 从右侧滑入，it表示面板宽度
        enter = slideInHorizontally { it },
        // 向右侧滑出
        exit = slideOutHorizontally { it }
    ) {
        // 面板容器：深色背景，左上右下圆角
        Box(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab切换栏：播放列表/歌单/本地歌曲库 三个标签
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabButton("📋 播放列表", currentTab == PlaylistTab.PLAYING) {
                        onTabChange(PlaylistTab.PLAYING)
                    }
                    TabButton("📁 歌单", currentTab == PlaylistTab.PLAYLISTS) {
                        onTabChange(PlaylistTab.PLAYLISTS)
                    }
                    TabButton("💿 本地歌曲库", currentTab == PlaylistTab.LIBRARY) {
                        onTabChange(PlaylistTab.LIBRARY)
                    }
                }

                // 内容区域：根据当前Tab显示不同内容
                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        // Tab 1: 当前播放列表
                        PlaylistTab.PLAYING -> {
                            // 多选模式下的播放列表内容
                            if (isMultiSelectMode) {
                                MultiSelectPlaylistContent(
                                    songs = currentPlaylist,
                                    currentIndex = currentSongIndex,
                                    selectedIndices = selectedSongIndices,
                                    // 切换选中状态
                                    onSelectionChange = { index ->
                                        if (selectedSongIndices.contains(index)) {
                                            selectedSongIndices = selectedSongIndices - index
                                        } else {
                                            selectedSongIndices = selectedSongIndices + index
                                        }
                                    },
                                    // 退出多选模式
                                    onToggleMultiSelect = {
                                        isMultiSelectMode = false
                                        selectedSongIndices = emptySet()
                                    },
                                    onClearPlaylist = onClearPlaylist,
                                    // 删除选中的歌曲
                                    onDeleteSelected = { indices ->
                                        onDeleteSongsFromPlaylist?.invoke(indices.toList())
                                        isMultiSelectMode = false
                                        selectedSongIndices = emptySet()
                                    },
                                    // 添加选中歌曲到歌单
                                    onAddToPlaylist = { songs ->
                                        songToAdd = songs.firstOrNull()
                                        showAddToPlaylistDialog = true
                                    },
                                    isMultiSelectMode = isMultiSelectMode
                                )
                            } else {
                                // 普通模式下的播放列表内容
                                PlayingListContent(
                                    songs = currentPlaylist,
                                    currentIndex = currentSongIndex,
                                    onSongClick = onSongClick,
                                    selectedIndices = selectedSongIndices,
                                    onSelectionChange = { index ->
                                        if (selectedSongIndices.contains(index)) {
                                            selectedSongIndices = selectedSongIndices - index
                                        } else {
                                            selectedSongIndices = selectedSongIndices + index
                                        }
                                    },
                                    onToggleMultiSelect = {
                                        if (isMultiSelectMode) {
                                            isMultiSelectMode = false
                                            selectedSongIndices = emptySet()
                                        } else {
                                            isMultiSelectMode = true
                                        }
                                    },
                                    onClearPlaylist = onClearPlaylist,
                                    onDeleteSelected = { indices ->
                                        onDeleteSongsFromPlaylist?.invoke(indices.toList())
                                        isMultiSelectMode = false
                                        selectedSongIndices = emptySet()
                                    },
                                    onAddToPlaylist = { songs ->
                                        songToAdd = songs.firstOrNull()
                                        showAddToPlaylistDialog = true
                                    },
                                    isMultiSelectMode = isMultiSelectMode
                                )
                            }
                        }
                        // Tab 2: 歌单
                        PlaylistTab.PLAYLISTS -> {
                            // 如果处于歌单详情视图，显示歌单内容
                            if (viewState == LibraryViewState.PLAYLIST_DETAIL && selectedPlaylistName != null) {
                                val playlistSongs = getPlaylistSongs?.invoke(selectedPlaylistName) ?: emptyList()
                                PlaylistDetailContent(
                                    playlistName = selectedPlaylistName,
                                    songs = playlistSongs,
                                    onSongClick = onSongClick,
                                    onBack = { onBackFromPlaylist?.invoke() },
                                    onDeleteSong = { index ->
                                        onRemoveSongFromPlaylist?.invoke(selectedPlaylistName, index)
                                    }
                                )
                            } else {
                                // 否则显示歌单列表
                                PlaylistListContent(
                                    playlists = playlists,
                                    onPlaylistClick = { name ->
                                        onPlaylistClick?.invoke(name)
                                    },
                                    onCreateClick = { showCreateDialog = true },
                                    onDeleteClick = { name ->
                                        playlistToDelete = name
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                        // Tab 3: 本地歌曲库
                        PlaylistTab.LIBRARY -> {
                            // 歌曲列表主视图
                            if (viewState == LibraryViewState.SONGS) {
                                if (libraryMultiSelectMode) {
                                    // 多选模式下的歌曲库内容
                                    LibraryMultiSelectContent(
                                        songs = allSongs,
                                        selectedIndices = librarySelectedIndices,
                                        onSongToggle = { index ->
                                            if (librarySelectedIndices.contains(index)) {
                                                librarySelectedIndices = librarySelectedIndices - index
                                            } else {
                                                librarySelectedIndices = librarySelectedIndices + index
                                            }
                                        },
                                        onToggleMultiSelect = {
                                            libraryMultiSelectMode = false
                                            librarySelectedIndices = emptySet()
                                        },
                                        onAddToQueue = { indices ->
                                            val songs = indices.map { allSongs[it] }
                                            onAddSongsToQueue?.invoke(songs)
                                        },
                                        onAddToPlaylist = { indices ->
                                            val songs = indices.map { allSongs[it] }
                                            onAddSongsToPlaylist?.invoke(songs)
                                        },
                                        onDeleteFromLibrary = { indices ->
                                            val ids = indices.map { allSongs[it].id }
                                            onDeleteSongsFromLibrary?.invoke(ids)
                                            libraryMultiSelectMode = false
                                            librarySelectedIndices = emptySet()
                                        },
                                        isMultiSelectMode = libraryMultiSelectMode
                                    )
                                } else {
                                    // 普通模式下的歌曲库内容
                                    LibraryContent(
                                        songs = allSongs,
                                        searchQuery = searchQuery,
                                        onSearchQueryChange = onSearchQueryChange ?: {},
                                        sortType = sortType,
                                        onSortTypeChange = onSortTypeChange ?: {},
                                        artists = artists,
                                        albums = albums,
                                        onSongClick = onSongClick,
                                        onMultiSelectToggle = { libraryMultiSelectMode = true },
                                        isMultiSelectMode = libraryMultiSelectMode,
                                        selectedIndices = librarySelectedIndices,
                                        onSelectionChange = { index ->
                                            if (librarySelectedIndices.contains(index)) {
                                                librarySelectedIndices = librarySelectedIndices - index
                                            } else {
                                                librarySelectedIndices = librarySelectedIndices + index
                                            }
                                        },
                                        viewState = viewState,
                                        onViewStateChange = { viewState = it },
                                        onArtistClick = onArtistClick ?: { viewState = LibraryViewState.ARTIST_SONGS },
                                        onAlbumClick = onAlbumClick ?: { viewState = LibraryViewState.ALBUM_SONGS },
                                        selectedArtist = selectedArtist,
                                        selectedAlbum = selectedAlbum,
                                        onBackFromArtist = onBackFromArtist ?: { viewState = LibraryViewState.ARTIST_LIST },
                                        onBackFromAlbum = onBackFromAlbum ?: { viewState = LibraryViewState.ALBUM_LIST },
                                        onAddToQueue = { indices ->
                                            val songsToAdd = indices.map { allSongs[it] }
                                            onAddSongsToQueue?.invoke(songsToAdd)
                                        },
                                        onAddToPlaylist = { indices ->
                                            val songsToAdd = indices.map { allSongs[it] }
                                            onAddSongsToPlaylist?.invoke(songsToAdd)
                                        },
                                        onDeleteFromLibrary = { indices ->
                                            val ids = indices.map { allSongs[it].id }
                                            onDeleteSongsFromLibrary?.invoke(ids)
                                        },
                                        onSetMusicDirectory = onSetMusicDirectory
                                    )
                                }
                            } else {
                                // 非主视图状态（艺术家/专辑列表或详情）的歌曲库内容
                                LibraryContent(
                                    songs = allSongs,
                                    searchQuery = searchQuery,
                                    onSearchQueryChange = onSearchQueryChange ?: {},
                                    sortType = sortType,
                                    onSortTypeChange = onSortTypeChange ?: {},
                                    artists = artists,
                                    albums = albums,
                                    onSongClick = onSongClick,
                                    onMultiSelectToggle = null,
                                    isMultiSelectMode = libraryMultiSelectMode,
                                    selectedIndices = librarySelectedIndices,
                                    onSelectionChange = { index ->
                                        if (librarySelectedIndices.contains(index)) {
                                            librarySelectedIndices = librarySelectedIndices - index
                                        } else {
                                            librarySelectedIndices = librarySelectedIndices + index
                                        }
                                    },
                                    viewState = viewState,
                                    onViewStateChange = { viewState = it },
                                    onArtistClick = onArtistClick ?: { viewState = LibraryViewState.ARTIST_SONGS },
                                    onAlbumClick = onAlbumClick ?: { viewState = LibraryViewState.ALBUM_SONGS },
                                    selectedArtist = selectedArtist,
                                    selectedAlbum = selectedAlbum,
                                    onBackFromArtist = onBackFromArtist ?: { viewState = LibraryViewState.ARTIST_LIST },
                                    onBackFromAlbum = onBackFromAlbum ?: { viewState = LibraryViewState.ALBUM_LIST },
                                    onAddToQueue = { indices ->
                                        val songsToAdd = indices.map { allSongs[it] }
                                        onAddSongsToQueue?.invoke(songsToAdd)
                                    },
                                    onAddToPlaylist = { indices ->
                                        val songsToAdd = indices.map { allSongs[it] }
                                        onAddSongsToPlaylist?.invoke(songsToAdd)
                                    },
                                    onDeleteFromLibrary = { indices ->
                                        val ids = indices.map { allSongs[it].id }
                                        onDeleteSongsFromLibrary?.invoke(ids)
                                    },
                                    onSetMusicDirectory = onSetMusicDirectory
                                )
                            }
                        }
                    }
                }
            }

            // 关闭按钮：位于面板右上角
            Text(
                text = "✕",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable { onDismiss() }
            )

            // ========== 对话框区域 ==========

            // 创建歌单对话框
            if (showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text("创建歌单") },
                    text = {
                        // 歌单名称输入框
                        BasicTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            decorationBox = { innerTextField ->
                                Box {
                                    // 空内容时显示占位提示文本
                                    if (newPlaylistName.isEmpty()) {
                                        Text("请输入歌单名称", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (newPlaylistName.isNotBlank()) {
                                    onCreatePlaylist?.invoke(newPlaylistName)
                                    newPlaylistName = ""
                                    showCreateDialog = false
                                }
                            }
                        ) {
                            Text("创建")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 删除歌单确认对话框
            if (showDeleteDialog && playlistToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("删除歌单") },
                    text = { Text("确定要删除歌单${playlistToDelete}吗？") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                playlistToDelete?.let { onDeletePlaylist?.invoke(it) }
                                showDeleteDialog = false
                                playlistToDelete = null
                            }
                        ) {
                            Text("删除", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 重命名歌单对话框
            if (showRenameDialog && playlistToRename != null) {
                AlertDialog(
                    onDismissRequest = { showRenameDialog = false },
                    title = { Text("重命名歌单") },
                    text = {
                        // 新名称输入框
                        BasicTextField(
                            value = renamePlaylistName,
                            onValueChange = { renamePlaylistName = it },
                            decorationBox = { innerTextField ->
                                Box {
                                    if (renamePlaylistName.isEmpty()) {
                                        Text("请输入歌单名称", color = Color.Gray)
                                    }
                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                playlistToRename?.let { oldName ->
                                    if (renamePlaylistName.isNotBlank()) {
                                        onRenamePlaylist?.invoke(oldName, renamePlaylistName)
                                    }
                                }
                                showRenameDialog = false
                                playlistToRename = null
                                renamePlaylistName = ""
                            }
                        ) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showRenameDialog = false
                            playlistToRename = null
                            renamePlaylistName = ""
                        }) {
                            Text("取消")
                        }
                    }
                )
            }

            // 添加歌曲到歌单对话框
            if (showAddToPlaylistDialog && songToAdd != null) {
                AlertDialog(
                    onDismissRequest = { showAddToPlaylistDialog = false },
                    title = { Text("添加到歌单") },
                    text = {
                        Column {
                            // 如果没有歌单，提示用户先创建
                            if (playlists.isEmpty()) {
                                Text("暂无歌单，请先创建", color = Color.Gray, fontSize = 14.sp)
                            } else {
                                // 显示所有歌单供用户选择
                                playlists.forEach { playlistName ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onAddToPlaylist?.invoke(songToAdd!!)
                                                showAddToPlaylistDialog = false
                                                songToAdd = null
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = playlistName, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    },
                    // 空确认按钮，点击外部或取消按钮关闭
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = {
                            showAddToPlaylistDialog = false
                            songToAdd = null
                        }) {
                            Text("取消")
                        }
                    }
                )
            }
        }
    }
}

/**
 * 歌曲库内容组件
 *
 * 显示歌曲库的所有歌曲，支持搜索、排序（全部/艺术家/专辑）和多选操作。
 * 这是歌曲库Tab的主视图，包含搜索框、分类选择和歌曲列表。
 *
 * @param songs 歌曲列表
 * @param searchQuery 搜索关键词
 * @param onSearchQueryChange 搜索关键词变化回调
 * @param sortType 排序类型
 * @param onSortTypeChange 排序类型变化回调
 * @param artists 艺术家列表
 * @param albums 专辑列表
 * @param onSongClick 点击歌曲回调，参数为歌曲在allSongs中的索引
 * @param onMultiSelectToggle 切换多选模式回调
 * @param isMultiSelectMode 是否处于多选模式
 * @param selectedIndices 已选中的歌曲索引集合
 * @param onSelectionChange 选中状态变化回调
 * @param viewState 当前视图状态
 * @param onViewStateChange 视图状态变化回调
 * @param onArtistClick 点击艺术家回调
 * @param onAlbumClick 点击专辑回调
 * @param selectedArtist 当前选中的艺术家
 * @param selectedAlbum 当前选中的专辑
 * @param onBackFromArtist 从艺术家视图返回回调
 * @param onBackFromAlbum 从专辑视图返回回调
 * @param onAddToQueue 添加歌曲到播放队列回调
 * @param onAddToPlaylist 添加歌曲到歌单回调
 * @param onDeleteFromLibrary 从歌曲库删除回调
 * @param onSetMusicDirectory 设置音乐目录回调
 */
@Composable
private fun LibraryContent(
    songs: List<Song>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortType: LibrarySortType,
    onSortTypeChange: (LibrarySortType) -> Unit,
    artists: List<String>,
    albums: List<String>,
    onSongClick: (Int) -> Unit,
    onMultiSelectToggle: (() -> Unit)?,
    isMultiSelectMode: Boolean = false,
    selectedIndices: Set<Int> = emptySet(),
    onSelectionChange: ((Int) -> Unit)? = null,
    viewState: LibraryViewState,
    onViewStateChange: (LibraryViewState) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    selectedArtist: String?,
    selectedAlbum: String?,
    onBackFromArtist: () -> Unit,
    onBackFromAlbum: () -> Unit,
    onAddToQueue: ((Set<Int>) -> Unit)? = null,
    onAddToPlaylist: ((Set<Int>) -> Unit)? = null,
    onDeleteFromLibrary: ((Set<Int>) -> Unit)? = null,
    onSetMusicDirectory: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框：支持按歌曲名、艺术家、专辑搜索
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            decorationBox = { innerTextField ->
                Box {
                    if (searchQuery.isEmpty()) {
                        Text("搜索歌曲...", color = Color.Gray)
                    }
                    innerTextField()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        )

        // 分类选项（仅在主视图显示）
        if (viewState == LibraryViewState.SONGS) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 全部歌曲
                SortChip("全部", sortType == LibrarySortType.ALL) {
                    onSortTypeChange(LibrarySortType.ALL)
                    onViewStateChange(LibraryViewState.SONGS)
                }
                // 按艺术家
                SortChip("艺术家", sortType == LibrarySortType.BY_ARTIST) {
                    onSortTypeChange(LibrarySortType.BY_ARTIST)
                    onViewStateChange(LibraryViewState.ARTIST_LIST)
                }
                // 按专辑
                SortChip("专辑", sortType == LibrarySortType.BY_ALBUM) {
                    onSortTypeChange(LibrarySortType.BY_ALBUM)
                    onViewStateChange(LibraryViewState.ALBUM_LIST)
                }
            }
        }

        // 内容区域：根据视图状态显示不同内容
        Box(modifier = Modifier.weight(1f)) {
            when (viewState) {
                // 歌曲列表视图
                LibraryViewState.SONGS -> {
                    LibrarySongsContent(
                        songs = songs,
                        onSongClick = onSongClick,
                        selectedIndices = selectedIndices,
                        onSelectionChange = { index -> onSelectionChange?.invoke(index) },
                        onToggleMultiSelect = {
                            onMultiSelectToggle?.invoke()
                        },
                        onAddToQueue = { indices ->
                            onAddToQueue?.invoke(indices)
                        },
                        onAddToPlaylist = { indices ->
                            onAddToPlaylist?.invoke(indices)
                        },
                        onDeleteFromLibrary = { indices ->
                            onDeleteFromLibrary?.invoke(indices)
                        },
                        onSetMusicDirectory = onSetMusicDirectory,
                        isMultiSelectMode = isMultiSelectMode
                    )
                }
                // 艺术家列表视图
                LibraryViewState.ARTIST_LIST -> {
                    ArtistListContent(
                        artists = artists,
                        songs = songs,
                        onArtistClick = onArtistClick
                    )
                }
                // 专辑列表视图
                LibraryViewState.ALBUM_LIST -> {
                    AlbumListContent(
                        albums = albums,
                        songs = songs,
                        onAlbumClick = onAlbumClick
                    )
                }
                // 某艺术家歌曲视图
                LibraryViewState.ARTIST_SONGS -> {
                    val artistSongs = songs.filter { it.artist == selectedArtist }
                    ArtistSongsContent(
                        artistName = selectedArtist ?: "",
                        songs = artistSongs,
                        onSongClick = { index ->
                            val song = artistSongs.getOrNull(index)
                            song?.let { s -> onSongClick(songs.indexOfFirst { it.id == s.id }) }
                        },
                        onBack = {
                            onBackFromArtist?.invoke()
                            onViewStateChange(LibraryViewState.SONGS)
                        }
                    )
                }
                // 某专辑歌曲视图
                LibraryViewState.ALBUM_SONGS -> {
                    val albumSongs = songs.filter { it.album == selectedAlbum }
                    AlbumSongsContent(
                        albumName = selectedAlbum ?: "",
                        songs = albumSongs,
                        onSongClick = { index ->
                            val song = albumSongs.getOrNull(index)
                            song?.let { s -> onSongClick(songs.indexOfFirst { it.id == s.id }) }
                        },
                        onBack = {
                            onBackFromAlbum?.invoke()
                            onViewStateChange(LibraryViewState.SONGS)
                        }
                    )
                }
                // 歌单详情在 PlaylistTab.PLAYLISTS 中处理
                LibraryViewState.PLAYLIST_DETAIL -> {
                    // 此视图状态由PlaylistPanel的PLAYLISTS Tab处理
                }
            }
        }
    }
}

/**
 * 歌曲库歌曲列表内容组件
 *
 * 显示歌曲库中的歌曲列表，支持多选和操作菜单。
 * 可以将歌曲添加到播放队列、歌单，或从歌曲库中删除。
 *
 * @param songs 歌曲列表
 * @param onSongClick 点击歌曲回调
 * @param selectedIndices 已选中的歌曲索引集合
 * @param onSelectionChange 选中状态变化回调
 * @param onToggleMultiSelect 切换多选模式回调
 * @param onAddToQueue 添加到播放队列回调
 * @param onAddToPlaylist 添加到歌单回调
 * @param onDeleteFromLibrary 从歌曲库删除回调
 * @param onSetMusicDirectory 设置音乐目录回调
 * @param isMultiSelectMode 是否处于多选模式
 */
@Composable
private fun LibrarySongsContent(
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    selectedIndices: Set<Int>,
    onSelectionChange: (Int) -> Unit,
    onToggleMultiSelect: () -> Unit,
    onAddToQueue: (Set<Int>) -> Unit,
    onAddToPlaylist: (Set<Int>) -> Unit,
    onDeleteFromLibrary: (Set<Int>) -> Unit,
    onSetMusicDirectory: (() -> Unit)? = null,
    isMultiSelectMode: Boolean = false
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 操作栏：显示选中数量、操作菜单、选择/取消选择按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 显示已选择的歌曲数量
            if (selectedIndices.isNotEmpty()) {
                Text(
                    text = "已选择 ${selectedIndices.size} 首",
                    color = Color.White,
                    fontSize = 14.sp
                )
            } else {
                Spacer(modifier = Modifier)
            }
            Row {
                // 操作菜单按钮
                Text(
                    text = "操作",
                    fontSize = 20.sp,
                    color = Color(0xFF00D4AA),
                    modifier = Modifier
                        .clickable { showDropdownMenu = true }
                        .padding(8.dp)
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    // 有选中歌曲时显示批量操作选项
                    if (selectedIndices.isNotEmpty()) {
                        DropdownMenuItem(
                            text = { Text("添加到播放列表") },
                            onClick = {
                                onAddToQueue(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("添加到歌单") },
                            onClick = {
                                onAddToPlaylist(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("从歌曲库中删除") },
                            onClick = {
                                onDeleteFromLibrary(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                        Divider()
                    }
                    // 设置音乐目录选项
                    DropdownMenuItem(
                        text = { Text("设置音乐目录") },
                        onClick = {
                            onSetMusicDirectory?.invoke()
                            showDropdownMenu = false
                        }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // 选择/取消选择按钮
                Text(
                    text = if (isMultiSelectMode) "取消选择" else "选择",
                    color = Color(0xFF00D4AA),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onToggleMultiSelect() }
                )
            }
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
        if (songs.isEmpty()) {
            // 无歌曲时显示提示
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无歌曲", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(songs) { index, song ->
                    val isSelected = selectedIndices.contains(index)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isMultiSelectMode) {
                                    onSelectionChange(index)
                                } else {
                                    onSongClick(index)
                                }
                            }
                            .background(if (isSelected) Color(0xFF2A2A4E) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 复选框（仅多选模式显示）
                        if (isMultiSelectMode) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onSelectionChange(index) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF00D4AA),
                                    uncheckedColor = Color.Gray
                                )
                            )
                        } else {
                            // 非多选模式显示序号
                            Text(
                                text = "${index + 1}",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.width(24.dp)
                            )
                        }
                        // 歌曲信息：标题和艺术家
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = Color.White,
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
                        // 时长
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
}

/**
 * 排序类型选择按钮组件
 *
 * 用于歌曲库分类选择（全部/艺术家/专辑），选中的按钮高亮显示
 *
 * @param text 按钮文本
 * @param selected 是否选中
 * @param onClick 点击回调
 */
@Composable
private fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (selected) Color(0xFF00D4AA) else Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

/**
 * 艺术家列表内容组件
 *
 * 显示歌曲库中所有艺术家，点击可查看某艺术家的所有歌曲
 *
 * @param artists 艺术家名称列表
 * @param songs 歌曲列表（用于统计每个艺术家的歌曲数量）
 * @param onArtistClick 点击艺术家回调，参数为艺术家名称
 */
@Composable
private fun ArtistListContent(
    artists: List<String>,
    songs: List<Song>,
    onArtistClick: (String) -> Unit
) {
    if (artists.isEmpty()) {
        // 无艺术家时显示提示
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无艺术家", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(artists) { _, artist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onArtistClick(artist) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎵", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = artist,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        // 统计该艺术家的歌曲数量
                        val songCount = songs.count { it.artist == artist }
                        Text(
                            text = "$songCount 首歌曲",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 专辑列表内容组件
 *
 * 显示歌曲库中所有专辑，点击可查看某专辑的所有歌曲
 *
 * @param albums 专辑名称列表
 * @param songs 歌曲列表（用于统计每个专辑的歌曲数量）
 * @param onAlbumClick 点击专辑回调，参数为专辑名称
 */
@Composable
private fun AlbumListContent(
    albums: List<String>,
    songs: List<Song>,
    onAlbumClick: (String) -> Unit
) {
    if (albums.isEmpty()) {
        // 无专辑时显示提示
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无专辑", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(albums) { _, album ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAlbumClick(album) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💿", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = album,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        // 统计该专辑的歌曲数量
                        val songCount = songs.count { it.album == album }
                        Text(
                            text = "$songCount 首歌曲",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 艺术家歌曲列表内容组件
 *
 * 显示某个艺术人的所有歌曲，支持点击播放
 *
 * @param artistName 艺术家名称
 * @param songs 该艺术家的歌曲列表
 * @param onSongClick 点击歌曲回调
 * @param onBack 返回回调
 */
@Composable
private fun ArtistSongsContent(
    artistName: String,
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮和标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "←", fontSize = 20.sp, color = Color(0xFF00D4AA))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = artistName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${songs.size})",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("暂无歌曲，点击返回", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(songs) { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongClick(index) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎵", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // 显示歌曲所属专辑
                            Text(
                                text = song.album,
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
}

/**
 * 专辑歌曲列表内容组件
 *
 * 显示某个专辑的所有歌曲，支持点击播放
 *
 * @param albumName 专辑名称
 * @param songs 该专辑的歌曲列表
 * @param onSongClick 点击歌曲回调
 * @param onBack 返回回调
 */
@Composable
private fun AlbumSongsContent(
    albumName: String,
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮和标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "←", fontSize = 20.sp, color = Color(0xFF00D4AA))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = albumName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${songs.size})",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text("暂无歌曲，点击返回", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(songs) { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongClick(index) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💿", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            // 显示歌曲所属艺术家
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
}

/**
 * 当前播放列表内容组件
 *
 * 显示当前正在播放的歌曲列表，支持多选模式和操作菜单
 * 当前播放的歌曲会高亮显示（主题色）
 *
 * @param songs 播放列表中的歌曲
 * @param currentIndex 当前播放的歌曲索引
 * @param onSongClick 点击歌曲回调
 * @param selectedIndices 已选中的歌曲索引集合
 * @param onSelectionChange 选中状态变化回调
 * @param onToggleMultiSelect 切换多选模式回调
 * @param onClearPlaylist 清空播放列表回调
 * @param onDeleteSelected 删除选中歌曲回调
 * @param onAddToPlaylist 添加选中歌曲到歌单回调
 * @param isMultiSelectMode 是否处于多选模式
 */
@Composable
private fun PlayingListContent(
    songs: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    selectedIndices: Set<Int>,
    onSelectionChange: (Int) -> Unit,
    onToggleMultiSelect: () -> Unit,
    onClearPlaylist: (() -> Unit)?,
    onDeleteSelected: (Set<Int>) -> Unit,
    onAddToPlaylist: (List<Song>) -> Unit,
    isMultiSelectMode: Boolean = false
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 操作栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedIndices.isNotEmpty()) {
                Text(
                    text = "已选择 ${selectedIndices.size} 首",
                    color = Color.White,
                    fontSize = 14.sp
                )
            } else {
                Spacer(modifier = Modifier)
            }
            Row {
                Text(
                    text = "操作",
                    fontSize = 20.sp,
                    color = Color(0xFF00D4AA),
                    modifier = Modifier
                        .clickable { showDropdownMenu = true }
                        .padding(8.dp)
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    // 清空播放列表选项
                    DropdownMenuItem(
                        text = { Text("清空播放列表") },
                        onClick = {
                            onClearPlaylist?.invoke()
                            showDropdownMenu = false
                        }
                    )
                    // 有选中歌曲时显示删除和添加选项
                    if (selectedIndices.isNotEmpty()) {
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                onDeleteSelected(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("添加到歌单") },
                            onClick = {
                                val selectedSongs = selectedIndices.map { songs[it] }
                                onAddToPlaylist(selectedSongs)
                                showDropdownMenu = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMultiSelectMode) "取消选择" else "选择",
                    color = Color(0xFF00D4AA),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onToggleMultiSelect() }
                )
            }
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
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
                    val isSelected = selectedIndices.contains(index)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isMultiSelectMode) {
                                    onSelectionChange(index)
                                } else {
                                    onSongClick(index)
                                }
                            }
                            .background(
                                when {
                                    isSelected -> Color(0xFF2A2A4E)
                                    isCurrentSong -> Color(0xFF2A2A4E)
                                    else -> Color.Transparent
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 复选框（仅在多选模式显示）
                        if (isMultiSelectMode) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { onSelectionChange(index) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF00D4AA),
                                    uncheckedColor = Color.Gray
                                )
                            )
                        } else {
                            // 非多选模式显示序号，当前歌曲序号高亮
                            Text(
                                text = "${index + 1}",
                                color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.width(24.dp)
                            )
                        }
                        // 歌曲信息：标题和艺术家
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
                        // 时长
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
}

/**
 * Tab按钮组件
 *
 * 用于播放列表面板的Tab切换（播放列表/歌单/歌曲库）
 *
 * @param text 按钮文本
 * @param selected 是否选中，选中时高亮显示
 * @param onClick 点击回调
 */
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

/**
 * 播放列表内容组件（已弃用，被PlayingListContent替代）
 *
 * 显示播放列表中的歌曲，当前歌曲高亮显示
 *
 * @param songs 歌曲列表
 * @param currentIndex 当前播放的歌曲索引
 * @param onSongClick 点击歌曲回调
 * @param onMultiSelectToggle 切换多选模式回调（可为null）
 * @param isMultiSelectMode 是否处于多选模式
 * @param selectedIndices 已选中的歌曲索引集合
 * @param onSelectionChange 选中状态变化回调（可为null）
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlaylistContent(
    songs: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    onMultiSelectToggle: (() -> Unit)? = null,
    isMultiSelectMode: Boolean = false,
    selectedIndices: Set<Int> = emptySet(),
    onSelectionChange: ((Int) -> Unit)? = null
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
                val isSelected = selectedIndices.contains(index)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isMultiSelectMode && onSelectionChange != null) {
                                Modifier.clickable { onSelectionChange(index) }
                            } else {
                                Modifier.clickable { onSongClick(index) }
                            }
                        )
                        .background(if (isCurrentSong) Color(0xFF2A2A4E) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 复选框（多选模式时显示）
                    if (isMultiSelectMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onSelectionChange?.invoke(index) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00D4AA),
                                uncheckedColor = Color.Gray
                            )
                        )
                    }
                    // 序号，当前歌曲高亮
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(if (isMultiSelectMode) 24.dp else 32.dp)
                    )
                    // 歌曲信息
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
                    // 时长
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

/**
 * 多选模式下的播放列表内容组件
 *
 * 与PlayingListContent类似，但默认处于多选模式，显示复选框
 *
 * @param songs 歌曲列表
 * @param currentIndex 当前播放的歌曲索引
 * @param selectedIndices 已选中的歌曲索引集合
 * @param onSelectionChange 选中状态变化回调
 * @param onToggleMultiSelect 退出多选模式回调
 * @param onClearPlaylist 清空播放列表回调
 * @param onDeleteSelected 删除选中歌曲回调
 * @param onAddToPlaylist 添加选中歌曲到歌单回调
 * @param isMultiSelectMode 是否处于多选模式
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MultiSelectPlaylistContent(
    songs: List<Song>,
    currentIndex: Int,
    selectedIndices: Set<Int>,
    onSelectionChange: (Int) -> Unit,
    onToggleMultiSelect: () -> Unit,
    onClearPlaylist: (() -> Unit)?,
    onDeleteSelected: (Set<Int>) -> Unit,
    onAddToPlaylist: (List<Song>) -> Unit,
    isMultiSelectMode: Boolean = false
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 操作栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedIndices.isNotEmpty()) {
                Text(
                    text = "已选择 ${selectedIndices.size} 首",
                    color = Color.White,
                    fontSize = 14.sp
                )
            } else {
                Spacer(modifier = Modifier)
            }
            Row {
                Text(
                    text = "操作",
                    fontSize = 20.sp,
                    color = Color(0xFF00D4AA),
                    modifier = Modifier
                        .clickable { showDropdownMenu = true }
                        .padding(8.dp)
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("清空播放列表") },
                        onClick = {
                            onClearPlaylist?.invoke()
                            showDropdownMenu = false
                        }
                    )
                    if (selectedIndices.isNotEmpty()) {
                        DropdownMenuItem(
                            text = { Text("删除") },
                            onClick = {
                                onDeleteSelected(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("添加到歌单") },
                            onClick = {
                                val selectedSongs = selectedIndices.map { songs[it] }
                                onAddToPlaylist(selectedSongs)
                                showDropdownMenu = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMultiSelectMode) "取消选择" else "选择",
                    color = Color(0xFF00D4AA),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onToggleMultiSelect() }
                )
            }
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(songs) { index, song ->
                val isCurrentSong = index == currentIndex
                val isSelected = selectedIndices.contains(index)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectionChange(index) }
                        .background(if (isSelected) Color(0xFF2A2A4E) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 复选框
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSelectionChange(index) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00D4AA),
                            uncheckedColor = Color.Gray
                        )
                    )
                    // 序号
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(24.dp)
                    )
                    // 歌曲信息
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
                    // 时长
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

/**
 * 歌单列表内容组件
 *
 * 显示用户创建的所有歌单，支持创建新歌单和删除已有歌单
 *
 * @param playlists 歌单名称列表
 * @param onPlaylistClick 点击歌单回调，参数为歌单名称
 * @param onCreateClick 点击创建新歌单回调
 * @param onDeleteClick 点击删除歌单回调，参数为歌单名称
 */
@Composable
private fun PlaylistListContent(
    playlists: List<String>,
    onPlaylistClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 创建歌单按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCreateClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📋", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "创建新歌单",
                color = Color(0xFF00D4AA),
                fontSize = 14.sp
            )
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌单列表
        if (playlists.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无歌单", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(playlists) { _, name ->
                    PlaylistItem(
                        name = name,
                        onClick = { onPlaylistClick(name) },
                        onDelete = { onDeleteClick(name) }
                    )
                }
            }
        }
    }
}

/**
 * 歌单项组件
 *
 * 显示单个歌单的名称和删除按钮
 *
 * @param name 歌单名称
 * @param onClick 点击回调
 * @param onDelete 删除回调
 */
@Composable
private fun PlaylistItem(
    name: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "📋", fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        // 删除按钮
        Text(
            text = "🗑",
            fontSize = 16.sp,
            modifier = Modifier.clickable { onDelete() }
        )
    }
}

/**
 * 格式化时长为 MM:SS 格式
 *
 * 将毫秒级的时长转换为分:秒格式显示
 *
 * @param time 时长（毫秒）
 * @return 格式化后的字符串，如 "03:45"
 */
private fun formatTime(time: Long): String {
    if (time <= 0) return "00:00"
    val seconds = (time / 1000) % 60
    val minutes = (time / 1000) / 60
    return "%02d:%02d".format(minutes, seconds)
}

/**
 * 歌曲库多选内容组件
 *
 * 用于歌曲库Tab的多选模式，显示所有歌曲并支持批量操作
 *
 * @param songs 歌曲列表
 * @param selectedIndices 已选中的歌曲索引集合
 * @param onSongToggle 切换选中状态回调
 * @param onToggleMultiSelect 退出多选模式回调
 * @param onAddToQueue 添加选中歌曲到播放队列回调
 * @param onAddToPlaylist 添加选中歌曲到歌单回调
 * @param onDeleteFromLibrary 从歌曲库删除选中歌曲回调
 * @param isMultiSelectMode 是否处于多选模式
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LibraryMultiSelectContent(
    songs: List<Song>,
    selectedIndices: Set<Int>,
    onSongToggle: (Int) -> Unit,
    onToggleMultiSelect: () -> Unit,
    onAddToQueue: (Set<Int>) -> Unit,
    onAddToPlaylist: (Set<Int>) -> Unit,
    onDeleteFromLibrary: (Set<Int>) -> Unit,
    isMultiSelectMode: Boolean = false
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框（多选模式下禁用输入）
        BasicTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        )

        // 操作栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedIndices.isNotEmpty()) {
                Text(
                    text = "已选择 ${selectedIndices.size} 首",
                    color = Color.White,
                    fontSize = 14.sp
                )
            } else {
                Spacer(modifier = Modifier)
            }
            Row {
                Text(
                    text = "操作",
                    fontSize = 20.sp,
                    color = Color(0xFF00D4AA),
                    modifier = Modifier
                        .clickable { showDropdownMenu = true }
                        .padding(8.dp)
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("添加到播放列表") },
                        onClick = {
                            onAddToQueue(selectedIndices)
                            showDropdownMenu = false
                        }
                    )
                    if (selectedIndices.isNotEmpty()) {
                        DropdownMenuItem(
                            text = { Text("添加到歌单") },
                            onClick = {
                                onAddToPlaylist(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("从歌曲库中删除") },
                            onClick = {
                                onDeleteFromLibrary(selectedIndices)
                                showDropdownMenu = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMultiSelectMode) "取消选择" else "选择",
                    color = Color(0xFF00D4AA),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onToggleMultiSelect() }
                )
            }
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(songs) { index, song ->
                val isSelected = selectedIndices.contains(index)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSongToggle(index) }
                        .background(if (isSelected) Color(0xFF2A2A4E) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isMultiSelectMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onSongToggle(index) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00D4AA),
                                uncheckedColor = Color.Gray
                            )
                        )
                    }
                    Text(
                        text = "${index + 1}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = song.title,
                            color = Color.White,
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

/**
 * 歌单详情内容组件
 *
 * 显示某个歌单中的所有歌曲，支持播放和删除歌曲
 *
 * @param playlistName 歌单名称
 * @param songs 歌单中的歌曲列表
 * @param onSongClick 点击歌曲回调
 * @param onBack 返回回调
 * @param onDeleteSong 删除歌曲回调，参数为歌曲索引（可为null）
 */
@Composable
private fun PlaylistDetailContent(
    playlistName: String,
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    onBack: () -> Unit,
    onDeleteSong: ((Int) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮和标题栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "←", fontSize = 20.sp, color = Color(0xFF00D4AA))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = playlistName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${songs.size})",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Divider(color = Color.Gray.copy(alpha = 0.2f))

        // 歌曲列表
        if (songs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("歌单暂无歌曲", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(songs) { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSongClick(index) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💿", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = Color.White,
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
                        // 删除按钮（仅当onDeleteSong不为null时显示）
                        if (onDeleteSong != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🗑",
                                fontSize = 16.sp,
                                modifier = Modifier.clickable { onDeleteSong(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}