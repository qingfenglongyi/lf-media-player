package com.byd.mediaplayer.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.byd.mediaplayer.model.PlayMode
import com.byd.mediaplayer.model.Song
import com.byd.mediaplayer.util.Logger

/**
 * 播放器核心管理类
 * 封装ExoPlayer，负责播放列表管理、播放控制、状态通知等功能
 *
 * 功能：
 * - 播放/暂停/上一曲/下一曲控制
 * - 播放模式切换（列表循环/单曲循环/随机）
 * - 播放列表管理（设置/删除歌曲）
 * - 播放状态监听
 *
 * 使用方式：
 * val playerManager = PlayerManager(context)
 * playerManager.setPlaylist(songs, 0)
 * playerManager.play()
 * playerManager.addListener(listener)  // 添加播放状态监听
 */
class PlayerManager(context: Context) {

    private val TAG = "PlayerManager"

    // ExoPlayer播放器实例
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    init {
        // 设置播放状态监听器
        exoPlayer.addListener(object : Player.Listener {
            /**
             * 播放/暂停状态变化回调
             * 当isPlaying状态改变时通知所有监听器
             */
            override fun onIsPlayingChanged(playing: Boolean) {
                Logger.d(TAG, "ExoPlayer isPlaying changed: $playing")
                notifyListeners()
            }

            /**
             * 播放状态变化回调
             * 当播放结束时自动播放下一首
             */
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    Logger.d(TAG, "播放结束，准备播放下一首")
                    playNext()
                }
            }
        })
    }

    // ============ 播放列表和状态属性 ============

    /** 当前播放列表（只读，外部不可直接修改） */
    var playlist: List<Song> = emptyList()
        private set

    /** 当前播放的歌曲索引（-1表示未播放） */
    var currentIndex: Int = -1
        private set

    /** 当前播放模式 */
    var playMode: PlayMode = PlayMode.LIST_LOOP
        private set

    /** 是否正在播放 */
    val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    /** 当前播放位置（毫秒） */
    val currentPosition: Long
        get() = exoPlayer.currentPosition

    /** 当前歌曲总时长（毫秒） */
    val duration: Long
        get() = exoPlayer.duration

    /** 当前播放的歌曲对象 */
    val currentSong: Song?
        get() = if (currentIndex >= 0 && currentIndex < playlist.size) {
            playlist[currentIndex]
        } else null

    // ============ 播放状态监听器 ============

    /** 播放器监听器列表 */
    private val playerListeners = mutableListOf<PlayerListener>()

    /**
     * 播放器监听器接口
     * 用于接收播放状态变化通知
     */
    interface PlayerListener {
        /** 播放状态变化回调
         * @param song 当前播放的歌曲，null表示无播放
         * @param isPlaying 是否正在播放
         */
        fun onPlaybackStateChanged(song: Song?, isPlaying: Boolean)

        /** 播放位置变化回调
         * @param position 当前播放位置（毫秒）
         * @param duration 总时长（毫秒）
         */
        fun onPositionChanged(position: Long, duration: Long)
    }

    /** 添加播放状态监听器 */
    fun addListener(listener: PlayerListener) {
        Logger.d(TAG, "添加监听器")
        playerListeners.add(listener)
    }

    /** 移除播放状态监听器 */
    fun removeListener(listener: PlayerListener) {
        Logger.d(TAG, "移除监听器")
        playerListeners.remove(listener)
    }

    /** 通知所有监听器当前播放状态 */
    private fun notifyListeners() {
        Logger.d(TAG, "通知监听器: isPlaying=$isPlaying, currentSong=${currentSong?.title}")
        playerListeners.forEach { listener ->
            listener.onPlaybackStateChanged(currentSong, isPlaying)
            listener.onPositionChanged(currentPosition, duration)
        }
    }

    /** 手动同步状态（用于恢复播放状态后） */
    fun notifyListenersForStateSync() {
        Logger.d(TAG, "手动同步状态: currentSong=${currentSong?.title}")
        notifyListeners()
    }

    // ============ 播放控制方法 ============

    /**
     * 设置播放列表并开始播放
     *
     * @param songs 歌曲列表
     * @param startIndex 起始播放索引，默认为0
     */
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        Logger.i(TAG, "设置播放列表: ${songs.size}首歌曲, startIndex=$startIndex")
        playlist = songs
        if (songs.isEmpty()) {
            // 空列表时停止播放
            currentIndex = -1
            exoPlayer.stop()
            notifyListeners()
            return
        }
        // 确保起始索引在有效范围内
        currentIndex = startIndex.coerceIn(0, songs.size - 1)
        prepareAndPlay()
    }

    /** 开始播放 */
    fun play() {
        Logger.i(TAG, "播放")
        exoPlayer.play()
        notifyListeners()
    }

    /** 暂停播放 */
    fun pause() {
        Logger.i(TAG, "暂停")
        exoPlayer.pause()
        notifyListeners()
    }

    /** 切换播放/暂停状态 */
    fun playPause() {
        Logger.i(TAG, "切换播放/暂停: isPlaying=$isPlaying")
        if (exoPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    /**
     * 跳转到指定位置
     * @param position 目标位置（毫秒）
     */
    fun seekTo(position: Long) {
        Logger.d(TAG, "跳转: position=$position")
        exoPlayer.seekTo(position)
    }

    /**
     * 播放下一曲
     * 根据播放模式决定下一曲的逻辑
     * @return 是否成功切换到下一曲
     */
    fun playNext(): Boolean {
        Logger.i(TAG, "下一曲: currentIndex=$currentIndex, playMode=$playMode")
        if (playlist.isEmpty()) {
            Logger.w(TAG, "播放列表为空")
            return false
        }

        // 根据播放模式计算下一曲索引
        currentIndex = when (playMode) {
            // 列表循环：(当前索引+1) % 列表大小
            PlayMode.LIST_LOOP -> (currentIndex + 1) % playlist.size
            // 单曲循环：索引不变
            PlayMode.SINGLE_LOOP -> currentIndex
            // 随机播放：随机选择
            PlayMode.SHUFFLE -> playlist.indices.random()
        }

        Logger.d(TAG, "下一曲索引: $currentIndex, 歌曲: ${currentSong?.title}")
        prepareAndPlay()
        return true
    }

    /**
     * 播放上一曲
     * 根据播放模式决定上一曲的逻辑
     * @return 是否成功切换到上一曲
     */
    fun playPrevious(): Boolean {
        Logger.i(TAG, "上一曲: currentIndex=$currentIndex, playMode=$playMode")
        if (playlist.isEmpty()) {
            Logger.w(TAG, "播放列表为空")
            return false
        }

        // 根据播放模式计算上一曲索引
        currentIndex = when (playMode) {
            // 列表循环：如果是第一首则跳到最后一首
            PlayMode.LIST_LOOP -> if (currentIndex > 0) currentIndex - 1 else playlist.size - 1
            // 单曲循环：索引不变
            PlayMode.SINGLE_LOOP -> currentIndex
            // 随机播放：随机选择
            PlayMode.SHUFFLE -> playlist.indices.random()
        }

        Logger.d(TAG, "上一曲索引: $currentIndex, 歌曲: ${currentSong?.title}")
        prepareAndPlay()
        return true
    }

    /**
     * 设置播放模式
     * @param mode 播放模式
     */
    fun setPlayMode(mode: PlayMode) {
        Logger.i(TAG, "设置播放模式: $mode")
        playMode = mode
    }

    /**
     * 从播放列表中移除指定索引的歌曲
     * 会自动调整当前播放索引
     *
     * @param indices 要删除的歌曲索引集合
     */
    fun removeFromPlaylist(indices: Set<Int>) {
        if (indices.isEmpty()) return
        Logger.i(TAG, "从播放列表删除: $indices")

        // 按索引降序排列，从后往前删除，避免索引变化影响
        val sortedIndices = indices.sortedDescending()
        val mutablePlaylist = playlist.toMutableList()
        var newCurrentIndex = currentIndex

        for (index in sortedIndices) {
            if (index in mutablePlaylist.indices) {
                mutablePlaylist.removeAt(index)
                // 调整当前播放索引
                when {
                    // 删除当前歌曲之前的歌曲，索引减1
                    index < newCurrentIndex -> newCurrentIndex--
                    // 删除当前播放的歌曲，需要重新计算索引
                    index == newCurrentIndex -> {
                        newCurrentIndex = minOf(newCurrentIndex, mutablePlaylist.size - 1)
                    }
                }
            }
        }

        if (mutablePlaylist.isEmpty()) {
            // 列表被清空，重置状态
            setPlaylist(emptyList(), 0)
        } else {
            playlist = mutablePlaylist
            currentIndex = newCurrentIndex.coerceIn(0, maxOf(0, mutablePlaylist.size - 1))
            notifyListeners()
        }
    }

    /**
     * 循环切换播放模式
     * LIST_LOOP -> SINGLE_LOOP -> SHUFFLE -> LIST_LOOP
     * @return 切换后的播放模式
     */
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

    /**
     * 准备并播放当前索引指向的歌曲
     * 内部方法，由其他方法调用
     */
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
        // 不在这里调用notifyListeners()，由onIsPlayingChanged回调处理
    }

    /**
     * 释放播放器资源
     * 调用后播放器将不可用
     */
    fun release() {
        Logger.d(TAG, "释放播放器资源")
        exoPlayer.release()
    }

    /** 获取底层ExoPlayer实例（用于特殊需求） */
    fun getPlayer(): ExoPlayer = exoPlayer
}