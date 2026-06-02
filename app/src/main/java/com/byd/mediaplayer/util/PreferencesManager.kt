package com.byd.mediaplayer.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "lf_media_player_prefs"
        private const val KEY_LAST_SONG_ID = "last_song_id"
        private const val KEY_LAST_POSITION = "last_position"
        private const val KEY_PLAY_MODE = "play_mode"
        private const val KEY_VOLUME = "volume"
        private const val KEY_PLAYLIST_PATH = "last_playlist_path"
    }
}