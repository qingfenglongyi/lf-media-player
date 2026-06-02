package com.byd.mediaplayer.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val path: String
)

enum class PlayMode {
    LIST_LOOP,
    SINGLE_LOOP,
    SHUFFLE
}

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playMode: PlayMode = PlayMode.LIST_LOOP,
    val playlist: List<Song> = emptyList(),
    val currentIndex: Int = -1
)