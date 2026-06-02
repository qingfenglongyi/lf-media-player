package com.byd.mediaplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioManager
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
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.player.PlayerService
import com.byd.mediaplayer.ui.LibraryViewState
import com.byd.mediaplayer.ui.LibrarySortType
import com.byd.mediaplayer.ui.PlaylistTab
import com.byd.mediaplayer.ui.PlayerScreen
import com.byd.mediaplayer.util.LrcParser
import com.byd.mediaplayer.util.PreferencesManager
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    private var playerService: PlayerService? = null
    private var serviceBound = false
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var audioManager: AudioManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerScreenWithState()
                }
            }
        }

        checkAndRequestPermissions()
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
        // 保存播放状态
        playerService?.getPlayerManager()?.let { manager ->
            preferencesManager.lastPlayedPosition = manager.currentPosition
            preferencesManager.lastPlayMode = manager.playMode.name
            manager.currentSong?.let {
                preferencesManager.lastPlayedSongId = it.id
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
        val repository = MusicRepository.getInstance(this)

        CoroutineScope(Dispatchers.Main).launch {
            val songs = repository.getAllSongs()
            if (songs.isNotEmpty()) {
                // 恢复上次的播放状态
                val lastSongId = preferencesManager.lastPlayedSongId
                val startIndex = songs.indexOfFirst { it.id == lastSongId }.takeIf { it >= 0 } ?: 0
                playerManager.setPlaylist(songs, startIndex)

                // 恢复播放模式
                val playMode = preferencesManager.lastPlayMode.let {
                    try { PlayMode.valueOf(it) } catch (e: Exception) { PlayMode.LIST_LOOP }
                }
                playerManager.setPlayMode(playMode)

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
        var currentSong by remember { mutableStateOf<Song?>(null) }
        var isPlaying by remember { mutableStateOf(false) }
        var playlist by remember { mutableStateOf<List<Song>>(emptyList()) }
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

        LaunchedEffect(playerService) {
            while (playerService == null) {
                delay(100)
            }
            val service = playerService ?: return@LaunchedEffect
            val manager = service.getPlayerManager()
            val repository = MusicRepository.getInstance(this@MainActivity)

            // 加载歌曲
            playlist = repository.getAllSongs()

            // 加载艺术家和专辑列表
            artists = MediaStoreHelper.getAllArtists(this@MainActivity)
            albums = MediaStoreHelper.getAllAlbums(this@MainActivity)

            // 加载歌单
            val database = AppDatabase.getInstance(this@MainActivity)
            database.playlistDao().getAllPlaylists().collect { playlistEntities ->
                playlists = playlistEntities.map { it.name }
            }

            if (playlist.isNotEmpty()) {
                val startIndex = preferencesManager.lastPlayedSongId.let { lastId ->
                    playlist.indexOfFirst { it.id == lastId }.takeIf { it >= 0 } ?: 0
                }
                manager.setPlaylist(playlist, startIndex)
            }

            // 音量
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = preferencesManager.lastVolume
            volume = currentVolume * maxVolume

            // 保存repository引用用于搜索
            val searchRepository = repository

            // 更新播放状态
            launch {
                while (true) {
                    val previousSong = currentSong
                    currentSong = manager.currentSong
                    isPlaying = manager.isPlaying
                    currentPosition = manager.currentPosition
                    duration = manager.duration
                    playMode = manager.playMode
                    playlist = manager.playlist

                    // 歌曲变化时重新加载歌词
                    if (currentSong != null && currentSong != previousSong) {
                        lyrics = LrcParser.parseLrc(currentSong.path)
                    }

                    delay(500)
                }
            }

            // 搜索函数
            val searchSongs: (String) -> Unit = { query ->
                CoroutineScope(Dispatchers.Main).launch {
                    playlist = if (query.isBlank()) {
                        searchRepository.getAllSongs()
                    } else {
                        searchRepository.searchSongs(query)
                    }
                }
            }

            // 保存搜索函数引用
            searchSongsRef = searchSongs
        }

        PlayerScreen(
            currentSong = currentSong,
            isPlaying = isPlaying,
            playlist = playlist,
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
            onPlayModeChange = { playerService?.getPlayerManager()?.cyclePlayMode() },
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
                    AppDatabase.getInstance(this@MainActivity).playlistDao()
                        .insertPlaylist(com.byd.mediaplayer.model.Playlist(name = name))
                }
            },
            onDeletePlaylist = { name ->
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getInstance(this@MainActivity).playlistDao()
                        .getAllPlaylists().collect { list ->
                            list.find { it.name == name }?.let {
                                AppDatabase.getInstance(this@MainActivity).playlistDao()
                                    .deletePlaylist(it)
                            }
                        }
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
                // TODO: 从数据库加载歌单歌曲
                selectedPlaylistSongs = emptyList()
                libraryViewState = LibraryViewState.PLAYLIST_DETAIL
            },
            selectedPlaylistName = selectedPlaylistName,
            onBackFromPlaylist = {
                selectedPlaylistName = null
                selectedPlaylistSongs = emptyList()
                libraryViewState = LibraryViewState.SONGS
            },
            getPlaylistSongs = { name -> selectedPlaylistSongs },
            viewState = libraryViewState
        )
    }
}