package com.byd.mediaplayer.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song

class PlayerManager(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    var playlist: List<Song> = emptyList()
        private set

    var currentIndex: Int = -1
        private set

    var playMode: PlayMode = PlayMode.LIST_LOOP
        private set

    val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    val currentPosition: Long
        get() = exoPlayer.currentPosition

    val duration: Long
        get() = exoPlayer.duration

    val currentSong: Song?
        get() = if (currentIndex >= 0 && currentIndex < playlist.size) {
            playlist[currentIndex]
        } else null

    private val playerListeners = mutableListOf<PlayerListener>()

    interface PlayerListener {
        fun onPlaybackStateChanged(song: Song?, isPlaying: Boolean)
        fun onPositionChanged(position: Long, duration: Long)
    }

    fun addListener(listener: PlayerListener) {
        playerListeners.add(listener)
    }

    fun removeListener(listener: PlayerListener) {
        playerListeners.remove(listener)
    }

    private fun notifyListeners() {
        playerListeners.forEach { listener ->
            listener.onPlaybackStateChanged(currentSong, isPlaying)
            listener.onPositionChanged(currentPosition, duration)
        }
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        playlist = songs
        currentIndex = startIndex
        prepareAndPlay()
    }

    fun play() {
        exoPlayer.play()
        notifyListeners()
    }

    fun pause() {
        exoPlayer.pause()
        notifyListeners()
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun playNext(): Boolean {
        if (playlist.isEmpty()) return false

        currentIndex = when (playMode) {
            PlayMode.LIST_LOOP -> (currentIndex + 1) % playlist.size
            PlayMode.SINGLE_LOOP -> currentIndex
            PlayMode.SHUFFLE -> playlist.indices.random()
        }

        prepareAndPlay()
        return true
    }

    fun playPrevious(): Boolean {
        if (playlist.isEmpty()) return false

        currentIndex = when (playMode) {
            PlayMode.LIST_LOOP -> if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
            PlayMode.SINGLE_LOOP -> currentIndex
            PlayMode.SHUFFLE -> playlist.indices.random()
        }

        prepareAndPlay()
        return true
    }

    fun setPlayMode(mode: PlayMode) {
        playMode = mode
    }

    fun cyclePlayMode(): PlayMode {
        playMode = when (playMode) {
            PlayMode.LIST_LOOP -> PlayMode.SINGLE_LOOP
            PlayMode.SINGLE_LOOP -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.LIST_LOOP
        }
        return playMode
    }

    private fun prepareAndPlay() {
        if (currentIndex < 0 || currentIndex >= playlist.size) return

        val song = playlist[currentIndex]
        val mediaItem = MediaItem.fromUri(song.uri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        notifyListeners()
    }

    fun release() {
        exoPlayer.release()
    }

    fun getPlayer(): ExoPlayer = exoPlayer
}