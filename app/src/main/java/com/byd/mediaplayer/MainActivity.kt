package com.byd.mediaplayer

/**
 * 本文件是本地MP3音乐播放器的主活动（MainActivity）
 *
 * 功能概述：
 * 1. 应用程序的入口点，负责初始化UI和绑定播放器服务
 * 2. 管理播放列表、歌单和歌曲库的数据
 * 3. 处理权限请求（存储权限、通知权限等）
 * 4. 协调UI和播放器服务之间的状态同步
 * 5. 支持从SAF（Storage Access Framework）选择音乐目录
 *
 * 架构说明：
 * - 采用Jetpack Compose构建响应式UI
 * - 使用StateFlow和Compose状态管理播放状态
 * - 通过ServiceConnection绑定PlayerService获取播放器服务
 * - 使用协程处理异步操作（如数据库操作、文件扫描等）
 */

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.byd.mediaplayer.data.MediaStoreHelper
import com.byd.mediaplayer.data.MusicRepository
import com.byd.mediaplayer.data.database.AppDatabase
import com.byd.mediaplayer.model.Lyrics
import com.byd.mediaplayer.util.Logger
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.player.PlayerManager
import com.byd.mediaplayer.player.PlayerService
import com.byd.mediaplayer.ui.LibraryViewState
import com.byd.mediaplayer.ui.LibrarySortType
import com.byd.mediaplayer.ui.PlaylistTab
import com.byd.mediaplayer.ui.PlayerScreen
import com.byd.mediaplayer.util.LrcParser
import com.byd.mediaplayer.util.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    private var playerService: PlayerService? = null
    private var serviceBound = false
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var audioManager: AudioManager
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.LocalBinder
            playerService = binder.getService()
            serviceBound = true
            loadSongsAndStartPlay()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
            serviceBound = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            loadSongsAndStartPlay()
        }
    }

    // 目录选择器结果Flow
    private val _directoryPickerResult = kotlinx.coroutines.flow.MutableSharedFlow<Uri?>()
    private val directoryPickerResult = _directoryPickerResult.asSharedFlow()

    private val directoryPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            // 持久化权限
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                Logger.e(TAG, "持久化目录权限失败: ${e.message}")
            }
            // 通过Flow通知composable
            activityScope.launch {
                _directoryPickerResult.emit(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "MainActivity onCreate - 开始")

        preferencesManager = PreferencesManager(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        Logger.d(TAG, "MainActivity onCreate - setContent前")
        setContent {
            // Logger.d(TAG, "PlayerScreenWithState - 开始渲染")
            MaterialTheme {
                // Logger.d(TAG, "PlayerScreenWithState - Surface前")
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Logger.d(TAG, "PlayerScreenWithState - 调用前")
                    PlayerScreenWithState()
                    // Logger.d(TAG, "PlayerScreenWithState - 调用后")
                }
                // Logger.d(TAG, "PlayerScreenWithState - Surface后")
            }
            // Logger.d(TAG, "PlayerScreenWithState - MaterialTheme后")
        }
        Logger.d(TAG, "MainActivity onCreate - setContent后")

        checkAndRequestPermissions()
        Logger.d(TAG, "MainActivity onCreate - 完成")
    }

    override fun onStart() {
        super.onStart()
        bindToService()
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
        // 保存播放状态和播放列表
        playerService?.getPlayerManager()?.let { manager ->
            preferencesManager.lastPlayedPosition = manager.currentPosition
            preferencesManager.lastPlayMode = manager.playMode.name
            manager.currentSong?.let {
                preferencesManager.lastPlayedSongId = it.id
            }
            // 保存播放列表到数据库
            val repository = MusicRepository.getInstance(this)
            activityScope.launch(Dispatchers.IO) {
                repository.saveCurrentPlaylist(manager.playlist, manager.currentIndex, manager.playMode.name)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

    private fun checkAndRequestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allGranted) {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun bindToService() {
        val intent = Intent(this, PlayerService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun loadSongsAndStartPlay() {
        val service = playerService ?: return
        service.startForegroundService()
        // 歌曲加载和播放状态恢复统一由 LaunchedEffect(playerService) 处理
    }

    @Composable
    private fun PlayerScreenWithState() {
        // 使用remember和mutableStateOf创建响应式状态
        var currentSong by remember { mutableStateOf<Song?>(null) }
        var isPlaying by remember { mutableStateOf(false) }
        var playlist by remember { mutableStateOf<List<Song>>(emptyList()) }
        var librarySongs by remember { mutableStateOf<List<Song>>(emptyList()) }
        var libraryDisplaySongs by remember { mutableStateOf<List<Song>>(emptyList()) }
        var playlistSongCache by remember { mutableStateOf<Map<String, List<Song>>>(emptyMap()) }
        var currentPosition by remember { mutableLongStateOf(0L) }
        var duration by remember { mutableLongStateOf(0L) }
        var playMode by remember { mutableStateOf(PlayMode.LIST_LOOP) }
        var lyrics by remember { mutableStateOf<Lyrics?>(null) }
        var volume by remember { mutableFloatStateOf(1.0f) }
        var showPlaylistPanel by remember { mutableStateOf(false) }
        var playlistTab by remember { mutableStateOf(PlaylistTab.PLAYING) }
        var searchQuery by remember { mutableStateOf("") }
        var sortType by remember { mutableStateOf(LibrarySortType.ALL) }
        var playlists by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
        var artists by remember { mutableStateOf<List<String>>(emptyList()) }
        var albums by remember { mutableStateOf<List<String>>(emptyList()) }
        var searchSongsRef by remember { mutableStateOf<((String) -> Unit)?>(null) }
        var selectedArtist by remember { mutableStateOf<String?>(null) }
        var selectedAlbum by remember { mutableStateOf<String?>(null) }
        var selectedArtistSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
        var selectedAlbumSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
        var libraryViewState by remember { mutableStateOf(LibraryViewState.SONGS) }
        var selectedPlaylistName by remember { mutableStateOf<String?>(null) }
        var selectedPlaylistSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
        var musicDirectoryUri by remember { mutableStateOf<Uri?>(null) }

        // 监听目录选择结果
        LaunchedEffect(directoryPickerResult) {
            directoryPickerResult.collect { uri: Uri? ->
                uri?.let { selectedUri ->
                    Logger.d(TAG, "收到目录选择结果: $selectedUri")
                    preferencesManager.musicDirectoryUri = selectedUri.toString()
                    musicDirectoryUri = selectedUri
                    // 重新加载歌曲
                    val repository = MusicRepository.getInstance(this@MainActivity)
                    val newSongs = MediaStoreHelper.querySongsFromDirectory(this@MainActivity, selectedUri)
                    librarySongs = newSongs
                    libraryDisplaySongs = newSongs
                    playlist = newSongs
                    Logger.i(TAG, "歌曲重新加载完成，共 ${newSongs.size} 首")
                }
            }
        }

        fun openDirectoryPicker() {
            Logger.d(TAG, "打开目录选择器")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            directoryPickerLauncher.launch(intent)
        }

        // // Logger.d(TAG, "PlayerScreenWithState - 初始化完成，开始LaunchedEffect")

        LaunchedEffect(playerService) {
            while (playerService == null) {
                delay(100)
            }
            val service = playerService ?: return@LaunchedEffect
            val manager = service.getPlayerManager()
            val repository = MusicRepository.getInstance(this@MainActivity)

            // 加载保存的音乐目录设置
            preferencesManager.musicDirectoryUri?.let { uriString ->
                try {
                    musicDirectoryUri = Uri.parse(uriString)
                } catch (e: Exception) {
                    Logger.e(TAG, "解析音乐目录URI失败: ${e.message}")
                }
            }

            // 加载歌单
            val database = AppDatabase.getInstance(this@MainActivity)
            val playlistEntities = database.playlistDao().getAllPlaylistsOnce()
            playlists = playlistEntities.map { it.name to database.playlistDao().getPlaylistSongCount(it.id) }

            // 加载歌曲到歌曲库（只有设置了音乐目录才自动加载）
            if (musicDirectoryUri != null && librarySongs.isEmpty()) {
                val allSongs = MediaStoreHelper.querySongsFromDirectory(this@MainActivity, musicDirectoryUri!!)
                librarySongs = allSongs
                libraryDisplaySongs = allSongs
                playlist = allSongs
                Logger.i(TAG, "从目录加载歌曲完成: ${allSongs.size}首")
            } else if (musicDirectoryUri == null) {
                // 未设置目录
                librarySongs = emptyList()
                libraryDisplaySongs = emptyList()
                playlist = emptyList()
                Logger.i(TAG, "未设置音乐目录，等待用户设置")
            }

            // 加载艺术家和专辑列表（仅在有歌曲时）
            if (musicDirectoryUri != null) {
                artists = MediaStoreHelper.getAllArtists(this@MainActivity)
                albums = MediaStoreHelper.getAllAlbums(this@MainActivity)
            } else {
                artists = emptyList()
                albums = emptyList()
            }

            // 仅在非首次启动时恢复播放状态（避免卸载重装后自动播放）
            if (!preferencesManager.isFirstLaunch && playlist.isNotEmpty()) {
                val startIndex = preferencesManager.lastPlayedSongId.let { lastId ->
                    playlist.indexOfFirst { it.id == lastId }.takeIf { it >= 0 } ?: 0
                }
                manager.setPlaylist(playlist, startIndex)
                Logger.i(TAG, "恢复播放列表: index=$startIndex")
            } else {
                // 首次启动，标记并等待用户主动播放
                preferencesManager.isFirstLaunch = false
                Logger.i(TAG, "首次启动，不自动恢复播放状态")
            }

            // 音量
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = preferencesManager.lastVolume
            volume = currentVolume * maxVolume

            // 保存repository引用用于搜索
            val searchRepository = repository

            // 保存搜索函数引用（使用SAF目录扫描）
            // 注意：使用getter函数而不是捕获值，确保每次搜索使用最新的目录设置
            searchSongsRef = { query ->
                // 防抖机制：延迟搜索，避免频繁搜索
                activityScope.launch {
                    delay(300) // 等待300ms后执行搜索，避免频繁搜索
                    val currentDirUri = preferencesManager.musicDirectoryUri?.let { Uri.parse(it) }
                    val songs = if (currentDirUri != null) {
                        MediaStoreHelper.querySongsFromDirectory(this@MainActivity, currentDirUri)
                    } else {
                        emptyList()
                    }
                    libraryDisplaySongs = if (query.isBlank()) {
                        songs
                    } else {
                        songs.filter {
                            it.title.contains(query, ignoreCase = true) ||
                            it.artist.contains(query, ignoreCase = true) ||
                            it.album.contains(query, ignoreCase = true)
                        }
                    }
                }
            }
        }

        // 播放器状态变化监听器
        DisposableEffect(playerService) {
            val service = playerService ?: return@DisposableEffect onDispose { }
            val manager = service.getPlayerManager()

            val listener = object : PlayerManager.PlayerListener {
                override fun onPlaybackStateChanged(song: Song?, playing: Boolean) {
                    Logger.d(TAG, "监听器收到状态变化: isPlaying=$playing, song=${song?.title}")
                    currentSong = song
                    isPlaying = playing
                }

                override fun onPositionChanged(position: Long, len: Long) {
                    currentPosition = position
                    duration = len
                }
            }

            manager.addListener(listener)

            onDispose {
                manager.removeListener(listener)
            }
        }

        // 位置轮询（用于进度条持续更新）
        val playerManagerForPosition = playerService?.getPlayerManager()
        LaunchedEffect(playerManagerForPosition) {
            val manager = playerManagerForPosition ?: return@LaunchedEffect
            while (true) {
                currentPosition = manager.currentPosition
                duration = manager.duration
                delay(500)
            }
        }

        // 重命名歌单函数
        fun renamePlaylist(oldName: String, newName: String) {
            activityScope.launch(Dispatchers.IO) {
                val database = AppDatabase.getInstance(this@MainActivity)
                database.playlistDao().getAllPlaylistsOnce().find { it.name == oldName }?.let { playlist ->
                    val updated = playlist.copy(name = newName, updatedAt = System.currentTimeMillis())
                    database.playlistDao().updatePlaylist(updated)
                }
                withContext(Dispatchers.Main) {
                    val updatedPlaylists = database.playlistDao().getAllPlaylistsOnce()
                    playlists = updatedPlaylists.map { it.name to database.playlistDao().getPlaylistSongCount(it.id) }
                    // 同步详情视图状态
                    if (selectedPlaylistName == oldName) {
                        selectedPlaylistName = newName
                        playlistSongCache = playlistSongCache - oldName
                    }
                }
            }
        }

        // 歌曲变化时重新加载歌词
        LaunchedEffect(currentSong) {
            currentSong?.let { song ->
                Logger.i(TAG, "歌曲变化: ${song.title}, 路径: ${song.path}")
                lyrics = LrcParser.parseLrc(this@MainActivity, song.path, musicDirectoryUri)
            }
        }

        // 从LaunchedEffect中同步播放模式到UI状态
        LaunchedEffect(playerService) {
            while (playerService == null) {
                delay(100)
            }
            val service = playerService ?: return@LaunchedEffect
            playMode = service.getPlayerManager().playMode
        }

        PlayerScreen(
            currentSong = currentSong,
            isPlaying = isPlaying,
            playlist = playlist,
            librarySongs = libraryDisplaySongs,
            currentPosition = currentPosition,
            duration = duration,
            playMode = playMode,
            lyrics = lyrics,
            showPlaylistPanel = showPlaylistPanel,
            playlistTab = playlistTab,
            onPlayPause = { playerService?.getPlayerManager()?.playPause() },
            onNext = { playerService?.getPlayerManager()?.playNext() },
            onPrevious = { playerService?.getPlayerManager()?.playPrevious() },
            onSeek = { playerService?.getPlayerManager()?.seekTo(it) },
            onSongClick = { index ->
                playerService?.getPlayerManager()?.let { manager ->
                    manager.setPlaylist(playlist, index)
                    lyrics = null // 清空歌词，下次自动加载
                }
            },
            onPlayModeChange = {
                val newMode = playerService?.getPlayerManager()?.cyclePlayMode()
                newMode?.let { playMode = it }
            },
            onPlayPlaylistSongs = { songs, index ->
                playerService?.getPlayerManager()?.let { manager ->
                    manager.setPlaylist(songs, index)
                    lyrics = null
                }
            },
            onCenterViewToggle = { /* 在 PlayerScreen 内部处理 */ },
            onPlaylistToggle = { showPlaylistPanel = !showPlaylistPanel },
            onPlaylistTabChange = { playlistTab = it },
            onPlaylistDismiss = { showPlaylistPanel = false },
            onCreatePlaylist = { name ->
                activityScope.launch(Dispatchers.IO) {
                    Logger.i(TAG, "创建歌单开始: name=$name")
                    try {
                        val db = AppDatabase.getInstance(this@MainActivity)
                        Logger.d(TAG, "数据库实例获取成功，准备插入歌单")
                        val playlist = com.byd.mediaplayer.model.Playlist(name = name)
                        Logger.d(TAG, "Playlist对象创建成功: $playlist")
                        val id = db.playlistDao().insertPlaylist(playlist)
                        Logger.i(TAG, "歌单创建成功: name=$name, id=$id")
                        // 刷新歌单列表
                        withContext(Dispatchers.Main) {
                            val updatedPlaylists = db.playlistDao().getAllPlaylistsOnce()
                            playlists = updatedPlaylists.map { it.name to db.playlistDao().getPlaylistSongCount(it.id) }
                            Logger.d(TAG, "歌单列表已刷新，数量: ${playlists.size}")
                        }
                    } catch (e: Exception) {
                        Logger.e(TAG, "创建歌单失败: name=$name, error=${e.message}", e)
                    }
                }
            },
            onDeletePlaylist = { name ->
                activityScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getAllPlaylistsOnce().find { it.name == name }?.let {
                        database.playlistDao().clearPlaylist(it.id)
                        database.playlistDao().deletePlaylist(it)
                    }
                    // 刷新歌单列表
                    withContext(Dispatchers.Main) {
                        val updatedPlaylists = database.playlistDao().getAllPlaylistsOnce()
                        playlists = updatedPlaylists.map { it.name to database.playlistDao().getPlaylistSongCount(it.id) }
                        // 同步详情视图状态
                        if (selectedPlaylistName == name) {
                            selectedPlaylistName = null
                            selectedPlaylistSongs = emptyList()
                            playlistSongCache = playlistSongCache - name
                            libraryViewState = LibraryViewState.SONGS
                        }
                    }
                }
            },
            onAddSongsToPlaylist = { songs, playlistName ->
                activityScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    try {
                        val targetPlaylist = database.playlistDao().getPlaylistByName(playlistName)
                        if (targetPlaylist != null) {
                            val basePos = database.playlistDao().getPlaylistSongCount(targetPlaylist.id)
                            val playlistSongs = songs.mapIndexed { index, song ->
                                com.byd.mediaplayer.model.PlaylistSong(
                                    playlistId = targetPlaylist.id,
                                    songId = song.id,
                                    position = basePos + index
                                )
                            }
                            database.playlistDao().insertPlaylistSongs(playlistSongs)
                        }
                    } catch (e: Exception) {
                        Logger.e(TAG, "添加歌曲到歌单失败", e)
                    }
                }
            },
            onAddSongsToQueue = { songs ->
                playerService?.getPlayerManager()?.let { manager ->
                    val currentList = manager.playlist.toMutableList()
                    songs.forEach { song ->
                        if (song !in currentList) {
                            currentList.add(song)
                        }
                    }
                    manager.setPlaylist(currentList, manager.currentIndex)
                }
            },
            onDeleteSongsFromPlaylist = { indices ->
                playerService?.getPlayerManager()?.removeFromPlaylist(indices.toSet())
                playlist = playerService?.getPlayerManager()?.playlist ?: emptyList()
            },
            onRemoveSongFromPlaylist = { playlistName, index ->
                val libraryRef = librarySongs // 用歌曲库全量数据匹配
                activityScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getPlaylistByName(playlistName)?.let { playlistEntity ->
                        val playlistSongs = database.playlistDao().getPlaylistSongs(playlistEntity.id)
                        if (index in playlistSongs.indices) {
                            val songId = playlistSongs[index].songId
                            database.playlistDao().deletePlaylistSong(playlistEntity.id, songId)
                            // 刷新歌单列表
                            val updatedSongs = database.playlistDao().getPlaylistSongs(playlistEntity.id)
                            val sortedSongs = updatedSongs.sortedBy { it.position }.mapNotNull { ps ->
                                libraryRef.find { it.id == ps.songId }
                            }
                            withContext(Dispatchers.Main) {
                                selectedPlaylistSongs = sortedSongs
                                playlistSongCache = playlistSongCache + (playlistName to sortedSongs)
                            }
                        }
                    }
                }
            },
            onDeleteSongsFromLibrary = { songIds ->
                activityScope.launch(Dispatchers.IO) {
                    val repository = MusicRepository.getInstance(this@MainActivity)
                    repository.hideSongs(songIds)
                    // 从 PlayerManager 内部队列中移除已删除歌曲
                    val manager = playerService?.getPlayerManager()
                    manager?.let { mgr ->
                        val filteredPlaylist = mgr.playlist.filter { it.id !in songIds }
                        val currentSongWasDeleted = mgr.currentSong?.id?.let { it in songIds } ?: false
                        val newIndex = if (currentSongWasDeleted || filteredPlaylist.isEmpty()) {
                            0
                        } else {
                            val removedBefore = songIds.count { delId ->
                                mgr.playlist.take(mgr.currentIndex).any { it.id == delId }
                            }
                            (mgr.currentIndex - removedBefore).coerceIn(0, maxOf(0, filteredPlaylist.size - 1))
                        }
                        withContext(Dispatchers.Main) {
                            mgr.setPlaylist(filteredPlaylist, newIndex)
                            playlist = filteredPlaylist
                        }
                    }
                    withContext(Dispatchers.Main) {
                        val musicDirUri = preferencesManager.musicDirectoryUri
                        val newLibrary = if (musicDirUri != null) {
                            MediaStoreHelper.querySongsFromDirectory(this@MainActivity, Uri.parse(musicDirUri))
                        } else {
                            emptyList()
                        }
                        librarySongs = newLibrary
                        libraryDisplaySongs = newLibrary
                    }
                    Logger.i(TAG, "已从库中隐藏 ${songIds.size} 首歌曲")
                }
            },
            onClearPlaylist = {
                val repository = MusicRepository.getInstance(this@MainActivity)
                playerService?.getPlayerManager()?.setPlaylist(emptyList(), 0)
                playlist = emptyList() // 同步更新UI状态
                // 保存清空后的播放列表状态
                activityScope.launch(Dispatchers.IO) {
                    repository.saveCurrentPlaylist(emptyList(), 0, playMode.name)
                }
            },
            onAddToPlaylist = { song, playlistName ->
                activityScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    val targetPlaylist = database.playlistDao().getPlaylistByName(playlistName)
                    if (targetPlaylist != null) {
                        database.playlistDao().insertPlaylistSong(
                            com.byd.mediaplayer.model.PlaylistSong(
                                playlistId = targetPlaylist.id,
                                songId = song.id,
                                position = database.playlistDao().getPlaylistSongCount(targetPlaylist.id)
                            )
                        )
                        withContext(Dispatchers.Main) {
                            android.widget.Toast.makeText(
                                this@MainActivity,
                                "已添加 ${song.title} 到 ${targetPlaylist.name}",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            },
            onSearchQueryChange = { query ->
                searchQuery = query
                searchSongsRef?.invoke(query)
            },
            searchQuery = searchQuery,
            sortType = sortType,
            onSortTypeChange = { sortType = it },
            artists = artists,
            albums = albums,
            onArtistClick = { artistName ->
                selectedArtist = artistName
                selectedArtistSongs = libraryDisplaySongs.filter { it.artist == artistName }
                libraryViewState = LibraryViewState.ARTIST_SONGS
            },
            onAlbumClick = { albumName ->
                selectedAlbum = albumName
                selectedAlbumSongs = libraryDisplaySongs.filter { it.album == albumName }
                libraryViewState = LibraryViewState.ALBUM_SONGS
            },
            selectedArtist = selectedArtist,
            selectedAlbum = selectedAlbum,
            onBackFromArtist = {
                selectedArtist = null
                selectedArtistSongs = emptyList()
                libraryViewState = LibraryViewState.ARTIST_LIST
            },
            onBackFromAlbum = {
                selectedAlbum = null
                selectedAlbumSongs = emptyList()
                libraryViewState = LibraryViewState.ALBUM_LIST
            },
            onPlaylistClick = { name ->
                selectedPlaylistName = name
                // 从数据库加载歌单歌曲
                activityScope.launch(Dispatchers.IO) {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getPlaylistByName(name)?.let { playlistEntity ->
                        val playlistSongs = database.playlistDao().getPlaylistSongs(playlistEntity.id)
                        val songsInPlaylist = librarySongs.filter { it.id in playlistSongs.map { ps -> ps.songId } }
                        // 按position排序
                        val sortedSongs = playlistSongs.sortedBy { it.position }.mapNotNull { ps ->
                            songsInPlaylist.find { it.id == ps.songId }
                        }
                        withContext(Dispatchers.Main) {
                            selectedPlaylistSongs = sortedSongs
                            playlistSongCache = playlistSongCache + (name to sortedSongs)
                        }
                    }
                }
                libraryViewState = LibraryViewState.PLAYLIST_DETAIL
            },
            selectedPlaylistName = selectedPlaylistName,
            onBackFromPlaylist = {
                selectedPlaylistName = null
                selectedPlaylistSongs = emptyList()
                libraryViewState = LibraryViewState.SONGS
            },
            getPlaylistSongs = { name -> playlistSongCache[name] ?: emptyList() },
            onSetMusicDirectory = {
                openDirectoryPicker()
            },
            playlists = playlists,
            onRenamePlaylist = { oldName, newName -> renamePlaylist(oldName, newName) }
        )
    }
}