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
                            PlaylistContent(
                                songs = currentPlaylist,
                                currentIndex = currentSongIndex,
                                onSongClick = onSongClick
                            )
                        }
                        PlaylistTab.PLAYLISTS -> {
                            if (viewState == LibraryViewState.PLAYLIST_DETAIL && selectedPlaylistName != null) {
                                PlaylistDetailContent(
                                    playlistName = selectedPlaylistName,
                                    songs = getPlaylistSongs?.invoke(selectedPlaylistName) ?: emptyList(),
                                    onSongClick = onSongClick,
                                    onBack = { onBackFromPlaylist?.invoke() }
                                )
                            } else {
                                PlaylistListContent(
                                    playlists = playlists,
                                    onPlaylistClick = onPlaylistClick ?: { name ->
                                        viewState = LibraryViewState.PLAYLIST_DETAIL
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
                            LibraryContent(
                                songs = allSongs,
                                searchQuery = searchQuery,
                                onSearchQueryChange = onSearchQueryChange ?: {},
                                sortType = sortType,
                                onSortTypeChange = onSortTypeChange ?: {},
                                artists = artists,
                                albums = albums,
                                onSongClick = onSongClick,
                                viewState = viewState,
                                onViewStateChange = { viewState = it },
                                onArtistClick = onArtistClick ?: { viewState = LibraryViewState.ARTIST_SONGS },
                                onAlbumClick = onAlbumClick ?: { viewState = LibraryViewState.ALBUM_SONGS },
                                selectedArtist = selectedArtist,
                                selectedAlbum = selectedAlbum,
                                onBackFromArtist = onBackFromArtist ?: { viewState = LibraryViewState.ARTIST_LIST },
                                onBackFromAlbum = onBackFromAlbum ?: { viewState = LibraryViewState.ALBUM_LIST }
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

            // 创建歌单对话框
            if (showCreateDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    title = { Text("创建歌单") },
                    text = {
                        BasicTextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            decoration = { Text("请输入歌单名称") },
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
                                        Text(text = "📋", fontSize = 16.sp)
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
    viewState: LibraryViewState,
    onViewStateChange: (LibraryViewState) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    selectedArtist: String?,
    selectedAlbum: String?,
    onBackFromArtist: () -> Unit,
    onBackFromAlbum: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索框
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            decoration = {
                Text(
                    text = "搜索歌曲...",
                    color = Color.Gray
                )
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
                    PlaylistContent(
                        songs = songs,
                        currentIndex = -1,
                        onSongClick = onSongClick,
                        onSongLongPress = { song ->
                            songToAdd = song
                            showAddToPlaylistDialog = true
                        }
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
                        onBack = onBackFromArtist
                    )
                }
                LibraryViewState.ALBUM_SONGS -> {
                    AlbumSongsContent(
                        albumName = selectedAlbum ?: "",
                        songs = songs.filter { it.album == selectedAlbum },
                        onSongClick = onSongClick,
                        onBack = onBackFromAlbum
                    )
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

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

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

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

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
    onSongClick: (Int) -> Unit,
    onSongLongPress: ((Song) -> Unit)? = null
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
                        .then(
                            if (onSongLongPress != null) {
                                Modifier.combinedClickable(
                                    onClick = { onSongClick(index) },
                                    onLongClick = { onSongLongPress(song) }
                                )
                            } else {
                                Modifier.clickable { onSongClick(index) }
                            }
                        )
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

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

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
                        Text(text = "📋", fontSize = 20.sp)
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

@Composable
private fun PlaylistDetailContent(
    playlistName: String,
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

        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

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
                        Text(text = "📋", fontSize = 16.sp)
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