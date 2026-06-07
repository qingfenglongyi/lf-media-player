package com.byd.mediaplayer

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    private var playerService: PlayerService? = null
    private var serviceBound = false
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var audioManager: AudioManager

    // 播放器状态（类级别，供 UI 使用）
    private var _currentSong by mutableStateOf<Song?>(null)
    private var _isPlaying by mutableStateOf(false)
    private var _playlist by mutableStateOf<List<Song>>(emptyList())
    private var _currentPosition by mutableLongStateOf(0L)
    private var _duration by mutableLongStateOf(0L)
    private var _playMode by mutableStateOf(PlayMode.LIST_LOOP)
    private var _lyrics by mutableStateOf<Lyrics?>(null)
    private var _volume by mutableFloatStateOf(1.0f)
    private var _showPlaylistPanel by mutableStateOf(false)
    private var _playlistTab by mutableStateOf(PlaylistTab.PLAYING)
    private var _searchQuery by mutableStateOf("")
    private var _sortType by mutableStateOf(LibrarySortType.ALL)
    private var _playlists by mutableStateOf<List<String>>(emptyList())
    private var _artists by mutableStateOf<List<String>>(emptyList())
    private var _albums by mutableStateOf<List<String>>(emptyList())

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
            CoroutineScope(Dispatchers.Main).launch {
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
            Logger.d(TAG, "PlayerScreenWithState - 开始渲染")
            MaterialTheme {
                Logger.d(TAG, "PlayerScreenWithState - Surface前")
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Logger.d(TAG, "PlayerScreenWithState - 调用前")
                    PlayerScreenWithState()
                    Logger.d(TAG, "PlayerScreenWithState - 调用后")
                }
                Logger.d(TAG, "PlayerScreenWithState - Surface后")
            }
            Logger.d(TAG, "PlayerScreenWithState - MaterialTheme后")
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
            CoroutineScope(Dispatchers.IO).launch {
                repository.saveCurrentPlaylist(manager.playlist, manager.currentIndex, manager.playMode.name)
            }
        }
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

        if (allGranted) {
            bindToService()
        } else {
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

        val playerManager = service.getPlayerManager()

        // 如果已经有播放列表，不重新设置（避免切换应用后从头播放）
        if (playerManager.playlist.isNotEmpty()) {
            Logger.d(TAG, "播放列表已存在，不重新设置")
            // 手动触发状态同步
            playerManager.notifyListenersForStateSync()
            return
        }

        // 首次启动时不自动恢复播放列表
        if (preferencesManager.isFirstLaunch) {
            Logger.i(TAG, "首次启动，loadSongsAndStartPlay中跳过恢复播放状态")
            preferencesManager.isFirstLaunch = false
            return
        }

        val repository = MusicRepository.getInstance(this)

        CoroutineScope(Dispatchers.Main).launch {
            val songs = repository.getAllSongs()
            if (songs.isNotEmpty()) {
                // 尝试从数据库恢复播放列表
                val restored = repository.restoreCurrentPlaylist(songs)
                if (restored != null) {
                    val (restoredPlaylist, restoredIndex, playMode) = restored
                    playerManager.setPlaylist(restoredPlaylist, restoredIndex)
                    playerManager.setPlayMode(try { PlayMode.valueOf(playMode) } catch (e: Exception) { PlayMode.LIST_LOOP })
                    Logger.i(TAG, "从数据库恢复播放列表成功")
                } else {
                    // 没有保存的播放列表，使用旧的恢复逻辑
                    val lastSongId = preferencesManager.lastPlayedSongId
                    val startIndex = songs.indexOfFirst { it.id == lastSongId }.takeIf { it >= 0 } ?: 0
                    playerManager.setPlaylist(songs, startIndex)

                    val playMode = preferencesManager.lastPlayMode.let {
                        try { PlayMode.valueOf(it) } catch (e: Exception) { PlayMode.LIST_LOOP }
                    }
                    playerManager.setPlayMode(playMode)
                }

                // 恢复播放位置
                val lastPosition = preferencesManager.lastPlayedPosition
                if (lastPosition > 0) {
                    playerManager.seekTo(lastPosition)
                }
            }
        }
    }

    @Composable
    private fun PlayerScreenWithState() {
        // 使用remember和mutableStateOf创建响应式状态
        var currentSong by remember { mutableStateOf<Song?>(null) }
        var isPlaying by remember { mutableStateOf(false) }
        var playlist by remember { mutableStateOf<List<Song>>(emptyList()) }
        var librarySongs by remember { mutableStateOf<List<Song>>(emptyList()) }
        var currentPosition by remember { mutableLongStateOf(0L) }
        var duration by remember { mutableLongStateOf(0L) }
        var playMode by remember { mutableStateOf(PlayMode.LIST_LOOP) }
        var lyrics by remember { mutableStateOf<Lyrics?>(null) }
        var volume by remember { mutableFloatStateOf(1.0f) }
        var showPlaylistPanel by remember { mutableStateOf(false) }
        var playlistTab by remember { mutableStateOf(PlaylistTab.PLAYING) }
        var searchQuery by remember { mutableStateOf("") }
        var sortType by remember { mutableStateOf(LibrarySortType.ALL) }
        var playlists by remember { mutableStateOf<List<String>>(emptyList()) }
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

        Logger.d(TAG, "PlayerScreenWithState - 初始化完成，开始LaunchedEffect")

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
                    Logger.d(TAG, "加载音乐目录: $musicDirectoryUri")
                } catch (e: Exception) {
                    Logger.e(TAG, "解析音乐目录URI失败: ${e.message}")
                }
            }

            // 加载歌曲到歌曲库（只有设置了音乐目录才自动加载）
            if (musicDirectoryUri != null) {
                val allSongs = MediaStoreHelper.querySongsFromDirectory(this@MainActivity, musicDirectoryUri!!)
                librarySongs = allSongs
                playlist = allSongs
                Logger.i(TAG, "从目录加载歌曲完成: ${allSongs.size}首")
            } else {
                // 首次启动或未设置目录，不自动搜索
                librarySongs = emptyList()
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

            // 加载歌单
            val database = AppDatabase.getInstance(this@MainActivity)
            val playlistEntities = database.playlistDao().getAllPlaylistsOnce()
            playlists = playlistEntities.map { it.name }

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
                CoroutineScope(Dispatchers.Main).launch {
                    val currentDirUri = preferencesManager.musicDirectoryUri?.let { Uri.parse(it) }
                    val songs = if (currentDirUri != null) {
                        MediaStoreHelper.querySongsFromDirectory(this@MainActivity, currentDirUri)
                    } else {
                        emptyList()
                    }
                    playlist = if (query.isBlank()) {
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
            CoroutineScope(Dispatchers.IO).launch {
                val database = AppDatabase.getInstance(this@MainActivity)
                database.playlistDao().getAllPlaylistsOnce().find { it.name == oldName }?.let { playlist ->
                    val updated = playlist.copy(name = newName, updatedAt = System.currentTimeMillis())
                    database.playlistDao().updatePlaylist(updated)
                }
                withContext(Dispatchers.Main) {
                    val updatedPlaylists = database.playlistDao().getAllPlaylistsOnce()
                    playlists = updatedPlaylists.map { it.name }
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

        // 播放模式变化时更新状态
        LaunchedEffect(playerService) {
            val service = playerService ?: return@LaunchedEffect
            val manager = service.getPlayerManager()
            playMode = manager.playMode
        }

        PlayerScreen(
            currentSong = currentSong,
            isPlaying = isPlaying,
            playlist = playlist,
            librarySongs = librarySongs,
            currentPosition = currentPosition,
            duration = duration,
            playMode = playMode,
            lyrics = lyrics,
            volume = volume,
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
            onVolumeChange = { newVolume ->
                volume = newVolume
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume.toInt(), 0)
                preferencesManager.lastVolume = newVolume / maxVolume
            },
            onCenterViewToggle = { /* 在 PlayerScreen 内部处理 */ },
            onPlaylistToggle = { showPlaylistPanel = !showPlaylistPanel },
            onPlaylistTabChange = { playlistTab = it },
            onPlaylistDismiss = { showPlaylistPanel = false },
            onCreatePlaylist = { name ->
                CoroutineScope(Dispatchers.IO).launch {
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
                            playlists = updatedPlaylists.map { it.name }
                            Logger.d(TAG, "歌单列表已刷新，数量: ${playlists.size}")
                        }
                    } catch (e: Exception) {
                        Logger.e(TAG, "创建歌单失败: name=$name, error=${e.message}", e)
                    }
                }
            },
            onDeletePlaylist = { name ->
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getAllPlaylistsOnce().find { it.name == name }?.let {
                        database.playlistDao().deletePlaylist(it)
                    }
                    // 刷新歌单列表
                    withContext(Dispatchers.Main) {
                        val updatedPlaylists = database.playlistDao().getAllPlaylistsOnce()
                        playlists = updatedPlaylists.map { it.name }
                    }
                }
            },
            onAddSongsToPlaylist = { songs ->
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    try {
                        val playlist: com.byd.mediaplayer.model.Playlist = database.playlistDao().getAllPlaylists().first().first()
                        songs.forEachIndexed { index, song ->
                            database.playlistDao().insertPlaylistSong(
                                com.byd.mediaplayer.model.PlaylistSong(
                                    playlistId = playlist.id,
                                    songId = song.id,
                                    position = index
                                )
                            )
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
                val currentPlaylistRef = playlist // 在lambda外部捕获
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getPlaylistByName(playlistName)?.let { playlistEntity ->
                        val playlistSongs = database.playlistDao().getPlaylistSongs(playlistEntity.id)
                        if (index in playlistSongs.indices) {
                            val songId = playlistSongs[index].songId
                            database.playlistDao().deletePlaylistSong(playlistEntity.id, songId)
                            // 刷新歌单列表
                            val updatedSongs = database.playlistDao().getPlaylistSongs(playlistEntity.id)
                            val sortedSongs = updatedSongs.sortedBy { it.position }.mapNotNull { ps ->
                                currentPlaylistRef.find { it.id == ps.songId }
                            }
                            withContext(Dispatchers.Main) {
                                selectedPlaylistSongs = sortedSongs
                            }
                        }
                    }
                }
            },
            onDeleteSongsFromLibrary = { songIds ->
                CoroutineScope(Dispatchers.IO).launch {
                    val repository = MusicRepository.getInstance(this@MainActivity)
                    repository.hideSongs(songIds)
                    withContext(Dispatchers.Main) {
                        // 刷新歌曲库
                        librarySongs = repository.getAllSongs()
                        playlist = librarySongs
                    }
                    Logger.i(TAG, "已从库中隐藏 ${songIds.size} 首歌曲")
                }
            },
            onClearPlaylist = {
                val repository = MusicRepository.getInstance(this@MainActivity)
                playerService?.getPlayerManager()?.setPlaylist(emptyList(), 0)
                playlist = emptyList() // 同步更新UI状态
                // 保存清空后的播放列表状态
                CoroutineScope(Dispatchers.IO).launch {
                    repository.saveCurrentPlaylist(emptyList(), 0, playMode.name)
                }
            },
            onAddToPlaylist = { song ->
                CoroutineScope(Dispatchers.IO).launch {
                    // 获取所有歌单，让用户选择（这里简化为添加到第一个歌单）
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getAllPlaylists().collect { playlists ->
                        if (playlists.isNotEmpty()) {
                            val playlistId = playlists.first().id
                            database.playlistDao().insertPlaylistSong(
                                com.byd.mediaplayer.model.PlaylistSong(
                                    playlistId = playlistId,
                                    songId = song.id,
                                    position = 0
                                )
                            )
                            // 在主线程显示Toast
                            CoroutineScope(Dispatchers.Main).launch {
                                android.widget.Toast.makeText(
                                    this@MainActivity,
                                    "已添加 ${song.title} 到 ${playlists.first().name}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
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
                selectedArtistSongs = playlist.filter { it.artist == artistName }
                libraryViewState = LibraryViewState.ARTIST_SONGS
            },
            onAlbumClick = { albumName ->
                selectedAlbum = albumName
                selectedAlbumSongs = playlist.filter { it.album == albumName }
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
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.getInstance(this@MainActivity)
                    database.playlistDao().getPlaylistByName(name)?.let { playlistEntity ->
                        val playlistSongs = database.playlistDao().getPlaylistSongs(playlistEntity.id)
                        val songIds = playlistSongs.map { it.songId }
                        val songsInPlaylist = playlist.filter { it.id in songIds }.toMutableList()
                        // 按position排序
                        val sortedSongs = playlistSongs.sortedBy { it.position }.mapNotNull { ps ->
                            songsInPlaylist.find { it.id == ps.songId }
                        }
                        withContext(Dispatchers.Main) {
                            selectedPlaylistSongs = sortedSongs
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
            getPlaylistSongs = { name -> selectedPlaylistSongs },
            onSetMusicDirectory = {
                openDirectoryPicker()
            },
            playlists = playlists,
            onRenamePlaylist = { oldName, newName -> renamePlaylist(oldName, newName) }
        )
    }
}