package com.byd.mediaplayer

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
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
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.player.PlayerService
import com.byd.mediaplayer.ui.PlayerScreen
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    private var playerService: PlayerService? = null
    private var serviceBound = false

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
        val repository = com.byd.mediaplayer.data.MusicRepository.getInstance(this)

        CoroutineScope(Dispatchers.Main).launch {
            val songs = repository.getAllSongs()
            if (songs.isNotEmpty()) {
                playerManager.setPlaylist(songs, 0)
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

        val scope = CoroutineScope(Dispatchers.Main)
        var updateJob by remember { mutableStateOf<Job?>(null) }

        LaunchedEffect(playerService) {
            while (playerService == null) {
                delay(100)
            }
            val service = playerService ?: return@LaunchedEffect
            val playerManager = service.getPlayerManager()

            // 加载歌曲
            val repository = com.byd.mediaplayer.data.MusicRepository.getInstance(this@MainActivity)
            playlist = repository.getAllSongs()

            if (playlist.isNotEmpty()) {
                playerManager.setPlaylist(playlist, 0)
            }

            // 更新播放状态
            launch {
                while (true) {
                    currentSong = playerManager.currentSong
                    isPlaying = playerManager.isPlaying
                    currentPosition = playerManager.currentPosition
                    duration = playerManager.duration
                    playlist = playerManager.playlist
                    delay(500)
                }
            }
        }

        val onPlayPause: () -> Unit = {
            playerService?.getPlayerManager()?.playPause()
        }

        val onNext: () -> Unit = {
            playerService?.getPlayerManager()?.playNext()
        }

        val onPrevious: () -> Unit = {
            playerService?.getPlayerManager()?.playPrevious()
        }

        val onSeek: (Long) -> Unit = { position ->
            playerService?.getPlayerManager()?.seekTo(position)
        }

        val onSongClick: (Int) -> Unit = { index ->
            playerService?.getPlayerManager()?.let { manager ->
                manager.setPlaylist(playlist, index)
            }
        }

        PlayerScreen(
            currentSong = currentSong,
            isPlaying = isPlaying,
            playlist = playlist,
            currentPosition = currentPosition,
            duration = duration,
            onPlayPause = onPlayPause,
            onNext = onNext,
            onPrevious = onPrevious,
            onSeek = onSeek,
            onSongClick = onSongClick
        )
    }
}