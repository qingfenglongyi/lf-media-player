package com.byd.mediaplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 播放列表实体类
 * 存储在Room数据库中
 *
 * @param id 播放列表唯一ID，自增长
 * @param name 播放列表名称
 * @param createdAt 创建时间戳
 * @param updatedAt 最后更新时间戳
 */
@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,              // 播放列表ID
    val name: String,             // 播放列表名称
    val createdAt: Long = System.currentTimeMillis(),    // 创建时间
    val updatedAt: Long = System.currentTimeMillis()      // 更新时间
)

/**
 * 播放列表歌曲关联实体类
 * 用于建立播放列表与歌曲的多对多关系
 *
 * @param playlistId 播放列表ID
 * @param songId 歌曲ID
 * @param position 歌曲在播放列表中的位置（用于排序）
 * @param addedAt 添加时间戳
 */
@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"]  // 组合主键，防止重复添加同一歌曲
)
data class PlaylistSong(
    val playlistId: Long,                              // 播放列表ID
    val songId: Long,                                  // 歌曲ID
    val position: Int,                                 // 在列表中的位置
    val addedAt: Long = System.currentTimeMillis()    // 添加时间
)

/**
 * 歌曲实体类
 * 存储在Room数据库中，用于缓存歌曲信息
 *
 * @param id 歌曲ID（主键），对应MediaStore中的音频ID
 * @param title 歌曲标题
 * @param artist 艺术家
 * @param album 专辑
 * @param duration 时长（毫秒）
 * @param path 文件路径
 * @param lastPlayedAt 最后播放时间
 * @param isHidden 是否被隐藏（从歌曲库中移除但不删除文件）
 */
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: Long,                      // 歌曲ID
    val title: String,                 // 歌曲标题
    val artist: String,                // 艺术家
    val album: String,                 // 专辑
    val duration: Long,               // 时长
    val path: String,                  // 文件路径
    val lastPlayedAt: Long? = null,    // 最后播放时间
    val isHidden: Boolean = false      // 是否隐藏
)

/**
 * 带歌曲列表的播放列表（视图模型）
 * 用于同时传递播放列表及其包含的歌曲
 *
 * @param playlist 播放列表信息
 * @param songs 该播放列表包含的歌曲列表
 */
data class PlaylistWithSongs(
    val playlist: Playlist,           // 播放列表
    val songs: List<Song>             // 包含的歌曲
)

/**
 * 配置项实体类
 * 用于存储键值对形式的配置信息
 *
 * @param key 配置键名
 * @param value 配置值
 * @param updatedAt 更新时间
 */
@Entity(tableName = "config")
data class Config(
    @PrimaryKey
    val key: String,                              // 配置键
    val value: String,                            // 配置值
    val updatedAt: Long = System.currentTimeMillis()  // 更新时间
)