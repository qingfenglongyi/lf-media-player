package com.byd.mediaplayer.model

import android.net.Uri

/**
 * 歌曲数据类
 * 表示一首音乐文件的基本信息
 *
 * @param id 歌曲的唯一标识符，通常来自MediaStore的音频ID
 * @param title 歌曲标题，从音频文件元数据中读取
 * @param artist 艺术家/歌手名称
 * @param album 专辑名称
 * @param duration 歌曲时长，单位为毫秒
 * @param uri 歌曲文件的Content URI，用于播放
 * @param path 歌曲文件的完整路径，用于定位歌词等关联文件
 */
data class Song(
    val id: Long,        // 歌曲唯一标识
    val title: String,   // 歌曲标题
    val artist: String,  // 艺术家名称
    val album: String,   // 专辑名称
    val duration: Long,  // 时长（毫秒）
    val uri: Uri,        // 播放用URI
    val path: String     // 文件路径（用于歌词匹配等）
)

/**
 * 播放模式枚举
 * 定义播放列表的循环方式
 */
enum class PlayMode {
    /** 列表循环：播完最后一首后回到第一首 */
    LIST_LOOP,
    /** 单曲循环：重复播放当前歌曲 */
    SINGLE_LOOP,
    /** 随机播放：随机选择下一首 */
    SHUFFLE
}

/**
 * 播放器状态数据类
 * 用于保存和传递播放器的完整状态信息
 *
 * @param currentSong 当前播放的歌曲
 * @param isPlaying 是否正在播放
 * @param currentPosition 当前播放位置（毫秒）
 * @param duration 当前歌曲总时长（毫秒）
 * @param playMode 当前播放模式
 * @param playlist 当前播放列表
 * @param currentIndex 当前播放的歌曲在列表中的索引
 */
data class PlayerState(
    val currentSong: Song? = null,     // 当前播放的歌曲
    val isPlaying: Boolean = false,    // 是否正在播放
    val currentPosition: Long = 0L,    // 当前播放位置（毫秒）
    val duration: Long = 0L,          // 当前歌曲总时长
    val playMode: PlayMode = PlayMode.LIST_LOOP,  // 播放模式
    val playlist: List<Song> = emptyList(),       // 播放列表
    val currentIndex: Int = -1         // 当前播放索引
)