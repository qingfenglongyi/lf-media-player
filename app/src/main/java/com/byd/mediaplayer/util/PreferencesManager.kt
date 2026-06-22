package com.byd.mediaplayer.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import org.json.JSONObject
import java.io.File

/**
 * SharedPreferences管理器
 * 用于持久化存储用户偏好设置，如上次播放位置、播放模式等
 * 支持导出/导入到外部存储，实现卸载后数据保留
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** 外部存储备份文件 */
    private val backupFile: File = File(
        Environment.getExternalStorageDirectory(),
        "lf_media_player/prefs_backup.json"
    )

    var lastPlayedSongId: Long
        get() = prefs.getLong(KEY_LAST_SONG_ID, -1L)
        set(value) = prefs.edit { putLong(KEY_LAST_SONG_ID, value) }

    var lastPlayedPosition: Long
        get() = prefs.getLong(KEY_LAST_POSITION, 0L)
        set(value) = prefs.edit { putLong(KEY_LAST_POSITION, value) }

    var lastPlayMode: String
        get() = prefs.getString(KEY_PLAY_MODE, "LIST_LOOP") ?: "LIST_LOOP"
        set(value) = prefs.edit { putString(KEY_PLAY_MODE, value) }

    var lastVolume: Float
        get() = prefs.getFloat(KEY_VOLUME, 1.0f)
        set(value) = prefs.edit { putFloat(KEY_VOLUME, value) }

    var lastPlaylistPath: String?
        get() = prefs.getString(KEY_PLAYLIST_PATH, null)
        set(value) = prefs.edit { putString(KEY_PLAYLIST_PATH, value) }

    var musicDirectoryUri: String?
        get() = prefs.getString(KEY_MUSIC_DIRECTORY_URI, null)
        set(value) = prefs.edit { putString(KEY_MUSIC_DIRECTORY_URI, value) }

    var musicDirectoryPath: String?
        get() = prefs.getString(KEY_MUSIC_DIRECTORY_PATH, null)
        set(value) = prefs.edit { putString(KEY_MUSIC_DIRECTORY_PATH, value) }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, value) }

    fun clear() {
        prefs.edit { clear() }
    }

    /** 将偏好设置导出到外部存储（卸载不会被删除） */
    fun exportToExternalStorage() {
        try {
            val dir = backupFile.parentFile
            if (dir == null || (!dir.exists() && !dir.mkdirs())) {
                Logger.w(TAG, "外部存储目录不可用，跳过导出")
                return
            }

            val json = JSONObject().apply {
                put(KEY_LAST_SONG_ID, lastPlayedSongId)
                put(KEY_LAST_POSITION, lastPlayedPosition)
                put(KEY_PLAY_MODE, lastPlayMode)
                put(KEY_VOLUME, lastVolume.toDouble())
                put(KEY_PLAYLIST_PATH, lastPlaylistPath ?: "")
                put(KEY_MUSIC_DIRECTORY_URI, musicDirectoryUri ?: "")
                put(KEY_MUSIC_DIRECTORY_PATH, musicDirectoryPath ?: "")
                put(KEY_FIRST_LAUNCH, isFirstLaunch)
            }
            backupFile.writeText(json.toString())
            Logger.d(TAG, "偏好设置已导出到外部存储")
        } catch (e: Exception) {
            Logger.e(TAG, "导出偏好设置失败: ${e.message}")
        }
    }

    /** 从外部存储恢复偏好设置（仅在重装后内部prefs为默认值时恢复） */
    fun importFromExternalStorage() {
        if (!backupFile.exists()) return
        try {
            val json = JSONObject(backupFile.readText())
            // 仅在重装场景（内部prefs为默认值）时恢复
            if (prefs.getLong(KEY_LAST_SONG_ID, -1L) == -1L
                && json.optLong(KEY_LAST_SONG_ID, -1L) != -1L) {
                prefs.edit {
                    putLong(KEY_LAST_SONG_ID, json.optLong(KEY_LAST_SONG_ID, -1L))
                    putLong(KEY_LAST_POSITION, json.optLong(KEY_LAST_POSITION, 0L))
                    putString(KEY_PLAY_MODE, json.optString(KEY_PLAY_MODE, "LIST_LOOP"))
                    putFloat(KEY_VOLUME, json.optDouble(KEY_VOLUME, 1.0).toFloat())
                    putString(KEY_PLAYLIST_PATH, json.optString(KEY_PLAYLIST_PATH).ifEmpty { null })
                    putString(KEY_MUSIC_DIRECTORY_URI, json.optString(KEY_MUSIC_DIRECTORY_URI).ifEmpty { null })
                    putString(KEY_MUSIC_DIRECTORY_PATH, json.optString(KEY_MUSIC_DIRECTORY_PATH).ifEmpty { null })
                    putBoolean(KEY_FIRST_LAUNCH, json.optBoolean(KEY_FIRST_LAUNCH, true))
                }
                Logger.i(TAG, "从外部存储恢复了偏好设置")
            }
        } catch (e: Exception) {
            Logger.e(TAG, "恢复偏好设置失败: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "PreferencesManager"
        private const val PREFS_NAME = "lf_media_player_prefs"

        private const val KEY_LAST_SONG_ID = "last_song_id"
        private const val KEY_LAST_POSITION = "last_position"
        private const val KEY_PLAY_MODE = "play_mode"
        private const val KEY_VOLUME = "volume"
        private const val KEY_PLAYLIST_PATH = "last_playlist_path"
        private const val KEY_MUSIC_DIRECTORY_URI = "music_directory_uri"
        private const val KEY_MUSIC_DIRECTORY_PATH = "music_directory_path"
        private const val KEY_FIRST_LAUNCH = "first_launch"
    }
}
