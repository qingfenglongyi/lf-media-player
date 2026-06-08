package com.byd.mediaplayer.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * SharedPreferences管理器
 * 用于持久化存储用户偏好设置，如上次播放位置、播放模式等
 *
 * 使用方法：
 * val prefs = PreferencesManager(context)
 * prefs.lastPlayedSongId = songId  // 保存
 * val songId = prefs.lastPlayedSongId  // 读取
 */
class PreferencesManager(context: Context) {

    // 获取SharedPreferences实例
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 上次播放的歌曲ID
     * 用于恢复播放状态
     */
    var lastPlayedSongId: Long
        get() = prefs.getLong(KEY_LAST_SONG_ID, -1L)
        set(value) = prefs.edit { putLong(KEY_LAST_SONG_ID, value) }

    /**
     * 上次播放位置（毫秒）
     * 用于断点续播
     */
    var lastPlayedPosition: Long
        get() = prefs.getLong(KEY_LAST_POSITION, 0L)
        set(value) = prefs.edit { putLong(KEY_LAST_POSITION, value) }

    /**
     * 上次播放模式
     * 值为 PlayMode 枚举的name，如 "LIST_LOOP"
     */
    var lastPlayMode: String
        get() = prefs.getString(KEY_PLAY_MODE, "LIST_LOOP") ?: "LIST_LOOP"
        set(value) = prefs.edit { putString(KEY_PLAY_MODE, value) }

    /**
     * 上次音量（0.0 - 1.0）
     */
    var lastVolume: Float
        get() = prefs.getFloat(KEY_VOLUME, 1.0f)
        set(value) = prefs.edit { putFloat(KEY_VOLUME, value) }

    /**
     * 上次播放列表路径（已废弃，保留兼容性）
     */
    var lastPlaylistPath: String?
        get() = prefs.getString(KEY_PLAYLIST_PATH, null)
        set(value) = prefs.edit { putString(KEY_PLAYLIST_PATH, value) }

    /**
     * 用户选择的音乐目录URI（SAF方式）
     * 用于指定扫描音乐的范围
     */
    var musicDirectoryUri: String?
        get() = prefs.getString(KEY_MUSIC_DIRECTORY_URI, null)
        set(value) = prefs.edit { putString(KEY_MUSIC_DIRECTORY_URI, value) }

    /**
     * 是否为首次启动
     * 首次启动时不自动恢复播放状态，避免干扰用户
     */
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, value) }

    /**
     * 清除所有保存的偏好设置
     */
    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        /** SharedPreferences文件名 */
        private const val PREFS_NAME = "lf_media_player_prefs"

        /** 上次播放歌曲ID的键 */
        private const val KEY_LAST_SONG_ID = "last_song_id"

        /** 上次播放位置的键 */
        private const val KEY_LAST_POSITION = "last_position"

        /** 上次播放模式的键 */
        private const val KEY_PLAY_MODE = "play_mode"

        /** 音量的键 */
        private const val KEY_VOLUME = "volume"

        /** 上次播放列表路径的键 */
        private const val KEY_PLAYLIST_PATH = "last_playlist_path"

        /** 音乐目录URI的键 */
        private const val KEY_MUSIC_DIRECTORY_URI = "music_directory_uri"

        /** 首次启动标记的键 */
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }
}