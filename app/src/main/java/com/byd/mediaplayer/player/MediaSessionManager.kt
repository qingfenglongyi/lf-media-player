package com.byd.mediaplayer.player

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.byd.mediaplayer.util.Logger

/**
 * 媒体会话管理器
 * 负责与系统媒体控制器交互，支持：
 * - 锁屏显示播放信息
 * - 车载/耳机线控按钮
 * - 通知栏媒体控件
 *
 * @param context Android上下文
 * @param playerManager 播放器管理器实例
 */
class MediaSessionManager(context: Context, private val playerManager: PlayerManager) {

    private val TAG = "MediaSessionManager"

    /** 媒体会话实例 */
    private val mediaSession: MediaSessionCompat

    init {
        mediaSession = MediaSessionCompat(context, "LFMediaPlayer").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    Logger.d(TAG, "MediaSession回调: onPlay")
                    playerManager.play()
                }
                override fun onPause() {
                    Logger.d(TAG, "MediaSession回调: onPause")
                    playerManager.pause()
                }
                override fun onSkipToNext() {
                    Logger.d(TAG, "MediaSession回调: onSkipToNext")
                    playerManager.playNext()
                }
                override fun onSkipToPrevious() {
                    Logger.d(TAG, "MediaSession回调: onSkipToPrevious")
                    playerManager.playPrevious()
                }
                override fun onSeekTo(pos: Long) {
                    Logger.d(TAG, "MediaSession回调: onSeekTo pos=$pos")
                    playerManager.seekTo(pos)
                }
            })
        }
    }

    /** 获取媒体会话实例 */
    fun getSession(): MediaSessionCompat = mediaSession

    /**
     * 更新播放状态
     * 用于同步到系统媒体服务，显示在锁屏和通知栏
     *
     * @param isPlaying 是否正在播放
     * @param position 当前播放位置（毫秒）
     */
    fun updatePlaybackState(isPlaying: Boolean, position: Long) {
        // 根据播放状态确定状态值
        val state = if (isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        // 设置播放状态
        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(state, position, 1f)  // 播放速率1.0
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                    PlaybackStateCompat.ACTION_SEEK_TO
                )
                .build()
        )
    }

    /**
     * 更新媒体元数据
     * 显示在锁屏和通知栏的歌曲信息
     *
     * @param song 歌曲信息
     */
    fun updateMetadata(song: com.byd.mediaplayer.model.Song) {
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
                .build()
        )
    }

    /** 激活媒体会话 */
    fun activeSession() {
        mediaSession.isActive = true
    }

    /** 释放媒体会话资源 */
    fun release() {
        mediaSession.release()
    }
}