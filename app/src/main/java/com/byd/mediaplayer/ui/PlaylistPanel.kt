package com.byd.mediaplayer.ui

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

enum class PlaylistTab {
    PLAYING,    // 当前播放
    PLAYLISTS,  // 歌单
    LIBRARY     // 歌曲库
}

enum class LibrarySortType {
    ALL,        // 全部
    BY_ARTIST,  // 按艺术家
    BY_ALBUM    // 按专辑
}

enum class LibraryViewState {
    SONGS,          // 歌曲列表
    ARTIST_LIST,    // 艺术家列表
    ALBUM_LIST,     // 专辑列表
    ARTIST_SONGS,   // 艺术家歌曲列表
    ALBUM_SONGS,    // 专辑歌曲列表
    PLAYLIST_DETAIL // 歌单详情
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
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<String?>(null) }
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    var songToAdd by remember { mutableStateOf<Song?>(null) }
    var viewState by remember { mutableStateOf(LibraryViewState.SONGS) }

    // 当外部传入的selectedArtist变化时，自动切换到对应视图
    LaunchedEffect(selectedArtist) {
        if (selectedArtist != null) {
            viewState = LibraryViewState.ARTIST_SONGS
        }
    }

    // 当外部传入的selectedAlbum变化时，自动切换到对应视图
    LaunchedEffect(selectedAlbum) {
        if (selectedAlbum != null) {
            viewState = LibraryViewState.ALBUM_SONGS
        }
    }

    // 多选模式状态
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedSongIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var showMenu by remember { mutableStateOf(false) }

    // Library多选模式状态
    var libraryMultiSelectMode by remember { mutableStateOf(false) }
    var librarySelectedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally { it },
        exit = slideOutHorizontally { it }
    ) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth()
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
                    TabButton("播放列表", currentTab == PlaylistTab.PLAYING) {
                        onTabChange(PlaylistTab.PLAYING)
                    }
                    TabButton("歌单", currentTab == PlaylistTab.PLAYLISTS) {
                        onTabChange(PlaylistTab.PLAYLISTS)
                    }
                    TabButton("本地歌曲库", currentTab == PlaylistTab.LIBRARY) {
                        onTabChange(PlaylistTab.LIBRARY)
                    }
                }

    // 内容区域
                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        PlaylistTab.PLAYING -> {
                            if (isMultiSelectMode) {
                                MultiSelectPlaylistContent(
                                    songs = currentPlaylist,
                                    currentIndex = currentSongIndex,
                                    selectedIndices = selectedSongIndices,
                                    onToggleMultiSelect = {
                                        isMultiSelectMode = false
                                        selectedSongIndices = emptySet()
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
                            } else {
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
                        PlaylistTab.PLAYLISTS -> {
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
                        PlaylistTab.LIBRARY -> {
                            if (viewState == LibraryViewState.SONGS) {
                                if (libraryMultiSelectMode) {
                                    LibraryMultiSelectContent(
                                        songs = allSongs,
                                        selectedIndices = librarySelectedIndices,
                                        onSongClick = { index ->
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
                                        }
                                    )
                                }
                            } else {
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
                                    }
                                )
                            }
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

            // 创建歌单对话框
            if (showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text("创建歌单") },
                    text = {
                        BasicTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            decorationBox = { innerTextField ->
                                Box {
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

            // 删除歌单对话框
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

            // 添加到歌单对话框
            if (showAddToPlaylistDialog && songToAdd != null) {
                AlertDialog(
                    onDismissRequest = { showAddToPlaylistDialog = false },
                    title = { Text("添加到歌单") },
                    text = {
                        Column {
                            if (playlists.isEmpty()) {
                                Text("暂无歌单，请先创建", color = Color.Gray, fontSize = 14.sp)
                            } else {
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
                                        Text(text = "⋮", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = playlistName, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    },
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
    onDeleteFromLibrary: ((Set<Int>) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框
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
                SortChip("全部", sortType == LibrarySortType.ALL) {
                    onSortTypeChange(LibrarySortType.ALL)
                    onViewStateChange(LibraryViewState.SONGS)
                }
                SortChip("艺术家", sortType == LibrarySortType.BY_ARTIST) {
                    onSortTypeChange(LibrarySortType.BY_ARTIST)
                    onViewStateChange(LibraryViewState.ARTIST_LIST)
                }
                SortChip("专辑", sortType == LibrarySortType.BY_ALBUM) {
                    onSortTypeChange(LibrarySortType.BY_ALBUM)
                    onViewStateChange(LibraryViewState.ALBUM_LIST)
                }
            }
        }

        // 内容区域
        Box(modifier = Modifier.weight(1f)) {
            when (viewState) {
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
                        isMultiSelectMode = isMultiSelectMode
                    )
                }
                LibraryViewState.ARTIST_LIST -> {
                    ArtistListContent(
                        artists = artists,
                        songs = songs,
                        onArtistClick = onArtistClick
                    )
                }
                LibraryViewState.ALBUM_LIST -> {
                    AlbumListContent(
                        albums = albums,
                        songs = songs,
                        onAlbumClick = onAlbumClick
                    )
                }
                LibraryViewState.ARTIST_SONGS -> {
                    ArtistSongsContent(
                        artistName = selectedArtist ?: "",
                        songs = songs.filter { it.artist == selectedArtist },
                        onSongClick = onSongClick,
                        onBack = {
                            onBackFromArtist?.invoke()
                            onViewStateChange(LibraryViewState.SONGS)
                        }
                    )
                }
                LibraryViewState.ALBUM_SONGS -> {
                    AlbumSongsContent(
                        albumName = selectedAlbum ?: "",
                        songs = songs.filter { it.album == selectedAlbum },
                        onSongClick = onSongClick,
                        onBack = {
                            onBackFromAlbum?.invoke()
                            onViewStateChange(LibraryViewState.SONGS)
                        }
                    )
                }
                LibraryViewState.PLAYLIST_DETAIL -> {
                    // 歌单详情在 PlaylistTab.PLAYLISTS 中处理
                }
            }
        }
    }
}

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
            Text(
                text = "已选择 ${selectedIndices.size} 首",
                color = Color.White,
                fontSize = 14.sp
            )
            Row {
                Text(
                    text = "⋮",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { showDropdownMenu = true }
                        .padding(8.dp)
                )
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
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
                    val isSelected = selectedIndices.contains(index)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (selectedIndices.isNotEmpty()) {
                                    onSelectionChange(index)
                                } else {
                                    onSongClick(index)
                                }
                            }
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
                        Text(
                            text = "${index + 1}",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.width(24.dp)
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
}

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

@Composable
private fun ArtistListContent(
    artists: List<String>,
    songs: List<Song>,
    onArtistClick: (String) -> Unit
) {
    if (artists.isEmpty()) {
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

@Composable
private fun AlbumListContent(
    albums: List<String>,
    songs: List<Song>,
    onAlbumClick: (String) -> Unit
) {
    if (albums.isEmpty()) {
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

@Composable
private fun ArtistSongsContent(
    artistName: String,
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮和标题
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无歌曲", color = Color.Gray, fontSize = 14.sp)
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

@Composable
private fun AlbumSongsContent(
    albumName: String,
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮和标题
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无歌曲", color = Color.Gray, fontSize = 14.sp)
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
                    }
                }
            }
        }
    }
}

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
            Text(
                text = "已选择 ${selectedIndices.size} 首",
                color = Color.White,
                fontSize = 14.sp
            )
            Row {
                Text(
                    text = "⋮",
                    fontSize = 20.sp,
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
                                if (selectedIndices.isNotEmpty()) {
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
                        // 复选框
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onSelectionChange(index) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF00D4AA),
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "${index + 1}",
                            color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.width(24.dp)
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
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(if (isMultiSelectMode) 24.dp else 32.dp)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MultiSelectPlaylistContent(
    songs: List<Song>,
    currentIndex: Int,
    selectedIndices: Set<Int>,
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
            Text(
                text = "已选择 ${selectedIndices.size} 首",
                color = Color.White,
                fontSize = 14.sp
            )
            Row {
                Text(
                    text = "⋮",
                    fontSize = 20.sp,
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
                        .clickable {
                            if (selectedIndices.contains(index)) {
                                // 已在选中列表，移除
                            } else {
                                // 添加到选中列表
                            }
                        }
                        .background(if (isSelected) Color(0xFF2A2A4E) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            if (it) {
                                // 添加到选中
                            } else {
                                // 从选中移除
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00D4AA),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(
                        text = "${index + 1}",
                        color = if (isCurrentSong) Color(0xFF00D4AA) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(24.dp)
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
            Text(text = "➕", fontSize = 20.sp)
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlaylistClick(name) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⋮", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = name,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "🗑",
                            fontSize = 16.sp,
                            modifier = Modifier.clickable { onDeleteClick(name) }
                        )
                    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LibraryMultiSelectContent(
    songs: List<Song>,
    selectedIndices: Set<Int>,
    onSongClick: (Int) -> Unit,
    onToggleMultiSelect: () -> Unit,
    onAddToQueue: (Set<Int>) -> Unit,
    onAddToPlaylist: (Set<Int>) -> Unit,
    onDeleteFromLibrary: (Set<Int>) -> Unit,
    isMultiSelectMode: Boolean = false
) {
    var showDropdownMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框
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
            Text(
                text = "已选择 ${selectedIndices.size} 首",
                color = Color.White,
                fontSize = 14.sp
            )
            Row {
                Text(
                    text = "⋮",
                    fontSize = 20.sp,
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
                        .clickable { onSongClick(index) }
                        .background(if (isSelected) Color(0xFF2A2A4E) else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSongClick(index) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00D4AA),
                            uncheckedColor = Color.Gray
                        )
                    )
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

@Composable
private fun PlaylistDetailContent(
    playlistName: String,
    songs: List<Song>,
    onSongClick: (Int) -> Unit,
    onBack: () -> Unit,
    onDeleteSong: ((Int) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮和标题
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