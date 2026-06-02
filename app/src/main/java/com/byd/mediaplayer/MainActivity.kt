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
import com.byd.mediaplayer.data.MusicRepository
import com.byd.mediaplayer.data.database.AppDatabase
import com.byd.mediaplayer.model.Lyrics
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.player.PlayerService
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

        LaunchedEffect(playerService) {
            while (playerService == null) {
                delay(100)
            }
            val service = playerService ?: return@LaunchedEffect
            val manager = service.getPlayerManager()

            // 加载歌曲
            val repository = MusicRepository.getInstance(this@MainActivity)
            playlist = repository.getAllSongs()

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

            // 更新播放状态
            launch {
                while (true) {
                    currentSong = manager.currentSong
                    isPlaying = manager.isPlaying
                    currentPosition = manager.currentPosition
                    duration = manager.duration
                    playMode = manager.playMode
                    playlist = manager.playlist

                    // 加载歌词
                    currentSong?.let { song ->
                        if (lyrics == null || lyrics?.lines.isNullOrEmpty()) {
                            lyrics = LrcParser.parseLrc(song.path)
                        }
                    }

                    delay(500)
                }
            }
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
            onPlaylistDismiss = { showPlaylistPanel = false }
        )
    }
}