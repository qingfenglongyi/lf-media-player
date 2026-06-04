package com.byd.mediaplayer.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.util.Logger

class PlayerManager(context: Context) {

    private val TAG = "PlayerManager"

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
        Logger.d(TAG, "添加监听器")
        playerListeners.add(listener)
    }

    fun removeListener(listener: PlayerListener) {
        Logger.d(TAG, "移除监听器")
        playerListeners.remove(listener)
    }

    private fun notifyListeners() {
        Logger.d(TAG, "通知监听器: isPlaying=$isPlaying, currentSong=${currentSong?.title}")
        playerListeners.forEach { listener ->
            listener.onPlaybackStateChanged(currentSong, isPlaying)
            listener.onPositionChanged(currentPosition, duration)
        }
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        Logger.i(TAG, "设置播放列表: ${songs.size}首歌曲, startIndex=$startIndex")
        playlist = songs
        if (songs.isEmpty()) {
            currentIndex = -1
            exoPlayer.stop()
            notifyListeners()
            return
        }
        currentIndex = startIndex.coerceIn(0, songs.size - 1)
        prepareAndPlay()
    }

    fun play() {
        Logger.i(TAG, "播放")
        exoPlayer.play()
        notifyListeners()
    }

    fun pause() {
        Logger.i(TAG, "暂停")
        exoPlayer.pause()
        notifyListeners()
    }

    fun playPause() {
        Logger.i(TAG, "切换播放/暂停: isPlaying=$isPlaying")
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun seekTo(position: Long) {
        Logger.d(TAG, "跳转: position=$position")
        exoPlayer.seekTo(position)
    }

    fun playNext(): Boolean {
        Logger.i(TAG, "下一曲: currentIndex=$currentIndex, playMode=$playMode")
        if (playlist.isEmpty()) {
            Logger.w(TAG, "播放列表为空")
            return false
        }

        currentIndex = when (playMode) {
            PlayMode.LIST_LOOP -> (currentIndex + 1) % playlist.size
            PlayMode.SINGLE_LOOP -> currentIndex
            PlayMode.SHUFFLE -> playlist.indices.random()
        }

        Logger.d(TAG, "下一曲索引: $currentIndex, 歌曲: ${currentSong?.title}")
        prepareAndPlay()
        return true
    }

    fun playPrevious(): Boolean {
        Logger.i(TAG, "上一曲: currentIndex=$currentIndex, playMode=$playMode")
        if (playlist.isEmpty()) {
            Logger.w(TAG, "播放列表为空")
            return false
        }

        currentIndex = when (playMode) {
            PlayMode.LIST_LOOP -> if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
            PlayMode.SINGLE_LOOP -> currentIndex
            PlayMode.SHUFFLE -> playlist.indices.random()
        }

        Logger.d(TAG, "上一曲索引: $currentIndex, 歌曲: ${currentSong?.title}")
        prepareAndPlay()
        return true
    }

    fun setPlayMode(mode: PlayMode) {
        Logger.i(TAG, "设置播放模式: $mode")
        playMode = mode
    }

    fun cyclePlayMode(): PlayMode {
        val oldMode = playMode
        playMode = when (playMode) {
            PlayMode.LIST_LOOP -> PlayMode.SINGLE_LOOP
            PlayMode.SINGLE_LOOP -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.LIST_LOOP
        }
        Logger.i(TAG, "循环切换播放模式: $oldMode -> $playMode")
        return playMode
    }

    private fun prepareAndPlay() {
        if (currentIndex < 0 || currentIndex >= playlist.size) {
            Logger.w(TAG, "无效的播放索引: $currentIndex")
            return
        }

        val song = playlist[currentIndex]
        Logger.d(TAG, "准备播放: ${song.title}, uri: ${song.uri}")
        val mediaItem = MediaItem.fromUri(song.uri)

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        notifyListeners()
    }

    fun release() {
        Logger.d(TAG, "释放播放器资源")
        exoPlayer.release()
    }

    fun getPlayer(): ExoPlayer = exoPlayer
}